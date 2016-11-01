package org.cm.podd.urban.report.activity.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.activity.MainActivity;
import org.cm.podd.urban.report.activity.profile.EditProfileActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.ActivityRegisterCompleteBinding;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterCompleteActivity extends BaseToolBarActivity {

    @Bind(R.id.name_edit_text)
    EditText mNameEdittext;

    ApiRequestor apiRequestor;
    ActivityRegisterCompleteBinding binding;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;

        binding = DataBindingUtil.setContentView(mActivity, R.layout.activity_register_complete);

        User.Model user = AppHelper.getUserSerialized(mContext);
        binding.setUser(user);

        ButterKnife.bind(this);
        apiRequestor = new ApiRequestor(this);

        dialog = ProgressDialog.show(RegisterCompleteActivity.this, "",
                "กรุณารอสักครู่...", true);

        dialog.setIndeterminate(true);
        dialog.hide();

        setTitle("ข้อมูลการลงทะเบียน");
    }

    @Override
    protected void onResume() {
        super.onResume();
        User.Model user = AppHelper.getUserSerialized(mContext);
        user.email = user.username;
        binding.setUser(user);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mNameEdittext.getWindowToken(), 0);

        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("RegisterComplete");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm_account) {
            saveProfile();
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_editpass)
    public void onclick() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        startActivity(intent);
    }

    public void saveProfile(){
        dialog.show();

        String firstName = mNameEdittext.getText().toString();

        if (firstName.equalsIgnoreCase("")) {
            Toast.makeText(RegisterCompleteActivity.this, "กรุณากรอกชื่อที่ใช้ในการแสดง", Toast.LENGTH_SHORT).show();
            return;
        }

        User.Model postModel = new User.Model();
        postModel.firstName = firstName;

        final Context mContext = this;

        apiRequestor.updateProfile(postModel, new Callback<User.Model>() {
            @Override
            public void success(User.Model model, Response response) {
                dialog.hide();

                BusProvider.getInstance().post(new ActivityResultEvent(TAG));
                Toast.makeText(RegisterCompleteActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                dialog.hide();

                Toast.makeText(RegisterCompleteActivity.this, "ลงทะเบียนไม่สำเร็จ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
