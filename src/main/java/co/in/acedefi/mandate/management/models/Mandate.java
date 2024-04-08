package co.in.acedefi.mandate.management.models;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;

import co.in.acedefi.mandate.management.enums.MandateFrequency;
import co.in.acedefi.mandate.management.enums.MandateType;
import co.in.acedefi.mandate.management.enums.PurposeCode;
import static java.nio.charset.StandardCharsets.UTF_8;



@DataType()
public final class Mandate {

@Property()
@JsonProperty("accountNumber")
private String accountNumber;

@Property()
@JsonProperty("accountIfsc")
private String accountIfsc;

@Property()
@JsonProperty("uniqueMandateReferenceNumber")
private String uniqueMandateReferenceNumber;

@Property()
@JsonProperty("mandateExpiry")
private String mandateExpiry;

@Property()
@JsonProperty("amount")
private long amount;

@Property()
@JsonProperty("type")
private MandateType type;

@Property()
@JsonProperty("utilization")
private long utilization;

@Property()
@JsonProperty("lastTransactionDate")
private String lastTransactionDate;

@Property()
@JsonProperty("frequency")
private MandateFrequency frequency;

@Property()
@JsonProperty("merchantIdentifier")
private String merchantIdentifier;

@Property()
@JsonProperty("customerId")
private String customerId;

@Property()
@JsonProperty("purposeCode")
private PurposeCode purposeCode;

private Mandate() { }

public static class Builder {
    private Mandate mandate = new Mandate();

 /**
 * instantiates the accountNumber of a new instance of Mandate
 * @param accountNumber
 * @return new instance of mandate
 */
    public Builder accountNumber(final String accountNumber) {
        mandate.accountNumber = accountNumber;
        return this;
    }

/**
 * instantiates the accountIfsc of a new instance of Mandate
 * @param accountIfsc
 * @return new instance of mandate
 */
    public Builder accountIfsc(final String accountIfsc) {
        mandate.accountIfsc = accountIfsc;
        return this;
    }

/**
 * instantiates the uniqueMandateReferenceNumber of a new instance of Mandate
 * @param uniqueMandateReferenceNumber
 * @return new instance of mandate
 */
    public Builder uniqueMandateReferenceNumber(final String uniqueMandateReferenceNumber) {
        mandate.uniqueMandateReferenceNumber = uniqueMandateReferenceNumber;
        return this;
    }

/**
 * instantiates the mandateExpiry of a new instance of Mandate
 * @param mandateExpiry
 * @return new instance of mandate
 */
    public Builder mandateExpiry(final String mandateExpiry) {
        mandate.mandateExpiry = mandateExpiry;
        return this;
    }

/**
 * instantiates the amount of a new instance of Mandate
 * @param amount
 * @return new instance of mandate
 */
    public Builder amount(final long amount) {
        mandate.amount = amount;
        return this;
    }

/**
 * instantiates the type of a new instance of Mandate
 * @param type
 * @return new instance of mandate
 */
    public Builder type(final String type) {
        mandate.type = MandateType.valueOf(type);
        return this;
    }

/**
 * instantiates the utilization of a new instance of Mandate
 * @param utilization
 * @return new instance of mandate
 */
    public Builder utilization(final long utilization) {
        mandate.utilization = utilization;
        return this;
    }

/**
 * instantiates the lastTransactionDate of a new instance of Mandate
 * @param lastTransactionDate
 * @return new instance of mandate
 */
    public Builder lastTransactionDate(final String lastTransactionDate) {
        mandate.lastTransactionDate = lastTransactionDate;
        return this;
    }

/**
 * instantiates the frequency of a new instance of Mandate
 * @param frequency
 * @return new instance of mandate
 */
    public Builder frequency(final String frequency) {
        mandate.frequency = MandateFrequency.valueOf(frequency);
        return this;
    }

/**
 * instantiates the merchantIdentifier of a new instance of Mandate
 * @param merchantIdentifier
 * @return new instance of mandate
 */
    public Builder merchantIdentifier(final String merchantIdentifier) {
        mandate.merchantIdentifier = merchantIdentifier;
        return this;
    }

/**
 * instantiates the customerId of a new instance of Mandate
 * @param customerId
 * @return new instance of mandate
 */
    public Builder customerId(final String customerId) {
        mandate.customerId = customerId;
        return this;
    }

/**
 * instantiates the purposeCode of a new instance of Mandate
 * @param purposeCode
 * @return new instance of mandate
 */
    public Builder purposeCode(final String purposeCode) {
        mandate.purposeCode = PurposeCode.valueOf(purposeCode);
        return this;
    }

/**
 * @return new instance of mandate
 */
    public Mandate build() {
        return mandate;
    }
}
// public Mandate(@JsonProperty("accountNumber") final String accountNumber,
//             @JsonProperty("accountIfsc") final String accountIfsc,
//         @JsonProperty("uniqueMandateReferenceNumber") final String uniqueMandateReferenceNumber,
//         @JsonProperty("mandateExpiry") final String mandateExpiry,
//         @JsonProperty("amount") final long amount,
//         @JsonProperty("utilization") final long utilization,
//         @JsonProperty("type") final MandateType type,
//         @JsonProperty("lastTransactionDate") final String lastTransactionDate,
//         @JsonProperty("frequency") final MandateFrequency frequency,
//         @JsonProperty("merchantIdentifier") final String merchantIdentifier,
//         @JsonProperty("customerId") final String customerId,
//         @JsonProperty("purposeCode") final PurposeCode purposeCode) {
//             super();
//             this.accountNumber = accountNumber;
//             this.accountIfsc = accountIfsc;
//             this.uniqueMandateReferenceNumber = uniqueMandateReferenceNumber;
//             this.mandateExpiry = mandateExpiry;
//             this.amount = amount;
//             this.utilization = utilization;
//             this.lastTransactionDate = lastTransactionDate;
//             this.type = type;
//             this.frequency = frequency;
//             this.merchantIdentifier = merchantIdentifier;
//             this.customerId = customerId;
//             this.purposeCode = purposeCode;
//     }

    public static Mandate fromJSONString(final String data) {
        final JSONObject json = new JSONObject(data);
        final Mandate mandate = new Builder()
            .accountNumber(json.getString("accountNumber"))
            .accountIfsc(json.getString("accountIfsc"))
            .uniqueMandateReferenceNumber(json.getString("uniqueMandateReferenceNumber"))
            .mandateExpiry(json.getString("mandateExpiry"))
            .amount(Long.valueOf(json.getString("amount")))
            .type(json.getString("type"))
            .utilization(Long.valueOf(json.getString("utilization")))
            .lastTransactionDate(json.getString("lastTransactionDate"))
            .frequency(json.getString("frequency"))
            .merchantIdentifier(json.getString("merchantIdentifier"))
            .customerId(json.getString("customerId"))
            .purposeCode(json.getString("purposeCode"))
            .build();

        return mandate;
    }

    public static Mandate fromBytes(final byte[] bytes) {
        return new Genson().deserialize(new String(bytes, UTF_8), Mandate.class);
    }

/**
 * Converts the object instance "this" into a json string
 * @return Json String
 */

    public String toJSONString() {
        return new Genson().serialize(this).toString();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountIfsc() {
        return accountIfsc;
    }

    public String getUniqueMandateReferenceNumber() {
        return uniqueMandateReferenceNumber;
    }

    public String getMandateExpiry() {
        return mandateExpiry;
    }

    public long getAmount() {
        return amount;
    }

    public MandateType getType() {
        return type;
    }

    public long getUtilization() {
        return utilization;
    }

    public String getLastTransactionDate() {
        return lastTransactionDate;
    }

    public MandateFrequency getFrequency() {
        return frequency;
    }

    public String getMerchantIdentifier() {
        return merchantIdentifier;
    }

    public String getCustomerId() {
        return customerId;
    }

    public PurposeCode getPurposeCode() {
        return purposeCode;
    }

    public void setAccountNumber(final String newAccountNumber) {
        this.accountNumber = newAccountNumber;
    }

    public void setAccountIfsc(final String newAccountIfsc) {
        this.accountIfsc = newAccountIfsc;
    }

    public void setUniqueMandateReferenceNumber(final String newUniqueMandateReferenceNumber) {
        this.uniqueMandateReferenceNumber = newUniqueMandateReferenceNumber;
    }

    public void setMandateExpiry(final String newMandateExpiry) {
        this.mandateExpiry = newMandateExpiry;
    }

    public void setAmount(final long newAmount) {
        this.amount = newAmount;
    }

    public void setType(final MandateType newType) {
        this.type = newType;
    }

    public void setUtilization(final long newUtilization) {
        this.utilization = newUtilization;
    }

    public void setLastTransactionDate(final String newLastTransactionDate) {
        this.lastTransactionDate = newLastTransactionDate;
    }

    public void setFrequency(final MandateFrequency newFrequency) {
        this.frequency = newFrequency;
    }

    public void setMerchantIdentifier(final String newMerchantIdentifier) {
        this.merchantIdentifier = newMerchantIdentifier;
    }

    public void setCustomerId(final String newCustomerId) {
        this.customerId = newCustomerId;
    }

    public void setPurposeCode(final PurposeCode newPurposeCode) {
        this.purposeCode = newPurposeCode;
    }

}
