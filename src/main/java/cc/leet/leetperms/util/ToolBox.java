package cc.leet.leetperms.util;

import java.text.DecimalFormat;

public class ToolBox {

    public static String getTimeSpent(double start){
        DecimalFormat decimal = new DecimalFormat("##.###");
        return decimal.format((System.nanoTime() - start) / 1000000);
    }

    public static boolean isAlphaNumeric(String string) {
        for(int i = string.length(); i --> 0;) {
            if(!Character.isLetterOrDigit(string.charAt(i)) )
                return false;
        }
        return true;
    }

}