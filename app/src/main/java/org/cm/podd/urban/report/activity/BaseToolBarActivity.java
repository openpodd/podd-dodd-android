package org.cm.podd.urban.report.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.protocol.ActivityInterface;

public abstract class BaseToolBarActivity extends BaseActivity implements ActivityInterface{
    private boolean hasToolbar;
    protected Context mContext;
    protected Activity mActivity;
    protected Toolbar toolbar;
    protected CoordinatorLayout coordinatorLayout;
    public static final String TAG = BaseToolBarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasToolbar(true);
    }

    private void setUpAllSequence() {
        mContext = this;
        onBindView();
        onSetupAdapter();
        onSetupEventListener();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setUpAllSequence();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setUpAllSequence();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setUpAllSequence();
    }

    public void setHasToolbar(boolean hasToolbar) {
        this.hasToolbar = hasToolbar;
    }

//    public void onSetupToolBar() {
//        Log.i(TAG, "onSetupToolBar");
//        if (hasToolbar) {
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 200);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinateLayoutRootView);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
