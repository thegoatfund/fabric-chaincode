package co.in.acedefi.mandate.management.enums;

public enum ContractEvents {

    MANDATE_CREATED("MandateCreated"),
    MANDATE_CREATION_FAILED("MandateCreationFailed"),
    TRANSACTION_APPROVED("TransactionApproved"),
    TRANSACTION_APPROVAL_FAILED("TransactionApprovalFailed");

    private String value;

    ContractEvents(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
