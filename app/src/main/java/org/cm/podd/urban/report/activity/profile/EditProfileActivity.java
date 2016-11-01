package org.cm.podd.urban.report.activity.profile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.BuildConfig;
import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseToolBarActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.ActivityEditProfileBinding;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;
import org.cm.podd.urban.report.helper.NetworkHelper;
import org.cm.podd.urban.report.helper.NetworkHelper_;
import org.cm.podd.urban.report.helper.S3Helper;
import org.cm.podd.urban.report.widget.ImageWrapperLinearLayout;

import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mylibrary.helper.CameraHelper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends BaseToolBarActivity {
    @Bind(R.id.name_edit_text) EditText mNameEdittext;
    @Bind(R.id.email_edit_text) EditText mEmailEdittext;
    @Bind(R.id.telephone_edit_text) EditText mTelephoneEdittext;
    @Bind(R.id.img_avatar) ImageView ImageAvatar;

    ApiRequestor apiRequestor;

    CameraHelper cameraHelper;
    ProgressDialog dialog;

    private ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        final User.Model user = AppHelper.getUserSerialized(mContext);
//        user.firstName = user.username;
        binding.setUser(user);
        ButterKnife.bind(this);

        cameraHelper = new CameraHelper(this);

        apiRequestor = new ApiRequestor(this);
        apiRequestor.getMyProfile(new Callback<User.Model>() {

            @Override
            public void success(User.Model model, Response response) {
//                Log.d("EditProfie", model.telephone);
                binding.setUser(model);
            }

            @Override
            public void failure(RetrofitError error) {
//                Log.e("EditProfileActivity", error.getMessage());
//                Toast.makeText(EditProfileActivity.this, "FAIL", Toast.LENGTH_SHORT).show();
            }
        });

         dialog = ProgressDialog.show(EditProfileActivity.this, "",
                "กรุณารอสักครู่...", true);

         dialog.setIndeterminate(true);
         dialog.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_profile) {
            saveProfile();
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

    Uri uri;
    ImageWrapperLinearLayout img;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraHelper.handleActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.RESULT_GET_CONTENT) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult OK");
                uri = data.getData();
            }

            if (uri != null) {

//                ImageAvatar.setImageURI(uri);

                img = new ImageWrapperLinearLayout(mContext).setImageResource(uri);
                img.setUri(uri);
                img.setPadding(10, 10, 10, 10);

                uploadToS3();
            }

        } else if (requestCode == CameraHelper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "CAMERA PHOTO (line 203): ");
            }
        }
        Log.d(TAG, "onActivityResult ");

    }

    // for update avatar image
    @OnClick(R.id.edit_profile_linearlayout)
    public void onClick(View v){
        cameraHelper.setListener(new CameraHelper.cameraHelperLister() {
            @Override
            public void onSuccess(Uri uri) {

            }

            @Override
            public void onCameraSuccess(Uri uri) {
                Log.d(TAG, "TAKE A PHOTO USING CAMERA SUCCESS (line ): ");

//                ImageAvatar.setImageURI(uri);

                img = new ImageWrapperLinearLayout(mContext).setImageResource(uri);
                img.setUri(uri);
                img.setPadding(10, 10, 10, 10);

                uploadToS3();
            }
        });

        cameraHelper.showPopUp();
    }

    private String s3ImagesFileUrl;

    public void uploadToS3() {
        dialog.show();

        final Task<String>.TaskCompletionSource task = Task.create();
        task.getTask().onSuccessTask(new Continuation<String, Task<ArrayList<String>>>() {
            @Override
            public Task<ArrayList<String>> then(Task<String> task) throws Exception {
                Log.d(TAG, "  (line ):" + task.getResult());

                final Task<ArrayList<String>>.TaskCompletionSource tcs = Task.create();
                final S3Helper s3Helper = new S3Helper(mContext).setCallback(new S3Helper.S3HelperCallback() {
                    @Override
                    public void onAllFilesUploaded(ArrayList<String> fileNames) {
                        Log.d(TAG, "onAllFileUploaded (line ): ");

                        ArrayList<String> updateKeys = new ArrayList<String>();
                        if (fileNames.size() > 0) {
                            Log.d(TAG, "success: " + fileNames.get(0));
                            s3ImagesFileUrl = BuildConfig.S3_PREFIX + fileNames.get(0);

                            updateKeys.add(fileNames.get(0));
                            updateKeys.add(fileNames.get(0) + "-thumbnail");
                        }
                        tcs.setResult(updateKeys);
                    }
                });

                s3Helper.beginUpload(img.getPath(), img.getThumbnailImage());
                Log.d(TAG, "(line ): " + " : " + img.getPath());

                return tcs.getTask();
            }
        }).continueWithTask(new Continuation<ArrayList<String>, Task<Void>>() {
            @Override
            public Task<Void> then(Task<ArrayList<String>> task) throws Exception {
                ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();
                for (int i = 0; i < task.getResult().size(); i++) {
                    NetworkHelper updateACL = NetworkHelper_.getInstance_(mContext);
                    final Task<Void>.TaskCompletionSource _task = Task.create();
                    tasks.add(_task.getTask());


                    updateACL.updateACL(task.getResult().get(i), new NetworkHelper.UpdateACLInterface() {
                        @Override
                        public void finish() {
                            _task.setResult(null);
                        }
                    });
                }

                return Task.whenAll(tasks);
            }
        }).continueWithTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                ArrayList<Task<Void>> tasks = new ArrayList<Task<Void>>();

                User.Model postModel = new User.Model();
                postModel.avatarUrl = s3ImagesFileUrl;
                postModel.thumbnailAvatarUrl = s3ImagesFileUrl + "-thumbnail";

                apiRequestor.updateProfile(postModel, new Callback<User.Model>() {
                    @Override
                    public void success(User.Model model, Response response) {
                        BusProvider.getInstance().post(new ActivityResultEvent(TAG));

                        Toast.makeText(EditProfileActivity.this, "เปลี่ยนรูปสำเร็จ", Toast.LENGTH_SHORT).show();

                        AppHelper.setUserSerialized(mContext, model);
                        binding.setUser(model);

                        dialog.hide();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dialog.hide();
                        Log.e("EditProfileActivity", error.getMessage());
                        Toast.makeText(EditProfileActivity.this, "ไม่สามารถเปลี่ยนรูปได้", Toast.LENGTH_SHORT).show();
                        AppHelper.showSnackbarErrorInternetConnection(mContext,
                                findViewById(android.R.id.content), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        uploadToS3();
                                    }
                                });
                    }
                });

                return Task.whenAll(tasks);
            }
        }).onSuccess(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {
                return null;
            }
        });

        task.setResult("Loading........");

    }

    public void saveProfile(){
        dialog.show();

        Log.d("EditProfileActivity", "Click ");
        if (uri != null) {
            uploadToS3();
        }

        String firstName = mNameEdittext.getText().toString();
        String email = mEmailEdittext.getText().toString();

        if (firstName.equalsIgnoreCase("") || email.equalsIgnoreCase("")) {
            Toast.makeText(EditProfileActivity.this, "กรุณากรอกชื่อ และอีเมล", Toast.LENGTH_SHORT).show();
            return;
        }

        User.Model postModel = new User.Model();
        postModel.firstName = firstName;
        postModel.email = email;
        postModel.telephone = mTelephoneEdittext.getText().toString();

        Log.d("EditProfileActivity", "POST OBJ LIKE (line ): ");

        final Context mContext = this;
        apiRequestor.updateProfile(postModel, new Callback<User.Model>() {
            @Override
            public void success(User.Model model, Response response) {
                dialog.hide();

                BusProvider.getInstance().post(new ActivityResultEvent(TAG));
                Toast.makeText(EditProfileActivity.this, "อัพเดทข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();

                AppHelper.setUserSerialized(mContext, model);

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
                Log.e("EditProfileActivity", error.getMessage());
                dialog.hide();

                Toast.makeText(EditProfileActivity.this, "อัพเดทข้อมูลไม่สำเร็จ ลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
                AppHelper.showSnackbarErrorInternetConnection(mContext,
                        findViewById(android.R.id.content), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveProfile();
                            }
                        });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("EditProfileActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
