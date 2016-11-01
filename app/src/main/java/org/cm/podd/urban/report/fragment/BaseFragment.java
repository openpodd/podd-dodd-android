package org.cm.podd.urban.report.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends android.support.v4.app.Fragment {
    public Context mContext;
    public Activity mActivity;
    public View mRootView;

    public Tracker pTracker;



    public BaseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        pTracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
