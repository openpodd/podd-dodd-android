package org.cm.podd.urban.report.helper;

import android.content.Intent;

import org.cm.podd.urban.report.data.Report;

public class ActivityResultEvent {

    private int requestCode;
    private int resultCode;
    public String fragmentName;
    public int position;
    public  Report.Model reportModel;
    private Intent data;

    public ActivityResultEvent(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }

}
