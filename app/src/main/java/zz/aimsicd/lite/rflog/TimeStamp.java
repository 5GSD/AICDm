package zz.aimsicd.lite.rflog;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeStamp {

    // For full reference, see: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
    // NOTE: We ignore timezone
    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    static final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    public static String getTimeStamp(long timestamp) {
        return format.format(timestamp);
    }

    public static String getTimeStamp() {
        return format.format(new Date());
    }
}
