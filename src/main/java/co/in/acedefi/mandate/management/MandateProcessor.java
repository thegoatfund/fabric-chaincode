package co.in.acedefi.mandate.management;

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
        MANDATE_ALREADY_EXISTS
    }

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

            if (MandateExists(ctx, uniqueMandateReferenceNumber)) {
                String errorMessage = String.format("Mandate %s already exists", uniqueMandateReferenceNumber);
                System.out.println(errorMessage);
                throw new ChaincodeException(errorMessage, MandateErrors.MANDATE_ALREADY_EXISTS.toString());
            }
    
            Mandate mandate = new Mandate(accountNumber, accountIfsc, uniqueMandateReferenceNumber, mandateExpiry, amount, 
                MandateType.valueOf(type),MandateFrequency.valueOf(frequency),customerName,customerId,PurposeCode.valueOf(purposeCode));

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
    public boolean MandateExists(final Context ctx, final String uniqueMandateReferenceNumber) {
        ChaincodeStub stub = ctx.getStub();
        String mandateJSON = stub.getStringState(uniqueMandateReferenceNumber);

        return (mandateJSON != null && !mandateJSON.isEmpty());
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Mandate updateMandate(final Context ctx, final String accountNumber, final String accountIfsc,
        final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount, 
        final String type,final String frequency,final String customerName,final String customerId, 
        final String purposeCode) {

        
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Transaction approveTransaction(final Context ctx, final String accountNumber, final String accountIfsc,
        final String uniqueMandateReferenceNumber, final String mandateExpiry, final long amount, 
        final String type,final String frequency,final String customerName,final String customerId, 
        final String purposeCode) {

        
    }

    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Boolean updateUtilization(final Context ctx, final long transactionAmount){

    }

    private Transaction saveTransaction(final Context ctx, final Transaction transaction){

    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Transaction ReadTransaction(final Context ctx, final String transactionId) {


    }



}
