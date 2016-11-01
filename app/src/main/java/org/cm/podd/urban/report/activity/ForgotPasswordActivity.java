package org.cm.podd.urban.report.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.UserCode;
import org.cm.podd.urban.report.helper.AppHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgotPasswordActivity extends BaseToolBarActivity {

    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    ApiRequestor apiRequestor;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        apiRequestor = new ApiRequestor(this);


        dialog = ProgressDialog.show(ForgotPasswordActivity.this, "",
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

    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ForgotPasswordActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public void confirm(){
        Log.d("ForgotPasswordActivity", "Click ");
        dialog.show();

        final String email = emailEditText.getText().toString();

        final Context c = this;

        apiRequestor.forgotPassword(email, new Callback<UserCode.Model>() {
            @Override
            public void success(UserCode.Model model, Response response) {
                dialog.hide();
                Intent i = new Intent(c, LoginCodeActivity.class);
                i.putExtra("email", email);
                i.putExtra("uid", model.uid);
                i.putExtra("token", model.token);

                startActivity(i);

            }

            @Override
            public void failure(RetrofitError error) {
                dialog.hide();

                Log.e("ForgotPasswordActivity", error.getMessage());
//                Toast.makeText(ForgotPasswordActivity.this, "ไม่มีอีเมลนี้อยู่ในระบบ", Toast.LENGTH_SHORT).show();
                if (error.getKind().toString() == "NETWORK"){
                    AppHelper.showSnackbarErrorInternetConnection(mContext, findViewById(android.R.id.content), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirm();
                        }
                    });
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "ไม่มีอีเมลนี้อยู่ในระบบ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.confirm_button)
    public void onClick(View v){
        confirm();
    }
}
