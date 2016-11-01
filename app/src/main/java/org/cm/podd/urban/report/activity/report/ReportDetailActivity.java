package org.cm.podd.urban.report.activity.report;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.otto.Subscribe;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.adapter.CommentAdapter;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.Comment;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.PostModel.*;
import org.cm.podd.urban.report.databinding.ActivityReportDetailBinding;
import org.cm.podd.urban.report.delegate.ReportDelegate;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;

public class ReportDetailActivity extends BaseToolBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    protected Context mContext;
    protected View mRootView;
    private ActivityReportDetailBinding binding;


    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeLayout;

    @Bind(R.id.comment_edittextview)
    EditText mCommentEditText;

    @Bind(R.id.recyclerview_relaytive_wrapper_drag_view)
    RelativeLayout parentRecycle;

    private ApiRequestor apiRequestor;
    private CommentAdapter adapter;
    private Report.Model reportModel;
    private boolean postComment = false;

    ReportDelegate mReportDelegate;
    private int reportPosition;
    private ActivityResultEvent mEvent;

    private int reportId;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
        mContext = this;
        mActivity = this;
        mReportDelegate = new ReportDelegate(mActivity, mRootView);
        Intent prevIntent = getIntent();

        String reportModelJson = prevIntent.getStringExtra(Report.REPORT_MODEL_KEY);

        reportPosition = prevIntent.getIntExtra(Report.REPORT_POSITION_KEY, 0);
        reportId = prevIntent.getIntExtra(Report.REPORT_ID_KEY, -1);

        if (reportId != -1) {
            getReportDetail();

        } else {
            reportModel = (Report.Model) AppHelper.deSerialize(mContext, reportModelJson, Report.Model.class);
            binding = DataBindingUtil.setContentView(mActivity,
                    R.layout.activity_report_detail);
            ButterKnife.bind(this);
            binding.setReport(reportModel);

            TextView emptyText = (TextView) findViewById(R.id.emptyText);
            emptyText.setVisibility(View.INVISIBLE);

            reportId = reportModel.id;
        }


        if (!hasNetworkConnection(mActivity)) {
            AppHelper.showSnackbarErrorInternetConnection(mContext,
                    findViewById(android.R.id.content), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getReportDetail();
                        }
                    });
        }

        setToolbar();
    }

    public void getReportDetail() {
        apiRequestor = new ApiRequestor(mContext);
        apiRequestor.getReportDetail(reportId, new Callback<Report.Model>() {
            @Override
            public void success(Report.Model model, Response response) {
                reportModel = model;
                binding = DataBindingUtil.setContentView(mActivity,
                        R.layout.activity_report_detail);
                ButterKnife.bind(mActivity);
                binding.setReport(reportModel);


                setToolbar();

                onRefresh();
            }

            @Override
            public void failure(RetrofitError error) {

                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getReportDetail();
                            }
                        });
            }
        });
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("รายละเอียด");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 200);
                }
            });

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);


//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public void onSetupAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        apiRequestor = new ApiRequestor(mContext);

        onRefresh();

    }

    @Override
    public void onSetupEventListener() {
        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        final ArrayList<CommentAdapter.Item> dataSet = new ArrayList<>();
        dataSet.add(0, new CommentAdapter.Item(CommentAdapter.HEADER, reportModel));
        apiRequestor.getComments(reportModel.id, new Callback<ArrayList<Comment.Model>>() {
            @Override
            public void success(final ArrayList<Comment.Model> models, Response response) {
//                adapter.clearDataSet();
                for (int i = models.size() - 1; i >= 0; i--) {
                    Log.d(TAG, "SUCCESS (line ): " + i);
                    dataSet.add(new CommentAdapter.Item(CommentAdapter.CHILD, models.get(i), reportModel));
                }

                adapter = new CommentAdapter(dataSet);
                adapter.setListener(mReportDelegate);
                recyclerView.setAdapter(adapter);
                AppHelper.setSwipeViewRefreshing(swipeLayout, false);

                if (postComment) {
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    postComment = false;
                }

            }

            @Override
            public void failure(RetrofitError error) {
                AppHelper.showSnackbarErrorInternetConnection(mContext, mRootView, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRefresh();
                    }
                });
            }
        });
    }

    @Subscribe
    public void onFragmentEvent(ActivityResultEvent event) {
        mEvent = event;
        // Do something when any event on fragment was happened
        reportModel = event.reportModel;
        binding.setReport(reportModel);

        getReportDetail();

    }

    public void onBindView() {
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
    }


    @OnClick(R.id.comment_submit_button)
    public void onClick(View v) {

        AppHelper.anonymousCheckAction(ReportDetailActivity.this, new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Toast.makeText(ReportDetailActivity.this, "สำเร็จ", Toast.LENGTH_SHORT).show();
                SupportPostModel postObj = new SupportPostModel();
                postObj.reportId = reportModel.id;
                postObj.isMeToo = reportModel.meTooId != 0;
                postObj.message = mCommentEditText.getText().toString();
                postObj.isLike = reportModel.likeId != 0;

                apiRequestor.supportReport(postObj, new Callback<Report.Model>() {
                    @Override
                    public void success(Report.Model model, Response response) {
                        postComment = true;
                        mCommentEditText.setText("");

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mCommentEditText.getWindowToken(), 0);

                        Toast.makeText(ReportDetailActivity.this, "สำเร็จ", Toast.LENGTH_SHORT).show();
                        reportModel.commentCount++;
                        binding.setReport(reportModel);

                        //TODO: FIX THIS
                        ActivityResultEvent event = new ActivityResultEvent(TAG);
                        if (mEvent != null) {
                            event.position = mEvent.position;
                            event.reportModel = reportModel;
                        }
                        else {
                            event.position = reportPosition;
                            event.reportModel = reportModel;
                        }

                        BusProvider.getInstance().postQueue(event);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(mContext, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ReportDetailActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }
}
