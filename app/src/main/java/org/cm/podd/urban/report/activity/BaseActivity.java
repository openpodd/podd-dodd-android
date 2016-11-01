package org.cm.podd.urban.report.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;

import com.facebook.FacebookSdk;

import org.cm.podd.urban.report.helper.TypefaceUtil;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        initialize();
    }

    protected void initialize() {

        TypefaceUtil.overrideFont(getApplicationContext(), "SANS_SERIF", "fonts/boon-400.otf");
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/boon-500.otf");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
//        conf.locale = new Locale("th");
        res.updateConfiguration(conf, dm);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

}
