package cc.leet.leetperms.util;

import java.text.DecimalFormat;

public class ToolBox {

    public static String getTimeSpent(double start){
        DecimalFormat decimal = new DecimalFormat("##.###");
        return decimal.format((System.nanoTime() - start) / 1000000);
    }

}