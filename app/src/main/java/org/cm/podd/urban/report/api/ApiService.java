package org.cm.podd.urban.report.api;

import org.cm.podd.urban.report.data.AdministrationArea;
import org.cm.podd.urban.report.data.Comment;
import org.cm.podd.urban.report.data.HotReport;
import org.cm.podd.urban.report.data.Notification;
import org.cm.podd.urban.report.data.PostModel;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.data.UserCode;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface ApiService {

    @POST("/users/register/device/1/")
    @Headers({"Content-Type: application/json"})
    void registerDevice(@Body TypedInput body, Callback<User.Model> cb);

    @POST("/users/profile/")
    @Headers({"Content-Type: application/json"})
    void userProfile(
            @Header("Authorization") String authorization,
            @Body TypedInput body,
            @Query("force") boolean force,
            Callback<User.Model> cb);

    @GET("/users/profile/")
    void getProfile(
            @Header("Authorization") String authorization,
            Callback<User.Model> cb);

    @POST("/users/profile/password/")
    @Headers({"Content-Type: application/json"})
    void updatePassword(@Header("Authorization") String authorization,
                        @Body TypedInput body,
                        Callback<User.Model> cb);

    @POST("/users/profile/device/")
    @Headers({"Content-Type: application/json"})
    void updateDevice(
            @Header("Authorization") String authorization,
            @Body TypedInput body,
            Callback<User.Model> cb);

    @POST("/users/forgot-password/")
    @Headers({"Content-Type: application/json"})
    void forgotPassword(@Body TypedInput body,
                        Callback<UserCode.Model> cb);

    @GET("/reportComments/")
    void getComments(
            @Header("Authorization") String authorization,
            @Query("reportId") int rid,
            Callback<ArrayList<Comment.Model>> cb);

    @GET("/users/profile/")
    void getComments(
            @Query("reportId") int rid,
            Callback<ArrayList<Comment.Model>> cb);

    @POST("/supports/")
    @Headers({"Content-Type: application/json"})
    void supports(
            @Header("Authorization") String authorization,
            @Body TypedInput body,
            Callback<Report.Model> cb);

//    @GET("/reports/search/?q=negative:true")
//    void getReportsAndUpdateListView(@Header("Authorization") String authorization, Callback<ArrayList<Report.Model>> cb);

    //    http://staging.cmonehealth.org:8000/feed/?administrationArea=277
    @GET("/feed/")
    void getReportsFeed(@Header("Authorization") String authorization,
                        @Query("administrationArea") int administrationArea,
                        @Query("isPolygon") boolean isPolygon,
                        @Query("page") int page,
                        @Query("page_size") int pageSize,
                        Callback<ArrayList<Report.Model>> cb);

    @GET("/notifications/")
    void getNotifications(@Header("Authorization") String authorization,
                          Callback<ArrayList<Notification.Model>> cb);

    @GET("/feed/mine/")
    void getMyReportFeed(@Header("Authorization") String authorization,
                         Callback<ArrayList<Report.Model>> cb);

    @GET("/administrationArea/{id}/")
    void getHotReports(@Header("Authorization") String authorization,
                       @Path("id") int id,
                       @Query("withMpoly") String withMpoly,
                       @Query("public") boolean isPublic,
                       @Query("isPolygon") boolean isPolygon,
                       Callback<HotReport.Model> cb);

    @GET("/administrationArea/")
    void getAdministrationArea(@Header("Authorization") String authorization,
                               Callback<List<AdministrationArea.Model>> cb);

    @GET("/administrationArea/")
    void getAdministrationAreaByLatLng(@Header("Authorization") String authorization,
                                       @Query("latitude") Double lat,
                                       @Query("longitude") Double lng,
                                       Callback<List<AdministrationArea.Model>> cb);

    @POST("/reports/")
    @Headers({"Content-Type: application/json"})
    void postReport(@Header("Authorization") String authorization,
                    @Body TypedInput body,
                    Callback<Report.Model> cb);

    @GET("/reports/{id}/")
    void getReport(@Header("Authorization") String authorization,
                   @Path("id") int id,
                   Callback<Report.Model> cb);

    @POST("/reportImages/")
    @Headers({"Content-Type: application/json"})
    void postReportImage(@Header("Authorization") String authorization,
                         @Body TypedInput body,
                         Callback<PostModel.ReportImagesModel> cb);

    @POST("/api-token-auth/")
    @Headers({"Content-Type: application/json"})
    void loginPassword(@Body TypedInput body,
                       Callback<User.Model> cb);

    @POST("/users/code-login/{uid}/{token}/")
    @Headers({"Content-Type: application/json"})
    void loginTempPassword(@Body TypedInput body,
                           @Path("uid") String uid,
                           @Path("token") String token,
                           Callback<User.Model> cb);

}

