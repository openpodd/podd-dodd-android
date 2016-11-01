package org.cm.podd.urban.report.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.protocol.ActivityInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by nat on 10/16/15 AD.
 */
public class BaseFeedFragment extends BaseFragment implements ActivityInterface,
        SwipeRefreshLayout.OnRefreshListener {
    protected Context mContext;
    protected View mRootView;

    @Bind(R.id.recyclerview) RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeLayout;


    protected ApiRequestor apiRequestor;

    public void onBindView() {
        ButterKnife.bind(this, mRootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        apiRequestor = new ApiRequestor(mContext);
    }



    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();

        onBindView();
        onSetupAdapter();
        onSetupEventListener();

        return mRootView;
    }

    public void setSwipeViewRefreshing(boolean b) {
        AppHelper.setSwipeViewRefreshing(swipeLayout, b);
    }

    @Override
    public void onRefresh() {
        setSwipeViewRefreshing(true);
    }
}
