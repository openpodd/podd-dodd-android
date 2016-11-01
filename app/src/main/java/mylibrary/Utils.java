package mylibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Utils {

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static boolean isThaiVersion() {
        return getLanguage().equalsIgnoreCase("th");
    }


    public static int pxToDp(Context context, int px) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    //http://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    public static float dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static float dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static float convertDpToPixel(Context context, int dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        return new Point(width, height);
    }
    public static void showKeyboard(final Context context, final EditText focusedBox) {
        // show soft keyboard
        focusedBox.setSelectAllOnFocus(true);
        focusedBox.selectAll();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(focusedBox.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
            }
        };
        timer.schedule(task, 500);

    }

    public static void hideKeyboard(final Context context, final EditText focusedBox) {
        InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(focusedBox.getApplicationWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static Calendar getTomorrowThisTime(Calendar c) {
        Calendar tomorrow = Calendar.getInstance(new Locale("th"));
        tomorrow = Utils.getNextNDay(1, c);
        return tomorrow;
    }

    public static Calendar getNextNDay(int day, Calendar c) {
        Calendar new_date = c;
        new_date.add(Calendar.DATE, day);
        return new_date;
    }

    public static Calendar getNextNMinute(int minute, Calendar c) {
        Calendar tomorrow = c;
        tomorrow.add(Calendar.MINUTE, minute);
        return tomorrow;
    }

    public static void fillImageViewInLinearLayout(ImageView what, int times, LinearLayout to) {
        for(int i =0 ;i < times; i++) {
            to.addView(what);
        }
    }

    public static void updateImageViewInLinearLayout(ImageView what, int times, LinearLayout to) {
        for(int i =0 ;i < times; i++) {
            to.addView(what);
        }
    }

    public static Calendar getNextNSecond(int second, Calendar c) {
        Calendar tomorrow = c;
        tomorrow.add(Calendar.SECOND, second);
        return tomorrow;
    }

    public static boolean stringIsEmpty(String str) {
        return str.trim().isEmpty();
    }

    public static boolean stringIsNotEmpty(String str) {
        return !stringIsEmpty(str);
    }

    public static String parseOneChangeDateFormat(Date date){
        try {

            Calendar cdate = Calendar.getInstance();
            cdate.setTime(date);

            // Java Calendar Constant + 1 example: October = 9
            return cdate.get(Calendar.YEAR) + "-" + (cdate.get(Calendar.MONTH) +1)  + "-" + cdate.get(Calendar.DATE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static ViewGroup createEmptyView(final ListView lview, final Context context) {
//        RelativeLayout relativeLayout = new RelativeLayout(context);
//        relativeLayout.setLayoutParams(
//                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT));
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View emptyView = inflater.inflate(R.layout.empty_list_item, null);
//
//        relativeLayout.addView(emptyView);
//
//        TextView text = new TextView(context);
//        text.setText("You still have no mission.");
//        text.setTextColor(context.getResources().getColor(R.color.lightgrey));
//
//        text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT));
//        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//
//        ((ViewGroup)lview.getParent()).addView(text);
//
//        return relativeLayout;
//    }
//
//
//    public static void setEmptyViewInlistView(final ListView lview, final Context context) {
//        RelativeLayout relativeLayout = new RelativeLayout(context);
//        relativeLayout.setLayoutParams(
//                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT));
//
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View emptyView = inflater.inflate(R.layout.empty_list_item, null);
//
//        relativeLayout.addView(emptyView);
//
//        TextView text = new TextView(context);
//        text.setText(context.getResources().getString(R.string.listview_no_mission));
//        text.setTextColor(context.getResources().getColor(R.color.lightgrey));
//
//        text.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT));
//        text.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
//
//        ((ViewGroup)lview.getParent()).addView(text);
//
//        lview.setEmptyView(relativeLayout);
//    }

}
