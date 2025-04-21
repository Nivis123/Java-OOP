package ru.inmemorydb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static Date parseDate(String dateStr) throws Exception {
        return ISO_FORMAT.parse(dateStr);
    }

    public static String formatDate(Date date) {
        return ISO_FORMAT.format(date);
    }
}