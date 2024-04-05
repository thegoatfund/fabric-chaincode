package co.in.acedefi.mandate.management.utils;

import java.util.List;

public final class MandateUtils {

    private MandateUtils() {

    }
  
    /**
     * @param string
     * @return
     */
    public static boolean stringIsNullOrEmpty(final String string) {
      return string == null || string.isEmpty();
    }

    public static boolean stringinListIsNullOrEmpty(final List<String> stringList) {

        for (String string : stringList){
            if (stringIsNullOrEmpty(string)){
                return true;
            }
        }
        return false;
    }
}
