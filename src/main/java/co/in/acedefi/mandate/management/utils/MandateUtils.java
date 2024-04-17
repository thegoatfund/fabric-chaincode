package co.in.acedefi.mandate.management.utils;

import java.util.ArrayList;
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

        if (stringList == null) {
            return true;
        }
        for (String string : stringList) {
            if (stringIsNullOrEmpty(string)) {
                return true;
            }
        }
        return false;
    }

    public static boolean longIsNullOrZero(final Long number) {
        return number == null || number == 0;
      }

    public static List<String> convertStringToList(final String jsonString) {
        List<String> jsonStringList = new ArrayList<>();
        // Remove the outer square brackets
        String jsonStringWithoutBrackets = jsonString.substring(1, jsonString.length() - 1);

        // Split the string using comma as the delimiter
        String[] jsonObjects = jsonStringWithoutBrackets.split(",(?=\\{)");

        for (String jsonObjectString : jsonObjects) {
            jsonObjectString = jsonObjectString + "}";
            jsonStringList.add(jsonObjectString);
        }

        return jsonStringList;
    }


}
