package com.afweb.util;

public class NumConvertion {
    public NumConvertion() {
    }


    public static float ConvertNumMilion(String strField) {
        strField = strField.trim();
        if (strField.equals("N/A") || strField.equals("NA")) {
            return 0;
        }
        int pos;
        if ((pos = strField.indexOf("Mil")) != -1) {
            strField = strField.substring(0, pos);
            strField = strField.replaceAll(",", "");
            float dField = Float.parseFloat(strField);

            dField = dField*1000000;
            return dField;

        }
        strField = strField.replaceAll(",", "");
        return Float.parseFloat(strField);
    }

    public static float ConvertNum(String strField) {
        strField = strField.trim();
        if (strField.equals("N/A") || strField.equals("NA")) {
            return 0;
        }

        strField = strField.replaceAll(",", "");
        return Float.parseFloat(strField);
    }
}
