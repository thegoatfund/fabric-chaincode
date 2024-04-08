package co.in.acedefi.mandate.management.models;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;

import co.in.acedefi.mandate.management.enums.PurposeCode;
import static java.nio.charset.StandardCharsets.UTF_8;





/*
 * From account
UMRN
transaction type
amount
date
 */

@DataType()
public final class MandateTransaction {

    @Property()
    @JsonProperty()
    private String fromAccount;

    @Property()
    @JsonProperty("uniqueMandateReferenceNumber")
    private String uniqueMandateReferenceNumber;

    @Property()
    @JsonProperty("purposeCode")
    private PurposeCode purposeCode;

    @Property()
    @JsonProperty("amount")
    private long amount;

    @Property()
    @JsonProperty("date")
    private String date;

    public MandateTransaction(@JsonProperty("fromAccount") final String fromAccount,
        @JsonProperty("uniqueMandateReferenceNumber") final String uniqueMandateReferenceNumber,
        @JsonProperty("purposeCode") final PurposeCode purposeCode,
        @JsonProperty("amount") final long amount,
        @JsonProperty("date") final String date) {
            super();
            this.fromAccount = fromAccount;
            this.uniqueMandateReferenceNumber = uniqueMandateReferenceNumber;
            this.purposeCode = purposeCode;
            this.amount = amount;
            this.date = date;
    }

    public static MandateTransaction fromJSONString(final String data) {
        final JSONObject json = new JSONObject(data);
        final MandateTransaction transaction = new MandateTransaction(
            json.getString("fromAccount"),
            json.getString("uniqueMandateReferenceNumber"),
            PurposeCode.valueOf(json.getString("purposeCode")),
            Long.valueOf(json.getString("amount")),
            json.getString("date"));
        return transaction;
    }

    public static MandateTransaction fromBytes(final byte[] bytes) {
        return new Genson().deserialize(new String(bytes, UTF_8), MandateTransaction.class);
    }

/**
 * Converts the MandateTRansaction object into a json string
 *
 * serialized the object to a json string using Genson.
 * @return Mandate object in a json string format
 */
    public String toJSONString() {
        return new Genson().serialize(this).toString();
    }

/**
 * gets the fromAccount of the Mandate Transaction
 * @return fromAccount
 */
    public String getFromAccount() {
        return fromAccount;
    }

/**
 * gets the uniqueMandateReferenceNumber of the Mandate Transaction
 * @return uniqueMandateReferenceNumber
 */
    public String getUniqueMandateReferenceNumber() {
        return uniqueMandateReferenceNumber;
    }

/**
 * gets the purposeCode of the Mandate Transaction
 * @return purposeCode
 */
    public PurposeCode getPurposeCode() {
        return purposeCode;
    }

/**
 * gets the amount of the Mandate Transaction
  * @return amount
 */
    public long getAmount() {
        return amount;
    }

/**
 * gets the Date of the Mandate Transaction
  * @return Date
 */
    public String getDate() {
        return date;
    }
/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public void setFromAccount(final String newFromAccount) {
        this.fromAccount = newFromAccount;
    }

/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public void setUniqueMandateReferenceNumber(final String newUniqueMandateReferenceNumber) {
        this.uniqueMandateReferenceNumber = newUniqueMandateReferenceNumber;
    }

/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public void setPurposeCode(final PurposeCode newPurposeCode) {
        this.purposeCode = newPurposeCode;
    }

/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public void setAmount(final long newAmount) {
        this.amount = newAmount;
    }

/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public void setDate(final String newDate) {
        this.date = newDate;
    }

}

