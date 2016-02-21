package cc.leet.leetperms.util;

import cn.nukkit.utils.TextFormat;

import java.text.DecimalFormat;

public class ToolBox {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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

    /**
     * Retrieves relative time.
     *
     * @param time Since specified time
     * @return String with relative time
     */
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) return null;

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return TextFormat.GREEN + "just now" + TextFormat.GOLD;
        } else if (diff < 2 * MINUTE_MILLIS) {
            return TextFormat.GREEN + "1 minute ago" + TextFormat.GOLD; // a minute ago
        } else if (diff < 50 * MINUTE_MILLIS) {
            return "" + TextFormat.GREEN + diff / MINUTE_MILLIS + " min ago" + TextFormat.GOLD;
        } else if (diff < 90 * MINUTE_MILLIS) {
            return TextFormat.GREEN + "1 hour ago" + TextFormat.GOLD;
        } else if (diff < 24 * HOUR_MILLIS) {
            return "" + TextFormat.YELLOW + diff / HOUR_MILLIS + " hours ago" + TextFormat.GOLD;
        } else if (diff < 48 * HOUR_MILLIS) {
            return TextFormat.RED + "yesterday" + TextFormat.GOLD;
        } else {
            return "" + TextFormat.RED + diff / DAY_MILLIS + " days ago" + TextFormat.GOLD;
        }
    }

}