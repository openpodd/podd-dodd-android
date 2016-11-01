package org.cm.podd.urban.report.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.Comment;
import org.cm.podd.urban.report.data.HotReport;
import org.cm.podd.urban.report.data.Notification;
import org.cm.podd.urban.report.data.PostModel;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.data.UserCode;
import org.cm.podd.urban.report.helper.AppHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Created by nat on 10/14/15 AD.
 */
public class ApiRequestor {
    private final Context mContext;
    public static final String TAG = ApiRequestor.class.getSimpleName();
    private final ApiService apiService;

    public ApiRequestor(Context context) {
        this.mContext = context;
        apiService = new RestClient().getApiService();
    }

    public void registerDevice(TypedInput json, Callback<User.Model> cb) {
        apiService.registerDevice(json, cb);
    }

    public void updateDevice(String email, Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("email", email);
        TypedInput json = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.userProfile(token, json, false, cb);
    }

    public void updateAndroidDevice(TypedInput json, Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.updateDevice(token, json, cb);
    }

    public void updateDeviceByFacebookToken(String fbtoken, Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        HashMap<String, String> hashMap = new HashMap<String, String>();

        hashMap.put("facebookAccessToken", fbtoken);
        hashMap.put("force", "true");

        TypedInput json = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.userProfile(token, json, true, cb);
    }

    public void updateProfile(User.Model user, Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        HashMap<String, String> hashMap = new HashMap<String, String>();

        if (user.firstName != null && !user.firstName.equalsIgnoreCase(""))
            hashMap.put("firstName", user.firstName);

        if (user.email != null && !user.email.equalsIgnoreCase(""))
            hashMap.put("email", user.email);

        if (user.telephone != null && !user.telephone.equalsIgnoreCase(""))
            hashMap.put("telephone", user.telephone);

        if (user.avatarUrl != null && !user.avatarUrl.equalsIgnoreCase(""))
            hashMap.put("avatarUrl", user.avatarUrl);

        if (user.thumbnailAvatarUrl != null && !user.thumbnailAvatarUrl.equalsIgnoreCase(""))
            hashMap.put("thumbnailAvatarUrl", user.thumbnailAvatarUrl);

        TypedInput json = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.userProfile(token, json, true, cb);
    }

    public void updatePassword(String password, Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("password", password);

        TypedInput json = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.updatePassword(token, json, cb);
    }

    public void forgotPassword(String email, Callback<UserCode.Model> cb) {
        String token = AppHelper.getToken(mContext);
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("email", email);

        TypedInput json = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.forgotPassword(json, cb);
    }

    public void loginTempPassword(String password, String uid, String token, Callback<User.Model> cb) {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("code", password);

        TypedInput typedInput = AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.loginTempPassword(typedInput, uid, token, cb);

    }

    public void getMyProfile(Callback<User.Model> cb) {
        String token = AppHelper.getToken(mContext);
        ;
        apiService.getProfile(token, cb);

    }

    public void getReportsByArea(final int areaId, final int page, final int pageSize, final Callback<ArrayList<Report.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getReportsFeed(token, areaId, true, page, pageSize, cb);

//
//
//        final ReportListCacheHelper cacheHelper = ReportListCacheHelper_.getInstance_(mContext);
//
//        Callback<ArrayList<Report.Model>> callback = new Callback<ArrayList<Report.Model>>() {
//            @Override
//            public void success(ArrayList<Report.Model> models, Response response) {
//                ReportListCacheHelper_.getInstance_(mContext).setReportList(areaId, models);
//                cacheHelper.reportModelCache.put(areaId, models);
//                cb.success(models, response);
//                cacheHelper.setDirty(false);
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                cb.failure(error);
//            }
//        };
//
//
//
//
//        if (ReportListCacheHelper_.getInstance_(mContext).isDirty()) {
//            Log.d(TAG, "getReportsByArea: CACHE REFRESH");
//            apiService.getReportsFeed(token, areaId, callback);
//        }
//        else {
//            ArrayList<Report.Model> r = cacheHelper.reportModelCache.get(areaId);
//            if (r!=null) {
//                Log.d(TAG, "getReportsByArea: CACHE HIT");
//                cb.success(r, null);
//
//            }
//            else {
//                Log.d(TAG, "getReportsByArea: CACHE MISSED");
//            }
//
//        }
    }

    public void getReportDetail(int reportId, Callback<Report.Model> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getReport(token, reportId, cb);

    }

    public void getMyReports(Callback<ArrayList<Report.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getMyReportFeed(token, cb);
    }

    public void supportReport(PostModel.SupportPostModel supportPostModel, Callback<Report.Model> cb) {
        String token = AppHelper.getToken(mContext);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(supportPostModel, supportPostModel.getClass());

        TypedInput in = null;
        try {
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        apiService.supports(token, in, cb);
    }

    public void postReport(PostModel.PostReportModel postReportModel, Callback<Report.Model> cb) {
        String token = AppHelper.getToken(mContext);

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(postReportModel);

        TypedInput in = null;
        try {
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        apiService.postReport(token, in, cb);

        ReportListCacheHelper_.getInstance_(mContext).setDirty(true);

    }

    public void getComments(int rid, Callback<ArrayList<Comment.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getComments(token,
                rid, cb);
    }

    public void postImage(PostModel.ReportImagesModel model, Callback<PostModel.ReportImagesModel> cb) {
        String token = AppHelper.getToken(mContext);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(model);

        TypedInput in = null;
        try {
            in = new TypedByteArray("application/json", json.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        apiService.postReportImage(token, in, cb);
    }

    public void getHotReport(int adminId, String withMpoly, Callback<HotReport.Model> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getHotReports(token, adminId, withMpoly, true, true, cb);
    }

    public void getAdministrationArea(Callback<List<AdministrationArea.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getAdministrationArea(token, cb);
    }

    public void getAdministrationArea(Double lat, Double lng, Callback<List<AdministrationArea.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getAdministrationAreaByLatLng(token,
                lat, lng,
                cb);
    }

    public void loginByPassword(String username, String password, Callback<User.Model> cb) {

        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("username", username);
        hashMap.put("password", password);

        TypedInput typedInput =  AppHelper.getTypedInputFromHashMap(mContext, hashMap);
        apiService.loginPassword(typedInput, cb);

    }


    public void getNotifications(Callback<ArrayList<Notification.Model>> cb) {
        String token = AppHelper.getToken(mContext);
        apiService.getNotifications(token, cb);
    }

}
