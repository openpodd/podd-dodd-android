package org.cm.podd.urban.report.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;

public class AboutWebviewActivity extends Activity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_webview);

        mWebView = (WebView) findViewById(R.id.webview_about);
        mWebView.setBackgroundColor(0);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        mWebView.loadUrl("file:///android_asset/www/about.html");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("AboutActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
