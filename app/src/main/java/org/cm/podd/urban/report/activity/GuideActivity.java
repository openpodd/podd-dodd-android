package org.cm.podd.urban.report.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.ButterKnife;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GuideActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    public CirclePageIndicator mIndicator;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        // Process app link data
                        if (appLinkData != null) {
                            Log.d("appLinkData", appLinkData.getTargetUri().toString());
                        }
                    }
                }
        );

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_guide);
        mContext = this;

        ArrayList<String> dataIntent = new ArrayList<String>();

        dataIntent.add("GUIDE-1");
        dataIntent.add("GUIDE-2");
        dataIntent.add("GUIDE-3");
        dataIntent.add("GUIDE-4");
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), dataIntent.size(), dataIntent);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                current_position = position;
                Button b = ButterKnife.findById(GuideActivity.this, R.id.btn_startapp);
                if (position == 3) {
                    b.setVisibility(View.VISIBLE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, UserLoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                else {
                    b.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
//        delayedHide(100);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final int num;

        private ArrayList<String> mDataSet;

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm, int num, ArrayList<String> dataSet) {
            super(fm);
            this.num = num;
            this.mDataSet = dataSet;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("GET POS:", String.valueOf(position));
            return PlaceholderFragment.newInstance(position, mDataSet.get(position));
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return num;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.guide_title_section0).toUpperCase(l);
                case 1:
                    return getString(R.string.guide_title_section1).toUpperCase(l);
                case 2:
                    return getString(R.string.guide_title_section2).toUpperCase(l);
                case 3:
                    return getString(R.string.guide_title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends android.support.v4.app.Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_ARRAY = "section_array";
        private int section_id;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_ARRAY, url);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            section_id = this.getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView;

            ImageView img;
            TextView title;
            TextView desc;
            RelativeLayout bg;

            switch (section_id) {
                case 0:
                    rootView = inflater.inflate(R.layout.fragment_first_guide, container, false);
                    img = (ImageView) rootView.findViewById(R.id.imageView1);
                    bg = (RelativeLayout) rootView.findViewById(R.id.bg_guide);
                    title = (TextView) rootView.findViewById(R.id.textViewTitle1);
                    desc = (TextView) rootView.findViewById(R.id.textViewDesc1);

                    title.setText(R.string.intro_title00);
                    desc.setText(R.string.intro_desc00);
                    img.setImageResource(R.mipmap.img_slide00);
                    bg.setBackgroundColor(Color.parseColor("#ffffff"));

                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_guide_list, container, false);
                    img = (ImageView) rootView.findViewById(R.id.imageView1);
                    bg = (RelativeLayout) rootView.findViewById(R.id.bg_guide);
                    title = (TextView) rootView.findViewById(R.id.textViewTitle1);
                    desc = (TextView) rootView.findViewById(R.id.textViewDesc1);

                    img.setImageResource(R.mipmap.img_slide01);
                    bg.setBackgroundColor(Color.parseColor("#F2B60F"));
                    title.setText(R.string.intro_title01);
                    desc.setText(R.string.intro_desc01);

                    break;
                case 2:
//                    Button b =  ButterKnife.findById(rootView, R.id.button_to_main);
//                    b.setVisibility(View.VISIBLE);
                    rootView = inflater.inflate(R.layout.fragment_guide_list, container, false);
                    img = (ImageView) rootView.findViewById(R.id.imageView1);
                    bg = (RelativeLayout) rootView.findViewById(R.id.bg_guide);
                    title = (TextView) rootView.findViewById(R.id.textViewTitle1);
                    desc = (TextView) rootView.findViewById(R.id.textViewDesc1);

                    img.setImageResource(R.mipmap.img_slide02);
                    bg.setBackgroundColor(Color.parseColor("#F47D1C"));
                    title.setText(R.string.intro_title02);
                    desc.setText(R.string.intro_desc02);

//                    b.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            SharedPreferences sp;
//                            SharedPreferences.Editor editor;
//                            sp = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
//                            editor = sp.edit();
//                            editor.putBoolean(Constants.OPEN_FIRST_TIME, false);
//                            editor.commit();
//                            Intent intent = new Intent(getActivity(), ProfileSexActivity.class);
//                            startActivity(intent);
//
//                        }
//                    });
                    break;
                case 3:
//                    Button b =  ButterKnife.findById(rootView, R.id.button_to_main);
//                    b.setVisibility(View.VISIBLE);
                    rootView = inflater.inflate(R.layout.fragment_last_guide, container, false);
                    img = (ImageView) rootView.findViewById(R.id.imageView1);
                    bg = (RelativeLayout) rootView.findViewById(R.id.bg_guide);
                    title = (TextView) rootView.findViewById(R.id.textViewTitle1);
                    desc = (TextView) rootView.findViewById(R.id.textViewDesc1);

                    img.setImageResource(R.mipmap.img_slide03);
                    bg.setBackgroundColor(Color.parseColor("#ACC617"));
                    title.setText(R.string.intro_title03);
                    desc.setText(R.string.intro_desc03);

                    break;
                default:
                    rootView = inflater.inflate(R.layout.fragment_guide_list, container, false);
                    break;

            }
            return rootView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("GuideActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
