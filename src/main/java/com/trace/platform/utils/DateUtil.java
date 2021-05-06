package com.trace.platform.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static String toNormalizeString(Date date) {
        return sdf.format(date);
    }

    public static Date strToDate(String str) throws ParseException {
        return sdf.parse(str);
    }
}
