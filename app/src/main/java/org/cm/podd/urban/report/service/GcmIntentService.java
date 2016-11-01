package org.cm.podd.urban.report.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.MainActivity;
import org.cm.podd.urban.report.activity.report.ReportDetailActivity;
import org.cm.podd.urban.report.activity.report.ReportFormActivity;
import org.cm.podd.urban.report.activity.report.ReportNewActivity;
import org.cm.podd.urban.report.adapter.ReportTypeAdapter;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.helper.AppHelper;

import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GcmIntentService";
    public static final String SYNC = "GcmIntentService.sync";

    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super(GcmIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                String message = intent.getStringExtra("message");
                message = Html.fromHtml(message).toString();

                String type = intent.getStringExtra("type");
                String reportId = intent.getStringExtra("reportId");

                Log.i(TAG, String.format("Receive GCM message type=%s, payload type = %s extra=%s report=%s", messageType, type, message, reportId));

                if (reportId != null) {

                    Log.i(TAG, String.format("Receive GCM message type=%s, payload type = %s extra=%s report=%s", messageType, type, message, reportId));

                        try {

                            int viewReportId = Integer.parseInt(reportId);

                            sendNotification(viewReportId, -1, message);
                        } catch (NumberFormatException e) {
                            CrashlyticsCore.getInstance().log("60 - Can't parse reportId");
                            Log.e(TAG, String.format("Can't parse reportId"));
                        }

                } else {
                    Pattern p = Pattern.compile(".*?@\\[reportType:\\((.*?)\\)\\]");
                    Matcher m = p.matcher(message);

                    if (m.find()) {
                        int reportType = ReportTypeAdapter.getItem(this, m.group(1));
                        sendNotification(-99999, reportType, message);
                        Log.e(TAG, String.format("new report"));
                    } else {
                        sendNotification(-1, -1, message);
                        Log.e(TAG, String.format("No view reportId"));
                        CrashlyticsCore.getInstance().log(String.format("66 - No view reportId"));
                    }
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(int reportId, int reportType, String message) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        AppHelper.setNotification(this, 1);

        Intent intent;
        if (reportId > -1) {
            intent = new Intent(this, ReportDetailActivity.class);
        } else if (reportId == -99999) {
            try {
                intent = new Intent(this, ReportFormActivity.class);

                ReportTypeAdapter.Item item = new ReportTypeAdapter.Item(ReportTypeAdapter.CHILD, this, reportType);

                Bundle bundle = new Bundle();
                bundle.putInt(ReportTypeAdapter.CAT_TYPE_STR_RES_KEY, item.stringRes);
                bundle.putString(ReportTypeAdapter.CAT_CODE_STRING, item.code);
                bundle.putInt(ReportTypeAdapter.CAT_COLOUR_INT, item.colour);
                intent.putExtras(bundle);
            } catch (Exception ex) {
                intent = new Intent(this, ReportNewActivity.class);
            }
        } else {
            intent = new Intent(this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        intent.setAction("org.cm.podd.urban.report.GcmIntentService");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        Bundle bundle = new Bundle();
        bundle.putInt(Report.REPORT_ID_KEY, reportId);
        intent.putExtras(bundle);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_noti)
                        .setContentTitle("ดูดีดี")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setContentText(message)
                        .setSound(alarmSound)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadcast(new Intent(SYNC));
            }
        });
    }
}
