package com.yooogle.bigmoney.utils;

import java.text.DecimalFormat;

public class Util {

    public static String format(double amount) {
        DecimalFormat d = new DecimalFormat("$###,###,###,###,##0.00");
        return d.format(amount);
    }

    public static boolean ArrayContains(String[] array, String string) {

        for(String s : array) {
            if (s.equalsIgnoreCase(string)) {
                return true;
            }
        }

        return false;
    }

}
