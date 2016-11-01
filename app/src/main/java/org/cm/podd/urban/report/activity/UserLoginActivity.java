package org.cm.podd.urban.report.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.register.RegisterCompleteActivity;
import org.cm.podd.urban.report.activity.register.RegisterEmailActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.api.RestClient;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.NetworkHelper;
import org.cm.podd.urban.report.helper.NetworkHelper_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;


public class UserLoginActivity extends BaseToolBarActivity {

    private Context mContext;
    GoogleCloudMessaging gcm;
    String regId = "";

    @Bind(R.id.facebook_login_button)
    LoginButton loginButton;
    @Bind(R.id.skip_button)
    Button mSkipLogin;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> fbCallback;
    private ApiRequestor apiRequestor;

    private void setupFacebookLoginButton() {
        Log.d(TAG, "setUpFacebookLogin  (line ): ");
        callbackManager = CallbackManager.Factory.create();
        List<String> permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("user_friends");

        loginButton.setReadPermissions(permissions);
        // If using in a fragment
        // Other app specific specialization

        fbCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess (line ): ");
                getFBEmail(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel (line ): ");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError (line ): ");
            }
        };

        loginButton.registerCallback(callbackManager, fbCallback);
    }

    public void getFBEmail(final LoginResult loginResult) {
        Log.d(TAG, "getFBEmail (line ): ");
//        GraphRequest.GraphJSONObjectCallback graphCallback =
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        String email = object.optString("email");
//                        Log.d(TAG, "onCompleted " + email);
//                        if (email != null) {
//                            Log.v("LoginActivity", response.toString());
//                            User.Model user = AppHelper.getUserSerialized(mContext);
//                            user.email = email;
//                            AppHelper.setUserSerialized(mContext, user);
//                            goNextAfterFacebookLogin();
//                        } else {
//                            Toast.makeText(mContext, "Can't get an email address", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }; // end callback
//
//
//        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), graphCallback);
//
//        Bundle parameters = new Bundle();
//        //parameters.putString("fields", "id,name,email,gender, birthday");
//        parameters.putString("fields", "id,name,email");
//        request.setParameters(parameters);
//
//        request.executeAsync();

        apiRequestor.updateDeviceByFacebookToken(loginResult.getAccessToken().getToken(), new Callback<User.Model>() {
            @Override
            public void success(User.Model model, Response response) {
                User.Model user = model;
                AppHelper.setIsLoggedIn(mContext, true);
                AppHelper.setToken(mContext, model.token);
                AppHelper.setUserSerialized(mContext, user);
                goNextAfterFacebookLogin();
            }

            @Override
            public void failure(RetrofitError error) {
                LoginManager.getInstance().logOut();
                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getFBEmail(loginResult);
                            }
                        });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        apiRequestor = new ApiRequestor(mContext);
        setContentView(R.layout.activity_user_login);
        AppHelper.getDeviceId(mContext);

        if (AppHelper.checkPlayServices(this)) {
            Log.d(TAG, "onCreate checkPlayservices OK");
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = AppHelper.getRegistrationId(this);

            if (regId.isEmpty()) {
                NetworkHelper networkHelper = NetworkHelper_.getInstance_(mContext);
                networkHelper.registerGCMDeviceInBackground(new NetworkHelper.NetworkCallback() {
                    @Override
                    public void onRegisterd(String str) {
                        Log.d(TAG, "onRegisterd - " + str);
                        AppHelper.storeRegistrationId(mContext, str);
                    }
                });
            }
        }
        registerPoddDevice();
    }


    public void registerPoddDevice() {

        if (AppHelper.getDeviceId(mContext).equalsIgnoreCase("USER_DEVICE")
                || AppHelper.getDeviceId(mContext).equalsIgnoreCase("NULL")) {
            AppHelper.setDeviceId(mContext);
        }

        HashMap<String, String> myMap = new HashMap<String, String>();
        myMap.put("deviceId", AppHelper.getDeviceId(mContext));
        myMap.put("gcmRegId", regId);
        myMap.put("model", Build.MODEL);
        myMap.put("brand", Build.BRAND);
        TypedInput ti = AppHelper.getTypedInputFromHashMap(mContext, myMap);

        apiRequestor.registerDevice(ti, new Callback<User.Model>() {
            @Override
            public void success(User.Model profile, Response response) {
                AppHelper.setToken(mContext, profile.token);
                AppHelper.setUserSerialized(mContext, profile);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("UserLoginActivity", "failed profile (line ): " + error.getMessage());
                try {
                    CrashlyticsCore.getInstance().log(error.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
//                Toast.makeText(mContext, "เกิดความผิดพลาดขณะลงทะเบียนอุปกรณ์", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBindView() {
        super.onBindView();
        ButterKnife.bind(this);
    }

    @Override
    public void onSetupAdapter() {
        super.onBindView();

    }

    @Override
    public void onSetupEventListener() {
        setupFacebookLoginButton();
        mSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppHelper.setIsLoggedIn(mContext, false);
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

    }

    private void goNextAfterFacebookLogin() {
        Intent intent = new Intent(mContext, RegisterCompleteActivity.class);
        startActivity(intent);
        LoginManager.getInstance().logOut();
    }

    @OnClick(R.id.btn_register_email)
    public void register_email() {
        Intent intent = new Intent(this, RegisterEmailActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void login_button() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("UserLoginActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());


    }
}
