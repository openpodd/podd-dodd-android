package org.cm.podd.urban.report.fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.MainActivity;
import org.cm.podd.urban.report.activity.report.ReportNewActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.delegate.ReportDelegate;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;
import org.cm.podd.urban.report.protocol.OnFragmentInteractionListener;

import java.util.ArrayList;

import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportFeedFragment extends BaseFeedFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = ReportFeedFragment.class.getSimpleName();
    private static ReportFeedFragment fragment;

    private OnFragmentInteractionListener mListener;
    private ApiRequestor apiRequestor;
    private Report.Adapter adapter;

    int page = 1;
    int pageSize = 20;
    int areaId = -99999;
    boolean loading = false;
    boolean end = false;

    protected Handler handler;

    LinearLayoutManager mLayoutManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReportFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportFeedFragment newInstance() {
        if (fragment != null) {
            return fragment;
        } else {
            fragment = new ReportFeedFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
        }
        return fragment;
    }

    public ReportFeedFragment() {
        // Required empty public constructor
    }

    ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Subscribe
    public void onFragmentEvent(ActivityResultEvent event) {
        // Do something when any event on fragment was happened

        if (event.reportModel == null) return;

        adapter.updateReport(event.reportModel, event.position);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        Tracker tracker = ((MyApplication) getActivity().getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ReportFeedView");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
    }

    TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getActivity();
        mRootView = inflater.inflate(R.layout.fragment_feed, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        emptyText = (TextView) mRootView.findViewById(R.id.emptyText);
        if (!hasNetworkConnection(getActivity())) {
            emptyText.setVisibility(View.VISIBLE);
        }
        BusProvider.getInstance().register(this);
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onBindView() {
        super.onBindView();
    }

    @Override
    public void onSetupAdapter() {
        super.onSetupAdapter();

        mLayoutManager = new LinearLayoutManager(getActivity());

        apiRequestor = new ApiRequestor(mContext);
        adapter = new Report.Adapter(mContext);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                // check scroll down;
                if(dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount){
                        if (!loading) loadMore();
                    }
                }


            }
        });

        onRefresh();
    }


    @Override
    public void onSetupEventListener() {
        super.onSetupEventListener();
        adapter.setCallback(new ReportDelegate(getActivity(), mRootView));
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        adapter.clearDataSet();

        areaId = -99999;

        try {
            areaId = ((MainActivity) getActivity()).getAdminAreaId();
        } catch (NullPointerException ex) {
            // Do nothing
        }

        if (areaId == -99999) {
            AppHelper.setSwipeViewRefreshing(swipeLayout, false);
            return;
        }

        emptyText.setVisibility(View.INVISIBLE);

        page = 1;
        end = false;
        pastReport = null;

        getReports(areaId, page, pageSize, true);

        AdministrationArea.Model area = ((MainActivity) getActivity()).getAdminArea();
        ((MainActivity) getActivity()).getHotReport(area, false);
    }

    public void loadMore () {
        if (end) return;

        page += 1;
        AppHelper.setSwipeViewRefreshing(swipeLayout, true);

        getReports(areaId, page, pageSize, false);
    }

    ArrayList<Report.Model> pastReport;

    public void getReports(int areaId, int page, int pageSize, final boolean resetPosition) {
        loading = true;
        apiRequestor.getReportsByArea(areaId, page, pageSize, new Callback<ArrayList<Report.Model>>() {
            @Override
            public void success(ArrayList<Report.Model> models, Response response) {
                // hot fix: problem when feed order change, To DO: fix it
                if (pastReport != null && pastReport.size() > 0 && models.size() > 0) {
                    end = pastReport.get(0).id == models.get(0).id;
                }

                if (models.size() == 0) end = true;
                int offset = adapter.getItemCount();

                if (end) {
                    if (offset + models.size() == 0) emptyText.setVisibility(View.VISIBLE);
                    AppHelper.setSwipeViewRefreshing(swipeLayout, false);
                    return;
                }

                for (int i = 0; i < models.size(); i++) {
                    adapter.add(models.get(i), offset + i);
                }

                pastReport = models;
                emptyText.setVisibility(View.INVISIBLE);

                if (resetPosition && recyclerView != null)
                    recyclerView.scrollToPosition(0);

                AppHelper.setSwipeViewRefreshing(swipeLayout, false);
                loading = false;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                AppHelper.setSwipeViewRefreshing(swipeLayout, false);
                AppHelper.showSnackbarErrorInternetConnection(mContext, mRootView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
                emptyText.setVisibility(View.VISIBLE);
                loading = false;
            }
        });
    }


    public void refreshReport() {
        onRefresh();
    }

    @OnClick(R.id.report_buttonview)
    public void onClickReportButton(View v) {
        AppHelper.anonymousCheckAction(getActivity(), new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Intent i = new Intent(mContext, ReportNewActivity.class);
                startActivity(i);
            }
        });
    }
}
