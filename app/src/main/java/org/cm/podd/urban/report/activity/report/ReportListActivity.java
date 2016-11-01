package org.cm.podd.urban.report.activity.report;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.data.Report;

import java.util.ArrayList;
import java.util.List;

public class ReportListActivity extends BaseToolBarActivity {

    private RecyclerView recyclerview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
    }

    @Override
    public void onSetupAdapter() {

        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        List<Report.Model> data = new ArrayList<>();

        Report.Adapter adapter = new Report.Adapter(mContext);
        recyclerview.setAdapter(adapter);
        adapter.add(new Report.Model(), 0);
        adapter.add(new Report.Model(), 1);
        adapter.add(new Report.Model(), 2);
        adapter.add(new Report.Model(), 3);
        adapter.add(new Report.Model(), 4);

    }

    @Override
    public void onSetupEventListener() {


    }

    @Override
    public void onBindView() {
        super.onBindView();
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ReportListActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
