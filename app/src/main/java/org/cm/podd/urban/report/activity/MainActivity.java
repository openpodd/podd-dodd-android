package org.cm.podd.urban.report.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.HotReport;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.ActivityMainBinding;
import org.cm.podd.urban.report.fragment.MyMapFragment;
import org.cm.podd.urban.report.fragment.NotificationFragment;
import org.cm.podd.urban.report.fragment.ProfileFragment;
import org.cm.podd.urban.report.fragment.ReportFeedFragment;
import org.cm.podd.urban.report.fragment.SettingsFragment;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.Constants;
import org.cm.podd.urban.report.helper.LocationHelper;
import org.cm.podd.urban.report.helper.NetworkHelper;
import org.cm.podd.urban.report.helper.NetworkHelper_;
import org.cm.podd.urban.report.protocol.ActivityInterface;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;
import org.cm.podd.urban.report.service.GcmBroadcastReceiver;
import org.cm.podd.urban.report.service.GcmIntentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mylibrary.dialog.ListItemDialog;
import mylibrary.dialog.OkDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;


public class MainActivity extends BaseToolBarActivity
        implements ActivityInterface, OnFragmentInteractionListener, ListItemDialog.onUserSubmit {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */


    public static final String TAG = MainActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private int[] tabIcons = {
            R.mipmap.ic_menu_map,
            R.mipmap.ic_menu_feed,
            R.mipmap.ic_menu_profile,
            R.mipmap.ic_menu_noti,
            R.mipmap.ic_menu_setting
    };

    List<AdministrationArea.Model> mAdminAreaList;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    @Bind(R.id.dragView)
    LinearLayout mDragViewLinearLayout;
    @Bind(R.id.list)
    ScrollView scrollView;
    @Bind(R.id.choose_amphur_button)
    Button chooseButton;

    private SlidingUpPanelLayout.PanelState panelState;

    private ApiRequestor apiRequestor;
    private ActivityMainBinding binder;

    private int mAmpureId = -99999;
    private int mListId = -99999;
    private AdministrationArea.Model mAmpure = null;

    private String selectedAreaJson;
    private Gson gson;
    private MyMapFragment mMyMapFragment;
    private ReportFeedFragment mReportFragment;

    protected BroadcastReceiver mSyncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeNotification();
        }
    };

    private static Tracker tracker;

    private CustomListItemDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;

        binder = DataBindingUtil.setContentView(this, R.layout.activity_main);
        apiRequestor = new ApiRequestor(mContext);
        gson = new Gson();

        getAdminAreaList();

        initSummary();

        updatePoddDevice();

        changeNotification();

        registerReceiver(mSyncReceiver, new IntentFilter(GcmIntentService.SYNC));

        tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);

        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
//                Log.d("SlideUp", slideOffset + "");
            }

            @Override
            public void onPanelCollapsed(View panel) {
                scrollView.scrollTo(0, 0);
            }

            @Override
            public void onPanelExpanded(View panel) {
                tracker.setScreenName("DashboardView");
                tracker.send(new HitBuilders.AppViewBuilder().build());
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBindView() {
        ButterKnife.bind(this);
        super.onBindView();
        Log.i(TAG, "onBindView");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mViewPager = (ViewPager) findViewById(R.id.container);



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1 || position == 0) {
                    ButterKnife.findById(mActivity, R.id.choose_amphur_button_linearlayout_wrapper).setVisibility(View.VISIBLE);
                } else {
                    ButterKnife.findById(mActivity, R.id.choose_amphur_button_linearlayout_wrapper).setVisibility(View.GONE);
                }

                if (position == 2) {
                    setTitle("ข้อมูลส่วนตัว");

                    tracker.setScreenName("MyProfileView");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                } else if (position == 3) {
                    setTitle("แจ้งเตือน");

                    AppHelper.setNotification(mActivity, 0);
                    changeNotification();

                    tracker.setScreenName("NotificationView");
                    tracker.send(new HitBuilders.AppViewBuilder().build());

                } else if (position > 3) {
                    setTitle("ตั้งค่า");

                    tracker.setScreenName("SettingView");
                    tracker.send(new HitBuilders.AppViewBuilder().build());
                }

                if (position != 0) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                        }
                    });

                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSlidingUpPanelLayout.setVisibility(View.VISIBLE);
                            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    });
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Intent prevIntent = getIntent();
        if (prevIntent != null) {
            String target = ReportFeedFragment.class.getSimpleName();
            String extra = prevIntent.getStringExtra(Report.INTENT_TARGET_TAG);

            if (extra != null) {
                if (target.equals(extra)) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(1, true);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onSetupAdapter() {
        // Set up the ViewPager with the sections adapter.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {

        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            tabLayout.getTabAt(3).setIcon(tabIcons[3]);
            tabLayout.getTabAt(4).setIcon(tabIcons[4]);
        } catch (NullPointerException exception) {
            Toast.makeText(MainActivity.this, "เครื่องไม่รองรับการใช้งาน", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeNotification() {
        try {
            int notification = AppHelper.getNotification(mContext);
            if (notification == 1) {
                tabLayout.getTabAt(3).setIcon(R.mipmap.ic_menu_noti_alert);
            } else {
                tabLayout.getTabAt(3).setIcon(R.mipmap.ic_menu_noti);
            }
        } catch (NullPointerException exception) {
//            Toast.makeText(MainActivity.this, "เครื่องไม่รองรับการใช้งาน", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetupEventListener() {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(TAG, "onFragmentInteraction (line 105): ");
    }

    @Override
    public void onSubmitted(int which, String str) {
        mListId = which;
        setSelectedArea(mAdminAreaList.get(which));
    }

    public int getAdminAreaId() {
        return mAmpureId;
    }

    public int getAreaId() {
        return mListId;
    }

    public AdministrationArea.Model getAdminArea() {
        return mAmpure;
    }

    public void setChooseButton(boolean enable) {
        chooseButton.setEnabled(enable);
    }

    public void setSelectedArea(final AdministrationArea.Model model) {
        chooseButton.setText(model.name);

        if (mAmpureId == model.id)
            return;

        if (!hasNetworkConnection(MainActivity.this)) {
            AppHelper.showSnackbarErrorInternetConnection(mContext,
                    findViewById(android.R.id.content), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setSelectedArea(model);
                        }
                    });
            return;
        }

        selectedAreaJson = gson.toJson(model, AdministrationArea.Model.class);
        AppHelper.setString(mContext, Constants.ADMIN_AREAD_SERIALIZED, selectedAreaJson);

        if (mMyMapFragment != null) {
            mMyMapFragment.clearMap();
        }

        getHotReport(model, true);

        mAmpureId = model.id;
        mAmpure = model;

        if (mReportFragment != null) {
            mReportFragment.refreshReport();
        }

    }

    public Task<Integer> getAdminAreaList(ApiRequestor apiRequestor) {
        final Task<Integer>.TaskCompletionSource tcs = Task.create();
        tcs.setResult(1);
        return tcs.getTask();
    }

    public void initSummary() {
        Report.SummaryModel summary = new Report.SummaryModel();
        summary.total = "0";
        for (int i = 0; i < 3; i++) {
            summary.reports[i] = "0";
            summary.texts[i] = "ยังไม่มีข้อมูล";
        }
        summary.reportTotal = 0;
        summary.humanCount = 0;
        summary.animalCount = 0;
        summary.envCount = 0;

        binder.setSummary(summary);
    }

    public void moveCamera() {

        if (mListId == -99999) return;

        LatLng latLng = new LatLng(mAmpure.location.coordinates.get(1), mAmpure.location.coordinates.get(0));
        mMyMapFragment.moveCamera(latLng, mAmpure.id);

        if (Constants.mAdminAreaList.get(mListId).mpoly != null) {
            List<List<List<Double>>> coordinates = Constants.mAdminAreaList.get(mListId).mpoly.coordinates;
            mMyMapFragment.drawBoundary(coordinates.get(0));
        }

    }

    public void getHotReport(final AdministrationArea.Model model, final boolean draw) {

        String withMpoly = "true";

        if (draw) {
            if (mMyMapFragment != null)
                mMyMapFragment.clearMap();
            chooseButton.setEnabled(false);

            if (mListId != -99999 && Constants.mAdminAreaList.get(mListId).mpoly != null) {
                withMpoly = "";
            }

            if(mViewPager != null && (mViewPager.getCurrentItem() == 0 || mViewPager.getCurrentItem() == 1)) {
                boolean showLoadingDialog =  mViewPager.getCurrentItem() == 0;
                mMyMapFragment.loadingMap(showLoadingDialog);
            }
        }

        final int id = model.id;
        if (id == mAmpureId && draw)
            return;

        if (Constants.mAdminAreaList == null || mAdminAreaList.size() == 0)
            return;

        mAmpureId = model.id;
        mAmpure = model;

        chooseButton.setEnabled(false);

        apiRequestor.getHotReport(id, withMpoly, new Callback<HotReport.Model>() {
            @Override
            public void success(HotReport.Model hotReport, Response response) {
                chooseButton.setEnabled(true);

                Report.SummaryModel summary = new Report.SummaryModel();
                summary.total = String.valueOf(hotReport.reportCount);
                for (int i = 0; i < hotReport.hotReportType.size(); i++) {
                    HotReport.Model.HotReportTypeEntity hotReportItem =
                            hotReport.hotReportType.get(i);
                    summary.reports[i] = String.valueOf(hotReportItem.count);
                    summary.texts[i] = String.valueOf(hotReportItem.type);
                }
                summary.reportTotal = hotReport.reportCount;
                summary.humanCount = Integer.parseInt(hotReport.getHumanCategoryCount());
                summary.animalCount = Integer.parseInt(hotReport.getAnimalCategoryCount());
                summary.envCount = Integer.parseInt(hotReport.getEnvCategoryCount());

                binder.setSummary(summary);

                if (mListId == -99999) return;

                if (hotReport.mpoly != null && Constants.mAdminAreaList.get(mListId).mpoly == null && !(model.id == -1|| model.id == 311)) {
                    Constants.mAdminAreaList.get(mListId).mpoly = hotReport.getPolygonEntity();

                }

                mMyMapFragment.getReportMarker();
            }

            @Override
            public void failure(RetrofitError error) {
                chooseButton.setEnabled(true);
                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getHotReport(model, draw);
                            }
                        });

                mMyMapFragment.dismissLoadingMap();
            }
        });

    }

    public void getCurrentAdminArea() {
        final LocationHelper locationHelper = LocationHelper.getInstance_(mContext);
        final Location currentLocation = locationHelper.getCurrentLocation();

        if (currentLocation != null) {
            getAdminArea(currentLocation.getLatitude(), currentLocation.getLongitude());
            locationHelper.disconnect();
        } else {
//            Toast.makeText(MainActivity.this, "ไม่สามารถระบุตำแหน่งของคุณได้", Toast.LENGTH_SHORT).show();
            locationHelper.disconnect();
            locationHelper.setOnLocationChanged(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "On Location Changed (line 387): ");
                    getAdminArea(location.getLatitude(), location.getLongitude());
                    locationHelper.disconnect();
                }
            });
            locationHelper.connect();
        }
    }

    public void getAdminArea(final double lat, final double lng) {
        if (mAdminAreaList == null) return;

        apiRequestor.getAdministrationArea(lat, lng, new Callback<List<AdministrationArea.Model>>() {
            @Override
            public void success(List<AdministrationArea.Model> models, Response response) {

                if (models.size() == 0) {
                    Toast.makeText(MainActivity.this, "คุณอยู่นอกพื้นที่เชียงใหม่", Toast.LENGTH_SHORT).show();
                    OkDialog dialog = new OkDialog(mContext);

                    dialog.setMessage(getString(R.string.dialogue_offchiangmai));
                    Log.d(TAG, "get selected id line 421: ");
                    setSelectedArea(mAdminAreaList.get(0));
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListId = 0;
                            setSelectedArea(mAdminAreaList.get(0));
                        }
                    }, 500);

                } else {
                    Button button = ButterKnife.findById(mActivity, R.id.choose_amphur_button);
                    button.setText(models.get(0).name);
                    for (int i = 0; i < Constants.mAdminAreaList.size(); i++) {
                        if (Constants.mAdminAreaList.get(i).id == models.get(0).id) {
                            mListId = i;
                            break;
                        }
                    }

                    setSelectedArea(models.get(0));
                }

            }

            @Override
            public void failure(RetrofitError error) {
                AppHelper.showSnackbarErrorInternetConnection(mContext, findViewById(android.R.id.content), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAdminAreaList();
                        getAdminArea(lat, lng);
                    }
                });

            }
        });
    }

    public AdministrationArea.Model getAreaAll() {

        AdministrationArea.Model.LocationEntity loc = new AdministrationArea.Model.LocationEntity();
        loc.type = "Point";
        List<Double> coordinates = new ArrayList<Double>();
        coordinates.add(99.039693);
        coordinates.add(18.705308);
        loc.coordinates = coordinates;

        AdministrationArea.Model area_all = new AdministrationArea.Model();
        area_all.id = -1;
        area_all.name = "จังหวัดเชียงใหม่";
        area_all.code = "public_1_chiang_mai";
        area_all.location = loc;
        area_all.weight = -10;

        return area_all;

    }

    private boolean update = true;

    public void getAdminAreaList() {
        dialog = new CustomListItemDialog();
        update = true;
        if (Constants.mAdminAreaList != null) {
            update = false;
            mAdminAreaList = Constants.mAdminAreaList;
            dialog.builder(mContext)
                    .setItems(Constants.spinnerSubs)
                    .setCallback(MainActivity.this);
            getCurrentAdminArea();

        }

        apiRequestor.getAdministrationArea(new Callback<List<AdministrationArea.Model>>() {
            @Override
            public void success(List<AdministrationArea.Model> models, Response response) {
                mAdminAreaList = models;
                mAdminAreaList.add(getAreaAll());

                final String[] spinnerSubs = new String[mAdminAreaList.size()];
                int[] idList = new int[mAdminAreaList.size()];

                Collections.sort(models, new Comparator<AdministrationArea.Model>() {
                    @Override
                    public int compare(AdministrationArea.Model m1, AdministrationArea.Model m2) {
                        String m1Compare = m1.name.replace("เ", "").replace("แ", "").replace("โ", "").replace("ไ", "").replace("ใ", "");
                        String m2Compare = m2.name.replace("เ", "").replace("แ", "").replace("โ", "").replace("ไ", "").replace("ใ", "");
                        return 100 * (m1.weight - m2.weight) + m1Compare.compareTo(m2Compare);
                    }
                });

                for (int i = 0; i < models.size(); i++) {
                    spinnerSubs[i] = models.get(i).name;
                    idList[i] = models.get(i).id;
                }

                Constants.mAdminAreaList = mAdminAreaList;
                Constants.spinnerSubs = spinnerSubs;

                dialog.builder(mContext)
                        .setItems(spinnerSubs)
                        .setCallback(MainActivity.this);

                if (update) {
                    getCurrentAdminArea();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getAdminAreaList();
                            }
                        });
            }
        });


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                Log.d(TAG, "POS: 0 (line ): ");
                mMyMapFragment = MyMapFragment.newInstance();
                return mMyMapFragment;
            } else if (position == 1) {
                mReportFragment = ReportFeedFragment.newInstance();
                return mReportFragment;
            } else if (position == 2) {
                return ProfileFragment.newInstance();
            } else if (position == 3) {
                return NotificationFragment.newInstance();
            } else {
                return SettingsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @OnClick(R.id.choose_amphur_button)
    public void onClickChooseAmphur(View v) {
        try {
            dialog.show("เลือกจังหวัด");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    public void updatePoddDevice() {

        final String regId = AppHelper.getRegistrationId(this);

        if (regId.isEmpty()) {
            NetworkHelper networkHelper = NetworkHelper_.getInstance_(mContext);
            networkHelper.registerGCMDeviceInBackground(new NetworkHelper.NetworkCallback() {
                @Override
                public void onRegisterd(String str) {
                    Log.d(TAG, "onRegisterd - " + str);
                    Log.d(TAG, "onRegisterd - " + str);
                    Log.d(TAG, "onRegisterd - " + str);
                    AppHelper.storeRegistrationId(mContext, str);
                }
            });
        }

        HashMap<String, String> myMap = new HashMap<String, String>();
        myMap.put("deviceId", AppHelper.getDeviceId(mContext));
        myMap.put("gcmRegId", regId);
        myMap.put("model", Build.MODEL);
        myMap.put("brand", Build.BRAND);
        TypedInput ti = AppHelper.getTypedInputFromHashMap(mContext, myMap);

        apiRequestor.updateAndroidDevice(ti, new Callback<User.Model>() {
            @Override
            public void success(User.Model profile, Response response) {
                Log.d("updatePoddDevice", "success profile (line ): " + regId);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("updatePoddDevice", "failed profile (line ): " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        changeNotification();

        tracker.setScreenName("MainActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSyncReceiver);
    }

    @SuppressLint("ValidFragment")
    public static class CustomListItemDialog extends ListItemDialog {

        @Override
        public void show(String tag) {
            super.show(tag);
            tracker.setScreenName("FilterView");
            tracker.send(new HitBuilders.AppViewBuilder().build());
        }
    }
}
