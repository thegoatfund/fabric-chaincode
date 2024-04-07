package co.in.acedefi.mandate.management;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

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

import co.in.acedefi.mandate.management.enums.MandateFrequency;
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
public class MandateProcessor implements ContractInterface{

    private enum MandateErrors {
        MANDATE_NOT_FOUND,
        MANDATE_ALREADY_EXISTS,
        INVALID_MANDATE
    }

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-mm-yyyy");

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Mandate ReadMandate(final Context ctx, final String uniqueMandateReferenceNumber) {

        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        if (mandateJSON == null || mandateJSON.isEmpty()) {
            String errorMessage = String.format("Mandate %s does not exist", uniqueMandateReferenceNumber);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
        }

        return Mandate.fromJSONString(mandateJSON);

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Mandate createMandate(final Context ctx, final String accountNumber, final String accountIfsc,
        final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount, 
        final String type,final String frequency,final String customerName,final String customerId, 
        final String purposeCode) {

            ChaincodeStub stub = ctx.getStub();

            if (mandateExists(ctx, uniqueMandateReferenceNumber)) {
                String errorMessage = String.format("Mandate %s already exists", uniqueMandateReferenceNumber);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
            }
    
            Mandate mandate = new Mandate(accountNumber, accountIfsc, uniqueMandateReferenceNumber, mandateExpiry, amount,0, 
                MandateType.valueOf(type),null,MandateFrequency.valueOf(frequency),customerName,customerId,PurposeCode.valueOf(purposeCode));

            if (validateMandateData(mandate)){
                stub.putStringState(uniqueMandateReferenceNumber, mandate.toJSONString());
            }
            
            return mandate;
        }

    private boolean validateMandateData(final Mandate mandate){

        if (MandateUtils.stringinListIsNullOrEmpty(new ArrayList<>(Arrays.asList(mandate.getAccountNumber(), 
            mandate.getUniqueMandateReferenceNumber(),mandate.getType().toString(),mandate.getFrequency().toString(),
            mandate.getCustomerId(),mandate.getPurposeCode().toString())))){

                String errorMessage = "Mandatory fields like AccountNumber, UniqueMandateReferenceNumber, Mandate Type, "+
                    "Mandate Frequency, customer id and purspose code are required";
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
        }
        return true;
        }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean mandateExists(final Context ctx, final String uniqueMandateReferenceNumber) {
        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        return (mandateJSON != null && !mandateJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Mandate getMandate(final Context ctx, final String uniqueMandateReferenceNumber) {
        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        if (mandateJSON != null && !mandateJSON.isEmpty()){
            return Mandate.fromJSONString(mandateJSON);
        }
        return null;
    }

    // @Transaction(intent = Transaction.TYPE.SUBMIT)
    // public Mandate updateMandate(final Context ctx, final String accountNumber, final String accountIfsc,
    //     final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount, 
    //     final String type,final String frequency,final String customerName,final String customerId, 
    //     final String purposeCode) {

        
    // }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public MandateTransaction approveTransaction(final Context ctx, final String fromAccount,
        final String uniqueMandateReferenceNumber, final String purposeCode, final long amount, 
        final String date) {

        Mandate mandate = getMandate(ctx, uniqueMandateReferenceNumber);
        MandateTransaction mandateTransaction = new MandateTransaction(fromAccount, uniqueMandateReferenceNumber, 
            PurposeCode.valueOf(purposeCode), amount, date);
        if (mandate == null){
            // String errorMessage = String.format("Mandate %s does not exist", uniqueMandateReferenceNumber);
            // System.out.println(errorMessage);
            throw new ChaincodeException(printErrorString("Mandate %s does not exist", uniqueMandateReferenceNumber), 
                MandateErrors.MANDATE_NOT_FOUND.toString());
        }
        if (validateUtilizationData(mandate, mandateTransaction)){
            updateMandate(ctx, mandate, mandateTransaction);
            return mandateTransaction;    
        }
        else {
            throw new ChaincodeException(printErrorString("Mandate Validations failed", uniqueMandateReferenceNumber), 
                MandateErrors.INVALID_MANDATE.toString());
        }
    }

    private Boolean validateUtilizationData(Mandate mandate, MandateTransaction mandateTransaction){

        
        
        if (MandateUtils.stringIsNullOrEmpty(mandateTransaction.getDate()) ||
            MandateUtils.longIsNullOrZero(mandateTransaction.getAmount())){
                throw new ChaincodeException(printErrorString("Invalid input data %s or %d", mandateTransaction.getDate(),
                    mandateTransaction.getAmount(), MandateErrors.MANDATE_NOT_FOUND.toString()));
        }

        if (LocalDate.parse(mandateTransaction.getDate(), dateTimeFormatter)
            .isAfter(LocalDate.parse(mandate.getMandateExpiry(), dateTimeFormatter))){
                throw new ChaincodeException(printErrorString("Mandate has expired on %s", mandate.getMandateExpiry()), 
                    MandateErrors.INVALID_MANDATE.toString());
        }
        if (mandate.getType()==MandateType.FIXED && mandateTransaction.getAmount()!=mandate.getAmount()){
            throw new ChaincodeException(printErrorString("Mandate type is fixed and transaction amount %d does "+
                "match the mandate amount", mandate.getAmount()), MandateErrors.INVALID_MANDATE.toString());
        }
        return true;

    }
    /*
     * if (transactiondate-last transaction date) >= Frequency days and transaction.amount <=mandate.amount) 
     *      then approve transaction and update mandate uqilizaiton = transaction amount and update last transaction date = current date
       else if (transactiondate-last transaction date) - Frequency days  and transac.amount<= mandate.amount-utilization) 
            then approve transaction and update mandate uqilizaiton = mandate.utilization+ transaction amount and update last transaction date = current date
     */

    private Mandate updateMandate(final Context ctx, final Mandate mandate, final MandateTransaction mandateTransaction){

        long daysBetween = ChronoUnit.DAYS.between(LocalDate.parse(mandateTransaction.getDate(),dateTimeFormatter),
            LocalDate.parse(mandate.getLastTransactionDate(),dateTimeFormatter));
        if ( daysBetween>= mandate.getFrequency().getValue() && mandateTransaction.getAmount()<=mandate.getAmount()){
            return updateMandateUtilizationAndLastTransactionDate(ctx, mandate,mandateTransaction.getAmount()
                ,mandateTransaction.getDate());
        }
        else if (daysBetween<mandate.getFrequency().getValue() && 
            mandateTransaction.getAmount()<=mandate.getAmount()-mandate.getUtilization()){
            return updateMandateUtilizationAndLastTransactionDate(ctx, mandate, mandate.getUtilization()+mandateTransaction.getAmount()
                ,mandateTransaction.getDate());
        }
        else {
            throw new ChaincodeException(printErrorString("Error while updating mandate %s to ledger ", 
            mandate.getUniqueMandateReferenceNumber()), MandateErrors.INVALID_MANDATE.toString());
        }

    }

    private Mandate updateMandateUtilizationAndLastTransactionDate(final Context ctx, Mandate mandate, final long transactionAmount,
        final String lastTransactionDate){

        ChaincodeStub stub = ctx.getStub();
        mandate.setLastTransactionDate(lastTransactionDate);
        mandate.setUtilization(transactionAmount);
        stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
        return mandate;

    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Mandate updateMandateUtilizationAndLastTransactionDate(final Context ctx, String uniqueMandateReferenceNumber, 
        final long transactionAmount,final String lastTransactionDate){

        Mandate mandate = ReadMandate(ctx, uniqueMandateReferenceNumber);
        ChaincodeStub stub = ctx.getStub();
        mandate.setLastTransactionDate(lastTransactionDate);
        mandate.setUtilization(transactionAmount);
        stub.putStringState(mandate.getUniqueMandateReferenceNumber(), mandate.toJSONString());
        return mandate;

    }

    // @Transaction(intent = Transaction.TYPE.EVALUATE)
    // public Transaction ReadTransaction(final Context ctx, final String transactionId) {


    // }


    private String printErrorString(String errorString, Object... args){
        String errorMessage = String.format(errorString, args);
        System.out.println(errorMessage);
        return errorMessage;
    }



}
