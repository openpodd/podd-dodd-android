package org.cm.podd.urban.report.delegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.BuildConfig;
import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.ViewReportLargePhotosActivity;
import org.cm.podd.urban.report.activity.report.ReportDetailActivity;
import org.cm.podd.urban.report.activity.report.SupportActivity;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.Constants;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static org.cm.podd.urban.report.helper.NetworkHelper.hasNetworkConnection;

public class ReportDelegate implements Report.Adapter.MainFeedListener {

    public static final String TAG = ReportDelegate.class.getSimpleName();

    //    private final ReportFeedAdapter reportAdapter;
    final Activity mActivity;
    final Context mContext;
    final View mRootView;

    public static void manualBindDelegateEvent(View rootView,
                                               final Report.Model report, final int position,
                                               final Report.Adapter.MainFeedListener listener) {

        ButterKnife.findById(rootView, R.id.share_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShareButtonClick(report, position, v);
                }
            }
        });


        ButterKnife.findById(rootView, R.id.comment_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCommentsClick(report, position, v);
                }
            }
        });


        ButterKnife.findById(rootView, R.id.support_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSupportButtonClick(report, position, v);
                }
            }
        });


        ButterKnife.findById(rootView, R.id.report_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onImageClick(report, position, v);
                }
            }

        });

        ButterKnife.findById(rootView, R.id.comment_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCommentsClick(report, position, v);
                }
            }
        });

        ButterKnife.findById(rootView, R.id.report_title_linearlayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCommentsClick(report, position, v);
                }
            }
        });

    }

    public ReportDelegate(Activity mActivity, View rootView) {
        this.mActivity = mActivity;
        mRootView = rootView;
        mContext = mActivity;
    }

    private Activity getActivity() {
        return mActivity;
    }

    private void startActivity(Intent i) {
        mActivity.startActivity(i);
    }


    @Override
    public void onLikeClick(boolean isLiked, Report.Model r, int position, View v) {

    }

    @Override
    public void onDoLike(Report.Model r, int position, View v) {

    }

    @Override
    public void onUnLike(Report.Model r, int position, View v) {

    }

    @Override
    public void onCommentsClick(final Report.Model r, final int position, View v) {
        AppHelper.anonymousCheckAction(mActivity, new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                if (mActivity.getClass().equals(ReportDetailActivity.class))
                    return;

                if (!hasNetworkConnection(getActivity())) {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(mContext, ReportDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Report.REPORT_MODEL_KEY, r.getJsonSerialized(mContext));
                bundle.putInt(Report.REPORT_POSITION_KEY, position);
                i.putExtras(bundle);
                getActivity().startActivity(i);

            }
        });
    }

    @Override
    public void onReportAbuse(Report.Model r, int position, int which, String str) {

    }

    @Override
    public void onInfoIconClick(Report.Model r, int position, View v) {

    }

    @Override
    public void onShareButtonClick(Report.Model r, int position, View v) {
        final String SHARE_PATTERN = BuildConfig.SHARE_PATTERN;
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");

        String shareUrl = String.format(SHARE_PATTERN, r.id);
        share.putExtra(Intent.EXTRA_SUBJECT, r.reportTypeName);
        share.putExtra(Intent.EXTRA_TEXT, shareUrl);

//        http://www.thaispendingwatch.com:8088/project/19/reports/338
        getActivity().startActivity(Intent.createChooser(share, shareUrl));

        MyApplication app = (MyApplication) getActivity().getApplication();
        Tracker tracker = app.getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("ShareView");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    public void onSupportButtonClick(final Report.Model r, final int position, View v) {
        AppHelper.anonymousCheckAction(getActivity(), new AppHelper.AnonymousCheck() {
            @Override
            public void denied() {

            }

            @Override
            public void next() {
                Intent i = new Intent(mContext, SupportActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Report.REPORT_MODEL_KEY, r.getJsonSerialized(mContext));
                bundle.putInt(Report.REPORT_POSITION_KEY, position);
                i.putExtras(bundle);
                getActivity().startActivity(i);
            }
        });
    }

    @Override
    public void onImageClick(Report.Model r, int position, View v) {
//        onCommentsClick(r, position, v);

        Intent intent = new Intent(getActivity(), ViewReportLargePhotosActivity.class);

        String[] imagesStr = new String[r.images.size()];

        ArrayList<String> imageList = new ArrayList();

        int i;
        for (i = 0; i < r.images.size(); i++) {
            imagesStr[i] = r.images.get(i).imageUrl;
            imageList.add(r.images.get(i).imageUrl);
        }

        intent.putStringArrayListExtra(Constants.REPORT_IMAGE_URLS_EXTRA_STRING_ARRAY, imageList);
        getActivity().startActivity(intent);
//        Intent i = new Intent(mContext, ReportDetailActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("REPORT_MODEL", r.getJsonSerialized(mContext));
//        i.putExtras(bundle);
//        getActivity().startActivity(i);

    }

}
