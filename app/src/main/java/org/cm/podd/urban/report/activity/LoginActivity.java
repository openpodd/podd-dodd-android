package org.cm.podd.urban.report.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class LoginActivity extends BaseToolBarActivity {

    @Bind(R.id.email_edittext)
    EditText mEmailEditText;
    @Bind(R.id.password_edittext)
    EditText mPasswordEditText;

    ApiRequestor apiRequestor;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        apiRequestor = new ApiRequestor(this);

        dialog = ProgressDialog.show(LoginActivity.this, "",
                "กรุณารอสักครู่...", true);

        dialog.setIndeterminate(true);
        dialog.hide();

        setTitle("เข้าสู่ระบบ");
    }

    @Override
    public void onSetupAdapter() {

    }

    @Override
    public void onSetupEventListener() {

    }

    @OnClick(R.id.login_button)
    public void onClick(View v) {
        login();
    }

    private void login() {
        dialog.show();

        ApiRequestor apiRequestor = new ApiRequestor(this);
        apiRequestor.loginByPassword(
                mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString(),
                new Callback<User.Model>() {
                    @Override
                    public void success(User.Model model, Response response) {
                        dialog.hide();

                        Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();
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

                        if (error.getKind().toString() == "NETWORK"){
                            AppHelper.showSnackbarErrorInternetConnection(mContext, findViewById(android.R.id.content), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    login();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "รหัสผ่านไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                        }


                    }
                }

        );
    }

    @OnClick(R.id.btn_forgotpass)
    public void forgot_password() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("LoginActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
