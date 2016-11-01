package org.cm.podd.urban.report;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by nat on 10/27/15 AD.
 */
public class MyApplication extends Application {
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private Locale locale = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = BuildConfig.GOOGLE_ANALYTICS;
    private static Context context;

    public enum TrackerName {
        APP_TRACKER
    }


    public MyApplication() {
        super();
    }

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // testing
//            if (BuildConfig.FLAVOR.equals("server_production") == false) {
//            }

            analytics.setDryRun(false);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            // --------------
            Tracker t = analytics.newTracker(PROPERTY_ID);


            // You only need to set User ID on a tracker once. By setting it on the tracker, the ID will be
            // sent with all subsequent hits.
//            SharedPrefUtil sharedPrefUtil = new SharedPrefUtil(getApplicationContext());
//            t.set("&uid", sharedPrefUtil.getUserName());

            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    public void onCreate() {
        super.onCreate();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        Configuration config = getBaseContext().getResources().getConfiguration();

        Locale locale = new Locale("th", "TH");
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {

        return MyApplication.context;
    }
}
