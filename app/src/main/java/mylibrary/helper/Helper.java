package mylibrary.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mylibrary.Constants;

public class Helper {
    public static String getStringResourceByName(Context c, String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources().getIdentifier(aString, "string", packageName);
        if (c != null) {
            return c.getString(resId);
        }
        else {
            return null;
        }
    }

    public static double dpToPx(Context context, float dp) {
        return convertDpToPixel(dp, context);
    }
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static String getThaiMonthString(Date date, Context context) {
        Locale locale = new Locale("th", "TH");
        return getMonthString(locale, date, context);

    }


    public static String getMonthString(Locale locale, Date date, Context context) {
        String dateString;
        if (locale.getLanguage().equals("th")) {
            Calendar calendar = Calendar.getInstance(locale);
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM", locale);
            dateString = sdf.format(date);
            dateString = String.format("%s %s", dateString, year + 543);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", locale);
            dateString = sdf.format(date);
        }
        return dateString;
    }



    public static String getThaiDateString(Date date, Context context) {
        Locale locale = new Locale("th", "TH");
        return getDateString(locale, date, context);

    }


    public static String getDateString(Locale locale, Date date, Context context) {
        String dateString;
        if (locale.getLanguage().equals("th")) {
            Calendar calendar = Calendar.getInstance(locale);
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM", locale);
            dateString = sdf.format(date);
            dateString = String.format("%s %s", dateString, year + 543);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", locale);
            dateString = sdf.format(date);
        }
        return dateString;
    }

    public static boolean setNotFirstTime(Context context, String prefName) {
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean(Constants.OPEN_FIRST_TIME, false);
        return editor.commit();
    }

    public static boolean setFirstTime(Context context, String prefName) {
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        sp = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.putBoolean(Constants.OPEN_FIRST_TIME, true);
        return editor.commit();
    }


    public static boolean isFirstTime(Context context, String prefName) {
        boolean is_first_time = context.getSharedPreferences(prefName, Context.MODE_PRIVATE).
                getBoolean(Constants.OPEN_FIRST_TIME, true);
        return is_first_time;
    }

//    public static int getToolbarHeight(Context context) {
//        int height = (int) context.getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
//        return height;
//    }
//
//    public static int getStatusBarHeight(Context context) {
//        int height = (int) context.getResources().getDimension(R.dimen.statusbar_size);
//        return height;
//    }



}
