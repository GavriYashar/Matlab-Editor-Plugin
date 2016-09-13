package at.justin.matlab.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Created by Andreas Justin on 2016-09-12. */
public class DateUtil {
    private static final DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final Date dateObj = new Date();

    public static String getCurrentDate() {
        return getCurrentDate(df);
    }

    public static String getCurrentDate(DateFormat dateFormat) {
        dateObj.setTime(System.currentTimeMillis());
        return dateFormat.format(dateObj);
    }

    public static String getDate(long millis) {
        dateObj.setTime(millis);
        return df.format(dateObj);
    }
}
