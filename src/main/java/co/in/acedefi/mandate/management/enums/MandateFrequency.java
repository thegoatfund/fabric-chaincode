package co.in.acedefi.mandate.management.enums;

import java.util.HashMap;
import java.util.Map;

public enum MandateFrequency {
    DAILY(1),
    MONTHLY(30),
    QUARTERLY(90),
    YEARLY(365),
    MANY_TILL_EXPIRY(0);

    private static final Map<Integer, MandateFrequency> MANDATE_FREQUENCE_MAP = new HashMap<>();

    private int value;

    MandateFrequency(final int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public static MandateFrequency getByValue(final int value) {
        return MANDATE_FREQUENCE_MAP.get(value);
    }

}
