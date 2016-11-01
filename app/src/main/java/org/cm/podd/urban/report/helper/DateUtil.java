package org.cm.podd.urban.report.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nat on 10/27/15 AD.
 */
public class DateUtil {
    public final static Locale TH_LOCALE = new Locale("th", "TH");

    final static String[] THAI_MONTH = {
            "ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
            "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."
    };

    public static String formatLocaleDate(Date date) {
        Locale defaultLocale = Locale.getDefault();

        if (defaultLocale.getLanguage().equals(TH_LOCALE.getLanguage())) {
            return convertToThaiDate(date);
        } else {
            return new SimpleDateFormat("dd MMM yyyy", defaultLocale).format(date);
        }
    }

    public static String convertToThaiDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dateNum = cal.get(Calendar.DATE);
        int month = cal.get(Calendar.MONTH);
        String thaiMonth = THAI_MONTH[month];
        int year = cal.get(Calendar.YEAR) + 543;
        return String.format("%d %s %d", dateNum, thaiMonth, year);
    }

//    public static String formatLocaleDateTime(Date date) {
//        String thaiDate = formatLocaleDate(date);
//        String time = formatTime(date);
//        String at = Application.getAppContext().getString(R.string.time_at);
//        return String.format("%s %s %s", thaiDate, at, time);
//    }

    private static String formatTime(Date date) {
        Locale defaultLocale = Locale.getDefault();
        if (defaultLocale.getLanguage().equals(TH_LOCALE.getLanguage())) {
            return new SimpleDateFormat("HH:mm").format(date);
        } else {
            return new SimpleDateFormat("hh:mm a").format(date);
        }
    }

    public static Date fromJsonDateString(String dateStr, int tz) {
        String[] formats = new String[]{
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS'z'",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.Z",
                "yyyy-MM-dd'T'HH:mm:ss.'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ss.SSSzzzz", // Blogger Atom feed has millisecs also
                "yyyy-MM-dd'T'HH:mm:sszzzz",
                "yyyy-MM-dd'T'HH:mm:ss z",
                "yyyy-MM-dd'T'HH:mm:ssz", // ISO_8601
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HHmmss.SSSz",

                "yyyy-MM-dd",
//                2015-10-02T03:33:58.918Z
        };
        Locale defaultLocale = Locale.getDefault();

        Date date = null;

        for (String format : formats) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, defaultLocale);
                date = parser.parse(dateStr);

                Calendar c = Calendar.getInstance();

                c.setTime(date);
                c.add(Calendar.HOUR, tz);

                date = c.getTime();
                break;
            } catch (ParseException e) {
                // Do nothing.
            }
        }

        return date;
    }
}
