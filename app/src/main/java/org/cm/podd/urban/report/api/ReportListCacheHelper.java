package org.cm.podd.urban.report.api;

import android.content.Context;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.cm.podd.urban.report.data.Report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nat on 11/16/15 AD.
 */
@EBean(scope = EBean.Scope.Singleton)
public class ReportListCacheHelper {

    public static final String TAG = ReportListCacheHelper.class.getSimpleName();

    @RootContext
    Context context;
    private boolean mDirty = true;

    Map<Integer, ArrayList<Report.Model>> reportModelCache = new HashMap<Integer, ArrayList<Report.Model>>();

    private long time = System.currentTimeMillis();

    public boolean isDirty() {
        return mDirty;
    }

    public ReportListCacheHelper setDirty(boolean mDirty) {
        this.mDirty = mDirty;
        return this;
    }

    public void setReportList(int id, ArrayList<Report.Model> models) {
        reportModelCache.put(id, models);
    }

//    public void getReportsByArea(int areaId, Callback<ArrayList<Report.Model>> cb) {
//        String token = AppHelper.getToken(context);
//
//        ApiService apiService = new RestClient().getApiService();
//        apiService.getReportsFeed(token, areaId, cb);
//
//    }


}
