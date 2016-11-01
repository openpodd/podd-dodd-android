package org.cm.podd.urban.report.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.helper.AppHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginCodeActivity extends BaseToolBarActivity {

    @Bind(R.id.email_text)
    TextView emailText;

    @Bind(R.id.uid_text)
    TextView uidText;

    @Bind(R.id.password_edit_text)
    EditText passwordEditText;

    ApiRequestor apiRequestor;

    private String email;
    private String uid;
    private String token;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_code);

        ButterKnife.bind(this);

        apiRequestor = new ApiRequestor(this);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");
        token = intent.getStringExtra("token");

        emailText.setText(email);
        uidText.setText(uid);


        dialog = ProgressDialog.show(LoginCodeActivity.this, "",
                "กรุณารอสักครู่...", true);

        dialog.setIndeterminate(true);
        dialog.hide();
    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }

    public void confirm(){
        dialog.show();

        Log.d("LoginCodeActivity", "Click ");
        String password = passwordEditText.getText().toString();

        apiRequestor.loginTempPassword(password, uid, token, new Callback<User.Model>() {
            @Override
            public void success(User.Model model, Response response) {
                dialog.hide();

                Toast.makeText(LoginCodeActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();

                AppHelper.setIsLoggedIn(mContext, true);
                AppHelper.setToken(mContext, model.token);
                AppHelper.setUserSerialized(mContext, model);

                Intent i = new Intent(mContext, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }

            @Override
            public void failure(RetrofitError error) {
                dialog.hide();

                AppHelper.showSnackbarErrorInternetConnection(mContext, findViewById(android.R.id.content), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm();
                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("LoginCodeActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @OnClick(R.id.confirm_button)
    public void onClick(View v){
        confirm();
    }
}
