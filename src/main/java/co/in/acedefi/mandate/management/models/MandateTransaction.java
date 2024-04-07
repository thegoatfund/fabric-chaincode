package co.in.acedefi.mandate.management.models;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonProperty;

import co.in.acedefi.mandate.management.enums.PurposeCode;
import lombok.Getter;
import lombok.Setter;
import static java.nio.charset.StandardCharsets.UTF_8;





/*
 * From account
UMRN
transaction type
amount
date
 */
@Getter
@Setter
@DataType()
public class MandateTransaction {

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

    public String toJSONString() {
        return new Genson().serialize(this).toString();
    }

}

