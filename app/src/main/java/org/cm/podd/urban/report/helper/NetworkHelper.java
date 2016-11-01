package org.cm.podd.urban.report.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.cm.podd.urban.report.BuildConfig;
import org.cm.podd.urban.report.data.PostModel.ReportImagesModel;

import java.io.IOException;

/**
 * Created by nat on 9/16/15 AD.
 */
@EBean(scope = EBean.Scope.Singleton)
public class NetworkHelper {

    GoogleCloudMessaging gcm;

    @RootContext
    Context mContext;

    @RootContext
    Context rootScope;

    public static final String TAG = NetworkHelper.class.getSimpleName();

    public static interface NetworkCallback {
        public void onRegisterd(String str);
    }

    public interface UpdateACLInterface {
        public void finish();

    }

    @Background
    public void updateACL(String name, UpdateACLInterface callback) {
        Log.d(TAG, "UPDATING ACL");

        try {
            Util.getS3Client(rootScope).setObjectAcl(BuildConfig.BUCKET_NAME, name, CannedAccessControlList.PublicRead);

            ReportImagesModel model = new ReportImagesModel();

            if (callback != null) {
                callback.finish();
            }
        } catch (Exception ex) {

        }

//
//        model.reportGuid = guid;
//        model.guid = guid;
//        model.imageUrl  =  BuildConfig.S3_PREFIX + name;
//        model.thumbnailUrl  =  BuildConfig.S3_PREFIX + name;
//
//
//        apiRequestor.postImage(model, new Callback<ApiRequestor.ReportImagesModel>() {
//            @Override
//            public void success(ApiRequestor.ReportImagesModel reportImagesModel, Response response) {
//                Log.d(TAG, "post image ok (line ): ");
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.d(TAG, "post image failed (line ): ");
//            }
//        });
//        Log.d(TAG, "UPDATED ACL...");
    }

    @Background
    public void registerGCMDeviceInBackground(NetworkCallback callback) {
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(mContext);
        }

        String regId = null;
        try {
            regId = gcm.register(Constants.SENDER_ID);
            callback.onRegisterd(regId);

            Log.d("GCM Register", regId);

        } catch (IOException e) {
//            Toast.makeText(mContext, "GCM Register error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        String msg = "Device registered, registration ID=" + regId;

        // You should send the registration ID to your server over HTTP,
        // so it can use GCM/HTTP or CCS to send messages to your app.
        // The request to your server should be authenticated if your app
        // is using accounts.
//        sendRegistrationIdToBackend();

        // For this demo: we don't need to send it because the device
        // will send upstream messages to a server that echo back the
        // message using the 'from' address in the message.

        // Persist the regID - no need to register again.]
//        storeRegistrationId(context, regId);

    }

    public static boolean hasNetworkConnection(Activity activity) {
        ConnectivityManager cm = ((ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE));
        boolean connected = cm != null &&
                cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnected();
        return connected;
    }
}
