package com.berry.blue.reds.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Timy {
    private static String STANDARD_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(STANDARD_DATE_FORMAT, Locale.US);

    public static String nowToString() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return simpleDateFormat.format(now);
    }
}
