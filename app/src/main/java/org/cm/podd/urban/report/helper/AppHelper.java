package org.cm.podd.urban.report.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.UserLoginActivity;
import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.User;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;
import static com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError;

public class AppHelper {
    private static boolean male;
    public static final String TAG = AppHelper.class.getSimpleName();

    public static SharedPreferences getSharedPreference(Context context) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        return mSharedPref;
    }


    public static User.Model getUserSerialized(Context context) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mSharedPref.getString(Constants.USER_SERIALIZED, "{}");

        User.Model u = (User.Model) gson.fromJson(json, User.Model.class);

        return u;
    }

    public static boolean setUserSerialized(Context context, User.Model user) {
        Gson gson = new Gson();
        return setString(context, Constants.USER_SERIALIZED, gson.toJson(user));
    }

    public static void centerMapOnMyLocation(LatLng myLocation, GoogleMap map, float zoom) {
        if (myLocation != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    zoom));
        }
    }

    public static Object deSerialize(Context context, String json, Class cls) {
        Gson gson = new GsonBuilder().create();
        Object obj = gson.fromJson(json, cls);
        return obj;
    }

    public static TypedInput getTypedInputFromJsonString(Context context, String json) {
        String android_id = android.provider.Settings.Secure.
                getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        TypedInput in = null;
        try {
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return in;

    }


    public static String getDeviceId(Context context) {

        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        String android_id = mSharedPref.getString(Constants.USER_DEVICE, "NULL");

        return android_id;
    }

    public static boolean setDeviceId(Context context) {
        String android_id = android.provider.Settings.Secure.
                getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        android_id += java.util.UUID.randomUUID();

        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(Constants.USER_DEVICE, android_id);

        return editor.commit();
    }

    public static TypedInput getTypedInputFromHashMap(Context context, HashMap<String, String> myMap) {
        String android_id = android.provider.Settings.Secure.
                getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        myMap.put("device_id", android_id);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(myMap);

        TypedInput in = null;
        try {
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return in;

    }

    public static TypedInput getDeviceIdTypedInput(Context context) {
        HashMap<String, String> stringMap = new HashMap<String, String>();
        TypedInput in = getTypedInputFromHashMap(context, stringMap);
        return in;

    }

//    public static int get(Context context, String field) {
//        int value;
////        dob    = mSharedPref.getInt(Constants.FIELD_PROFILE_DOB, 0);
////        waist  = mSharedPref.getInt(Constants.FIELD_PROFILE_WAIST, 0);
////        height = mSharedPref.getInt(Constants.FIELD_PROFILE_HEIGHT, 0);
////        weight = mSharedPref.getInt(Constants.FIELD_PROFILE_WEIGHT, 0);
//        SharedPreferences mSharedPref =  context.getSharedPreferences(Constants.APP_PREF,
//                Context.MODE_PRIVATE);
//
//        if (field.equals(Constants.FIELD_PROFILE_AGE)) {
//            int year = Calendar.getInstance().get(Calendar.YEAR)+543;
//            int dob    = AppHelper.get(context, Constants.FIELD_PROFILE_DOB);
//            return year - dob;
//        }
//        else {
//            value = mSharedPref.getInt(field, 0);
//        }
//        return value;

//    }

    public static boolean setToken(Context context, String value) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(Constants.USER_TOKEN, value);
        return editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        String str = mSharedPref.getString(Constants.USER_TOKEN, "NULL");
        return "Token " + str;
    }

    public static boolean setString(Context context, String key, String value) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(key, value);
        return editor.commit();
    }


    public static AdministrationArea.Model getCurrentAdminArea(Context context) {
        String json = getString(context, Constants.ADMIN_AREAD_SERIALIZED, "{}");
        return (AdministrationArea.Model) deSerialize(context, json, AdministrationArea.Model.class);
    }

    public static boolean setBoolean(Context context, String key, boolean value) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putBoolean(key, value);
        return editor.commit();
    }


    public static boolean getBoolean(Context context, String key, boolean fallback) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);


        return mSharedPref.getBoolean(key, fallback);
    }

    public static String getString(Context context, String key, String fallback) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);


        return mSharedPref.getString(key, fallback);
    }

    public static boolean xset(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <E> E get(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (E) field.get(object);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    public static String getDurationString(Date d, int duration_minutes) {
        Calendar c = getCalendarFromDate(d);

        int hour0 = c.get(Calendar.HOUR_OF_DAY);
        int min0 = c.get(Calendar.MINUTE);

        c.add(Calendar.MINUTE, duration_minutes);

        int hour1 = c.get(Calendar.HOUR_OF_DAY);
        int min1 = c.get(Calendar.MINUTE);

        String str = String.format("%d.%02d น. - %d.%02d น.", hour0, min0, hour1, min1);
        return str;
    }

    public static Calendar getCalendarFromDate(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }

//    public static void loadCircleImage(Context context, ImageView view, String url) {
//        int size = (int) Helper.dpToPx(context, 90);
//        loadCircleImage(context, view, url, size);
//    }

    public static Date parseDateFromString(String datetimeString) throws ParseException {
        final String TAG = AppHelper.class.getSimpleName();
        if (datetimeString == null) {
            return new Date();
        }
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
        Locale locale = new Locale("th", "TH");
        Date date = null;

        for (String format : formats) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format, defaultLocale);
                date = parser.parse(datetimeString);
                Log.d(TAG, "format:: " + format);
                break;
            } catch (ParseException e) {
                // Do nothing.
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.HOUR, 7);
        date = calendar.getTime();

////        Locale defaultLocale = Locale.getDefault();
////        Locale locale = new Locale("th", "TH");
//        Log.d(TAG, "parseDateFromString " + datetimeString);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale);
////        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
//        Date d = null;
//        d = sdf.parse(datetimeString);

        return date;
    }


//    public static String getPassDuration(Date date) {
//        return TimeUtils.millisToLongDHMS(Calendar.getInstance().getTimeInMillis() - date.getTime());
//    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences sp = AppHelper.getSharedPreference(context);
        boolean status = sp.getBoolean(Constants.LOGGED_IN_BOOL, false);

        return status;
    }


    public static void setSwipeViewRefreshing(final SwipeRefreshLayout mSwipeLayout, final boolean enabled) {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(enabled);
            }
        });

    }

    public static void showSnackbarErrorInternetConnection(final Context context, final View rootView, final View.OnClickListener onClickListener) {
//        Context context = rootView.getContext();

//        ViewGroup group = (ViewGroup) snack.getView();
//        group.setBackgroundColor(getResources().getColor(R.color.red));
//        snack.show();


        final ForegroundColorSpan whiteSpan =
                new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.white));
        SpannableStringBuilder snackbarText =
                new SpannableStringBuilder(context.getString(R.string.internet_connection_error));
        snackbarText.setSpan(whiteSpan, 0, snackbarText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        try {
            final Snackbar snackbar = Snackbar.make(rootView, snackbarText, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(context.getResources().getColor(R.color.mdtp_accent_color))
                    .setAction("ลองใหม่", onClickListener).show();

            View view = snackbar.getView().getRootView();
            if (view != null) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
            }

        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public static void showSnackbarErrorLocation(final Context context, final View rootView, final View.OnClickListener onClickListener) {
//        Context context = rootView.getContext();

//        ViewGroup group = (ViewGroup) snack.getView();
//        group.setBackgroundColor(getResources().getColor(R.color.red));
//        snack.show();


        final ForegroundColorSpan whiteSpan =
                new ForegroundColorSpan(ContextCompat.getColor(context, android.R.color.white));
        SpannableStringBuilder snackbarText =
                new SpannableStringBuilder(context.getString(R.string.location_error));
        snackbarText.setSpan(whiteSpan, 0, snackbarText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        try {
            Snackbar snackbar = Snackbar.make(rootView, snackbarText, Snackbar.LENGTH_INDEFINITE);
            snackbar.setActionTextColor(context.getResources().getColor(R.color.mdtp_accent_color))
                    .setAction("ลองใหม่", onClickListener).show();
        }
        catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public static boolean setIsLoggedIn(Context context, boolean b) {
        return AppHelper.setBoolean(context, Constants.LOGGED_IN_BOOL, b);
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreference(context);
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }


    public static boolean checkPlayServices(final Activity activity) {
        int resultCode = isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (isUserRecoverableError(resultCode)) {
                getErrorDialog(resultCode, activity,
                        Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public static boolean isCloaking(Context c) {
        return getBoolean(c, Constants.USER_CLOAKING, false);
    }

    public static void setCloaking(Activity mContext, boolean b) {
        setBoolean(mContext, Constants.USER_CLOAKING, b);
    }


    public static interface AnonymousCheck {
        public void denied();

        public void next();
    }

    public static void anonymousCheckAction(Activity activity, AnonymousCheck what) {
        if (isLoggedIn(activity)) {
            what.next();
        } else {
            what.denied();
            Intent intent = new Intent(activity, UserLoginActivity.class);
            activity.startActivity(intent);
        }
    }

    public static void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreference(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        Log.i(TAG, "Saving regId " + regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    public static int getNotification(Context context) {

        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);
        int notification = mSharedPref.getInt(Constants.NOTIFICATION, 0);
        return notification;
    }

    public static boolean setNotification(Context context, int notification) {
        SharedPreferences mSharedPref = context.getSharedPreferences(Constants.APP_PREF,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(Constants.NOTIFICATION, notification);

        return editor.commit();
    }
}
