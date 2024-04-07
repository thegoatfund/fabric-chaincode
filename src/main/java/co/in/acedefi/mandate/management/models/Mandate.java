package co.in.acedefi.mandate.management.models;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;

import co.in.acedefi.mandate.management.enums.MandateFrequency;
import co.in.acedefi.mandate.management.enums.MandateType;
import co.in.acedefi.mandate.management.enums.PurposeCode;
import lombok.Getter;
import lombok.Setter;
import static java.nio.charset.StandardCharsets.UTF_8;

/*
 * a> Mandate Date
b> Expiry
c> Account number
d> IFSC
e> Amount
f> Frequency
g> Mandate Type: Fixed or Max Amount
f> Owner Name:
i> customerId
g transaction type
h> UMRN
 */
@Getter
@Setter
@DataType()
public class Mandate {

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
@JsonProperty("customerName")
private String customerName;

@Property()
@JsonProperty("customerId")
private String customerId;

@Property()
@JsonProperty("purposeCode")
private PurposeCode purposeCode;

public Mandate(@JsonProperty("accountNumber") final String accountNumber, 
            @JsonProperty("accountIfsc") final String accountIfsc,
        @JsonProperty("uniqueMandateReferenceNumber") final String uniqueMandateReferenceNumber, 
        @JsonProperty("mandateExpiry") final String mandateExpiry, 
        @JsonProperty("amount") final long amount, 
        @JsonProperty("utilization") final long utilization,
        @JsonProperty("type") final MandateType type,
        @JsonProperty("lastTransactionDate") final String lastTransactionDate,
        @JsonProperty("frequency") final MandateFrequency frequency,
        @JsonProperty("customerName") final String customerName,
        @JsonProperty("customerId") final String customerId, 
        @JsonProperty("purposeCode") final PurposeCode purposeCode) {
            super();
            this.accountNumber = accountNumber;
            this.accountIfsc = accountIfsc;
            this.uniqueMandateReferenceNumber = uniqueMandateReferenceNumber;
            this.mandateExpiry = mandateExpiry;
            this.amount = amount;
            this.utilization=utilization;
            this.lastTransactionDate=lastTransactionDate;
            this.type=type;
            this.frequency = frequency;
            this.customerName=customerName;
            this.customerId=customerId;
            this.purposeCode=purposeCode;
    }

    public static Mandate fromJSONString(final String data) {
        final JSONObject json = new JSONObject(data);
        final Mandate mandate = new Mandate(
            json.getString("accountNumber"), 
            json.getString("accountIfsc"),
            json.getString("uniqueMandateReferenceNumber"), 
            json.getString("mandateExpiry"), 
            Long.valueOf(json.getString("amount")), 
            Long.valueOf(json.getString("utilization")), 
            MandateType.valueOf(json.getString("type")),
            json.getString("lastTransactionDate"),
            MandateFrequency.valueOf(json.getString("frequency")),
            json.getString("customerName"),
            json.getString("customerId"), 
            PurposeCode.valueOf(json.getString("purposeCode")));
        return mandate;
    }

    public static Mandate fromBytes(final byte[] bytes) {
        return new Genson().deserialize(new String(bytes, UTF_8), Mandate.class);
    }

    public String toJSONString() {
        return new Genson().serialize(this).toString();
    }



}
