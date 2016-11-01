package org.cm.podd.urban.report.activity;

import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.ActivityEditPasswordBinding;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.BusProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditPasswordActivity extends BaseToolBarActivity {
    @Bind(R.id.password_edit_text) EditText mPasswordEdittext;
    @Bind(R.id.confirm_password_edit_text) EditText mConfirmPasswordEdittext;

    ApiRequestor apiRequestor;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityEditPasswordBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_password);
        ButterKnife.bind(this);

        apiRequestor = new ApiRequestor(this);

        dialog = ProgressDialog.show(EditPasswordActivity.this, "",
                "กรุณารอสักครู่...", true);

         dialog.setIndeterminate(true);
         dialog.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_password) {
            savePassword();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }

    public void savePassword(){
        dialog.show();
        String password = mPasswordEdittext.getText().toString();
        String confirmPassword = mConfirmPasswordEdittext.getText().toString();

        if (password.equalsIgnoreCase(confirmPassword)) {
            apiRequestor.updatePassword(password, new Callback<User.Model>() {

                @Override
                public void success(User.Model model, Response response) {
                    Log.e(TAG, "Success");
                    dialog.hide();
                    BusProvider.getInstance().post(new ActivityResultEvent(TAG));
                    Toast.makeText(EditPasswordActivity.this, "เปลี่ยนรหัสผ่านสำเร็จแล้ว", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 200);
                }

                @Override
                public void failure(RetrofitError error) {
                    dialog.hide();
                    Log.e(TAG, error.getMessage());
                    Toast.makeText(EditPasswordActivity.this, "คุณยังไม่ได้ตั้งรหัสผ่านใหม่", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dialog.hide();
            Toast.makeText(EditPasswordActivity.this, "รหัสผ่านไม่ตรงกัน หรือคุณยังกรอกรหัสผ่านไม่ครบ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("EditPasswordActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());

    }
}
