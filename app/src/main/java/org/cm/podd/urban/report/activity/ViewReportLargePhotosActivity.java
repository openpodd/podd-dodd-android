package org.cm.podd.urban.report.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.helper.Constants;

import java.util.ArrayList;
import java.util.Locale;


public class ViewReportLargePhotosActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    public CirclePageIndicator mIndicator;
    int current_position = 0;
    public static final String TAG = ViewReportLargePhotosActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.content_view_report_large_photos);

        Intent i = getIntent();


        ArrayList<String> dataIntent = i.getStringArrayListExtra(Constants.REPORT_IMAGE_URLS_EXTRA_STRING_ARRAY);


        Log.d(TAG, " (line ): ");
        Log.d(TAG, " data intent (line ): " + dataIntent.size());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), dataIntent.size(), dataIntent);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current_position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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
//            Toast.makeText(ViewReportLargePhotosActivity.this, "TAB" + position, Toast.LENGTH_SHORT).show();
//            switch (position) {
//                case 0:
//                    return getString(R.string.title_section1).toUpperCase(l);
//                case 1:
//                    return getString(R.string.title_section2).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
//            }
            return "TITLE";
//            return null;
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

            View rootView = inflater.inflate(R.layout.fragment_large_photo, container, false);

            Log.d("section id", String.valueOf(section_id));

            final ImageView img = (ImageView) rootView.findViewById(R.id.imageView1);
            final String url = this.getArguments().getString(ARG_SECTION_ARRAY);

//            final Point size = AppHelper.getS(getActivity());
            Glide.with(getActivity()).load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(img);
            return rootView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ViewFullImageActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}

