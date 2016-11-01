package org.cm.podd.urban.report.activity.report;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.adapter.ReportTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ReportNewActivity extends BaseToolBarActivity {
    private RecyclerView recyclerview;
    private ReportTypeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_type);
        SwipeRefreshLayout swipeRefreshLayout = ButterKnife.findById(this, R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

    }


    @Override
    public void onBindView() {
        super.onBindView();
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onSetupAdapter() {
        List<ReportTypeAdapter.Item> data = new ArrayList<>();


//        URIBuilder builder = new URIBuilder();
//        builder.

        //HUMAN
        ReportTypeAdapter.Item header = new ReportTypeAdapter.Item(ReportTypeAdapter.HEADER, mContext, R.string.IC_CAT_HUMAN);
        header.invisibleChildren = new ArrayList<>();
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_FOOD_POISON));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_FOOD_DIRTY_SHOP));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_FOOD_CONTAMINATED_SUSPECTED));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_DRUG_FAKE_DRUG));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_DRUG_NON_FDA));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_HUMAN_DANGER_COSMETIC));
        data.add(header);

        // ANIMAL
        header = new ReportTypeAdapter.Item(ReportTypeAdapter.HEADER, mContext, (R.string.IC_CAT_ANIMAL));
        header.invisibleChildren = new ArrayList<>();
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ANIMAL_BITTEN));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ANIMAL_DEAD));
        data.add(header);


        // ENVIRONMENT
        header = new ReportTypeAdapter.Item(ReportTypeAdapter.HEADER, mContext, R.string.IC_CAT_ENVI);
        header.invisibleChildren = new ArrayList<>();
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_NOISY));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_GARBAGE));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_DIRTY_WATER));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_MOSQUITO_PROPAGATE));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_SMOG));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_FIRE));
        header.invisibleChildren.add(new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, mContext, R.string.IC_CAT_ENVI_FLOOD));
        data.add(header);

        adapter = new ReportTypeAdapter(data);
        recyclerview.setAdapter(adapter);
    }

    @Override
    public void onSetupEventListener() {
        Log.d(TAG, "onSetupEventListener (line 67): ");
        recyclerview.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.d(TAG, "onTouchEvent (line 75): ");

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ReportNewActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
