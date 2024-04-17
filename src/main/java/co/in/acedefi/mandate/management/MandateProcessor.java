package co.in.acedefi.mandate.management;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import co.in.acedefi.mandate.management.enums.ContractEvents;
import co.in.acedefi.mandate.management.enums.MandateType;
import co.in.acedefi.mandate.management.enums.PurposeCode;
import co.in.acedefi.mandate.management.models.Mandate;
import co.in.acedefi.mandate.management.models.MandateTransaction;
import co.in.acedefi.mandate.management.utils.MandateUtils;

@Contract(
    name = "MandateProcessor",
    info =
        @Info(
            title = "MandateProcessor Contract",
            description = "Contract for processing mandates",
            version = "0.0.1-SNAPSHOT",
            license =
                @License(
                    name = "Enterprise License",
                    url = "www.acedefi.co.in"),
            contact =
                @Contact(
                    email = "manu.kumar@acedefi.co.in",
                    name = "Manu Kumar",
                    url = "www.acedefi.co.in")))

@Default
public class MandateProcessor implements ContractInterface {

    private enum MandateErrors {
        MANDATE_NOT_FOUND,
        MANDATE_ALREADY_EXISTS,
        INVALID_MANDATE,
        GENERIC_PROCESSING_ERROR
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

/**
 * Reads a mandate from the ledger.
 *
 * This method retrieves a mandate from the ledger based on the UMRN number.
 * Subclasses should override this method to customize the behavior of mandate retrieval.
 *
 * @param uniqueMandateReferenceNumber UMRN on the mandate.
 * @return The retrieved mandate.
 */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Mandate readMandate(final Context ctx, final String uniqueMandateReferenceNumber) {

        try {

            ChaincodeStub stub = ctx.getStub();
            String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

            if (mandateJSON == null || mandateJSON.isEmpty()) {
                String errorMessage = String.format("Mandate %s does not exist", uniqueMandateReferenceNumber);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
            }

            return Mandate.fromJSONString(mandateJSON);

        } catch (Exception e) {
            throw new ChaincodeException(e.getMessage(), MandateErrors.GENERIC_PROCESSING_ERROR.toString());
        }

    }

/**
 * Creates a mandate on the blockchain ledger
 *
 * This method takes input as the various parameters through a custom class for creating a mandate. validates those parameters
 * and creates a mandate.
 * @param Mandate Object
 * @return The saved mandate on the blockchain.
 */
    // public Mandate createMandate(final Context ctx, final Mandate mandate) {
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Mandate createMandate(final Context ctx, final String accountNumber, final String accountIfsc,
        final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount,
        final String type, final String frequency, final String merchantIdentifier, final String customerId,
        final String purposeCode) {

            ChaincodeStub stub = ctx.getStub();

            if (mandateExists(ctx, uniqueMandateReferenceNumber)) {
                String errorMessage = String.format("Mandate %s already exists", uniqueMandateReferenceNumber);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
            }

            // Mandate mandate = new Mandate(accountNumber, accountIfsc, uniqueMandateReferenceNumber, mandateExpiry, amount,0,
            // MandateType.valueOf(type),null,MandateFrequency.valueOf(frequency),customerName,customerId,PurposeCode.valueOf(purposeCode));

            Mandate mandate =  new Mandate.Builder()
            .accountNumber(accountNumber)
            .accountIfsc(accountIfsc)
            .uniqueMandateReferenceNumber(uniqueMandateReferenceNumber)
            .mandateExpiry(mandateExpiry)
            .amount(Long.valueOf(amount))
            .type(type)
            .utilization(0)
            .lastTransactionDate("01-01-1900")
            .frequency(frequency)
            .merchantIdentifier(merchantIdentifier)
            .customerId(customerId)
            .purposeCode(purposeCode)
            .build();

            if (validateMandateData(mandate)) {
                stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
            }
            return mandate;
        }


    private boolean validateMandateData(final Mandate mandate) {

        try {

            if (MandateUtils.stringinListIsNullOrEmpty(new ArrayList<>(Arrays.asList(mandate.getAccountNumber(),
                mandate.getUniqueMandateReferenceNumber(), mandate.getType().toString(), mandate.getFrequency().toString(),
                mandate.getCustomerId(), mandate.getPurposeCode().toString())))) {

                    String errorMessage = "Mandatory fields like AccountNumber, UniqueMandateReferenceNumber, Mandate Type, "
                    + "Mandate Frequency, customer id and purspose code are required";
                    System.out.println(errorMessage);
                    throw new ChaincodeException(errorMessage, MandateErrors.INVALID_MANDATE.toString());
            }
            if (LocalDate.parse(mandate.getMandateExpiry(), dateTimeFormatter) == null) {

                throw new ChaincodeException(printErrorString("Mandate expiry date needs to be provided ",
                    mandate.getUniqueMandateReferenceNumber()), MandateErrors.INVALID_MANDATE.toString());
            }
        } catch (DateTimeParseException e) {

            throw new ChaincodeException(printErrorString("Invalid date format provided in the mandate",
                    mandate.getUniqueMandateReferenceNumber()), MandateErrors.INVALID_MANDATE.toString());
        }
        return true;
    }
/**
 * Checks whether a mandate exists on te input ledger
 * This method takes input as the the UMRN and validates if the mandate exists.
 * @param uniqueMandateReferenceNumber UMRN on the mandate.
 * @return Boolean if it exists or not.
 */

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean mandateExists(final Context ctx, final String uniqueMandateReferenceNumber) {
        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        return (mandateJSON != null && !mandateJSON.isEmpty());
    }

/**
 * Retrieves a mandate from the ledger
 * This method takes input as the the UMRN and retrieves the mandate.
 * @param uniqueMandateReferenceNumber UMRN on the mandate.
 * @return Mandate object
 */


    private Mandate getMandate(final Context ctx, final String uniqueMandateReferenceNumber) {
        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        if (mandateJSON != null && !mandateJSON.isEmpty()) {
            return Mandate.fromJSONString(mandateJSON);
        }
        throw new ChaincodeException("Mandate Not found", MandateErrors.MANDATE_NOT_FOUND.toString());
    }

    // @Transaction(intent = Transaction.TYPE.SUBMIT)
    // public Mandate updateMandate(final Context ctx, final String accountNumber, final String accountIfsc,
    //     final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount,
    //     final String type,final String frequency,final String customerName,final String customerId,
    //     final String purposeCode) {


    // }

/**
 * Approves a transaction if it matches with the mandate parameters
 * This method takes input various parameters of a transaction
 * fromAccount
 * @param uniqueMandateReferenceNumber UMRN on the mandate.
 * @param purposeCode purpose code of the txn
 * @param amount amount of the txn
 * @param date - date of the txn
 * @return MandateTransaction object
 */

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public MandateTransaction approveTransaction(final Context ctx, final String fromAccount,
        final String uniqueMandateReferenceNumber, final String purposeCode, final long amount,
        final String date) {

        try {
            System.out.println("INSIDE APPROVE TRANSACTION");
            Mandate mandate = getMandate(ctx, uniqueMandateReferenceNumber);
            MandateTransaction mandateTransaction = new MandateTransaction(fromAccount, uniqueMandateReferenceNumber,
                PurposeCode.valueOf(purposeCode), amount, date);
            if (mandate == null) {
                // String errorMessage = String.format("Mandate %s does not exist", uniqueMandateReferenceNumber);
                // System.out.println(errorMessage);
                throw new ChaincodeException(printErrorString("Mandate %s does not exist", uniqueMandateReferenceNumber),
                    MandateErrors.MANDATE_NOT_FOUND.toString());
            }
            if (validateUtilizationData(mandate, mandateTransaction)) {
                updateMandate(ctx, mandate, mandateTransaction);
                return mandateTransaction;
            } else {
                throw new ChaincodeException(printErrorString("Mandate Validations failed", uniqueMandateReferenceNumber),
                    MandateErrors.INVALID_MANDATE.toString());
            }
        } catch (Exception e) {
            throw new ChaincodeException(e.getMessage(), MandateErrors.GENERIC_PROCESSING_ERROR.toString());
        }

    }

    private Boolean validateUtilizationData(final Mandate mandate, final MandateTransaction mandateTransaction) {


        if (MandateUtils.stringIsNullOrEmpty(mandateTransaction.getDate())
        || MandateUtils.longIsNullOrZero(mandateTransaction.getAmount())) {
                throw new ChaincodeException(printErrorString("Invalid input data %s or %d", mandateTransaction.getDate(),
                    mandateTransaction.getAmount(), MandateErrors.MANDATE_NOT_FOUND.toString()));
        }

        if (LocalDate.parse(mandateTransaction.getDate(), dateTimeFormatter)
            .isAfter(LocalDate.parse(mandate.getMandateExpiry(), dateTimeFormatter))) {
                throw new ChaincodeException(printErrorString("Mandate has expired on %s", mandate.getMandateExpiry()),
                    MandateErrors.INVALID_MANDATE.toString());
        }
        if (mandate.getType() == MandateType.FIXED && mandateTransaction.getAmount() != mandate.getAmount()) {
            throw new ChaincodeException(printErrorString("Mandate type is fixed and transaction amount %d does "
            + " match the mandate amount", mandate.getAmount()), MandateErrors.INVALID_MANDATE.toString());
        }
        return true;

    }
    /*
     * if (transactiondate-last transaction date) >= Frequency days and transaction.amount <=mandate.amount)
     *      then approve transaction and update mandate uqilizaiton = transaction amount and update last transaction date = current date
       else if (transactiondate-last transaction date) - Frequency days  and transac.amount<= mandate.amount-utilization)
            then approve transaction and update mandate uqilizaiton = mandate.utilization+ transaction amount and update last transaction date = current date
     */

    private Mandate updateMandate(final Context ctx, final Mandate mandate, final MandateTransaction mandateTransaction) {

        long daysBetween = ChronoUnit.DAYS.between(LocalDate.parse(mandateTransaction.getDate(), dateTimeFormatter),
            LocalDate.parse(mandate.getLastTransactionDate(), dateTimeFormatter));
        if (daysBetween >= mandate.getFrequency().getValue() && mandateTransaction.getAmount() <= mandate.getAmount()) {
            return updateMandateUtilizationAndLastTransactionDate(ctx, mandate, mandateTransaction.getAmount(),
            mandateTransaction.getDate());
        } else if (daysBetween < mandate.getFrequency().getValue()
        && mandateTransaction.getAmount() <= mandate.getAmount() - mandate.getUtilization()) {
            return updateMandateUtilizationAndLastTransactionDate(ctx, mandate, mandate.getUtilization() + mandateTransaction.getAmount(),
            mandateTransaction.getDate());
        } else {
            throw new ChaincodeException(printErrorString("Error while updating mandate %s to ledger ",
            mandate.getUniqueMandateReferenceNumber()), MandateErrors.INVALID_MANDATE.toString());
        }

    }

    private Mandate updateMandateUtilizationAndLastTransactionDate(final Context ctx, final Mandate mandate, final long transactionAmount,
        final String lastTransactionDate) {

        ChaincodeStub stub = ctx.getStub();
        mandate.setLastTransactionDate(lastTransactionDate);
        mandate.setUtilization(transactionAmount);
        stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
        return mandate;

    }

/**
 * updates the utilization and the last transaction date of a mandate.
 *
 * This method takes input as the the UMRN, transaction amount and transaction date. Updates the mandate on the
 * ledger with this info
 * @param uniqueMandateReferenceNumber UMRN on the mandate.
 * @param transactionAmount
 * @param lastTransactionDate
 * @return Mandate object
 */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Mandate updateMandateUtilizationAndLastTransactionDate(final Context ctx, final String uniqueMandateReferenceNumber,
        final long transactionAmount, final String lastTransactionDate) {

        Mandate mandate = readMandate(ctx, uniqueMandateReferenceNumber);
        ChaincodeStub stub = ctx.getStub();
        mandate.setLastTransactionDate(lastTransactionDate);
        mandate.setUtilization(transactionAmount);
        stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
        return mandate;

    }

    private String printErrorString(final String errorString, final Object... args) {
            String errorMessage = String.format(errorString, args);
        System.out.println(errorMessage);
        return errorMessage;
    }

/**
 * Creates mandates in bulk
 *
 * This method takes input a String [] of json objects each representing a mandate, in a loop converts them to a
 * mandate object and then puts them in the ledger
 * @param mandateJsonArray UMRN on the mandate.
 * @return Array of mandate objects
 */

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public List<Mandate> batchCreateMandate(final Context ctx, final String mandateJsonArray) {

        System.out.println("Inside Mandate Function" + mandateJsonArray);
        if (MandateUtils.stringIsNullOrEmpty(mandateJsonArray)) {
            throw new ChaincodeException(printErrorString("Mandate cant be null or empty ",
                new Object()), MandateErrors.INVALID_MANDATE.toString());
        }

        List<Mandate> mandateCreatedArray = new ArrayList<Mandate>();
        ChaincodeStub stub = ctx.getStub();
        for (String mandateJson : MandateUtils.convertStringToList(mandateJsonArray)) {
            System.out.println("Inside for loop before mandate creation" + mandateJson);
            Mandate mandate = Mandate.fromJSONString(mandateJson);
        // for (Mandate mandate : mandateJsonArray) {
            System.out.println("Inside for loop");
            try {
                if (validateMandateData(mandate)) {
                    stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
                    stub.setEvent(ContractEvents.MANDATE_CREATED.getValue(), mandate.toJSONString().getBytes(UTF_8));
                }
            } catch (Exception e) {
                mandate.setType(MandateType.NOT_CREATED);
                stub.setEvent(ContractEvents.MANDATE_CREATION_FAILED.getValue(), mandate.toJSONString().getBytes(UTF_8));
            }
            mandateCreatedArray.add(mandate);
        }
        return mandateCreatedArray;

    }

}
