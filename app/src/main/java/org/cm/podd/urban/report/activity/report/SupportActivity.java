package org.cm.podd.urban.report.activity.report;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.BaseActivity;
import org.cm.podd.urban.report.api.ApiRequestor;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.PostModel.*;
import org.cm.podd.urban.report.data.SupportButtonViewModel;
import org.cm.podd.urban.report.databinding.ActivityDialogueSupportBinding;
import org.cm.podd.urban.report.helper.ActivityResultEvent;
import org.cm.podd.urban.report.helper.AppHelper;
import org.cm.podd.urban.report.helper.BusProvider;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SupportActivity extends BaseActivity {
    public static final String TAG = SupportActivity.class.getSimpleName();

    @Bind(R.id.comment_edittextview)
    EditText mCommentEdittext;
    @Bind(R.id.like_togglebutton)
    ToggleButton mLikeToggleButton;
    @Bind(R.id.me_too_togglebutton)
    ToggleButton mMeTooToggleButton;

    Report.Model mReport;
    ApiRequestor apiRequestor;
    private SupportButtonViewModel viewModel;
    private ActivityDialogueSupportBinding binding;
    private int mPosition;


    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dialogue_support);
        ButterKnife.bind(this);

        apiRequestor = new ApiRequestor(this);

        Intent prevIntent = getIntent();


        mReport = (Report.Model) AppHelper.deSerialize(this,
                prevIntent.getStringExtra(Report.REPORT_MODEL_KEY), Report.Model.class);

        mPosition = prevIntent.getIntExtra(Report.REPORT_POSITION_KEY, 0);


        binding.setReport(mReport);
        viewModel = new SupportButtonViewModel();
        viewModel.setLike(mReport.isLike());
        viewModel.setMeToo(mReport.isMeToo());
        binding.setSupportView(viewModel);

        Log.d(TAG, " (line ): ");
    }

    @OnClick(R.id.confirm_buttonview)
    public void onClick(View v) {
        Log.d(TAG, "CONFIRM (line ): ");
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        SupportPostModel postModel = new SupportPostModel();
        postModel.isLike = mLikeToggleButton.isChecked();
        postModel.isMeToo = mMeTooToggleButton.isChecked();
        postModel.reportId = mReport.id;
        postModel.message = mCommentEdittext.getText().toString();


        boolean currentMeToo = postModel.isMeToo;
        boolean currentLike = postModel.isLike;
        boolean prevMetoo = mReport.isMeToo();
        boolean prevLike = mReport.isLike();

        // incomming isMetoo = true
        if (prevMetoo == true && !currentMeToo) {
            mReport.meTooCount--;
        }

        if (prevMetoo == false && currentMeToo) {
            mReport.meTooCount++;
        }

        if (prevLike == true && !currentLike) {
            mReport.likeCount--;
        }
        if (prevLike == false && currentLike) { //
            mReport.likeCount++;
        }

        if (postModel.message.isEmpty() == false) {
            mReport.commentCount++;
        }

        mReport.meTooId = postModel.isMeToo ? 1 : 0;
        mReport.likeId = postModel.isLike ? 1 : 0;

        Log.d(TAG, "POST OBJ LIKE (line ): ");

        apiRequestor.supportReport(postModel, new Callback<Report.Model>() {
                    @Override
                    public void success(Report.Model model, Response response) {
                        ActivityResultEvent event = new ActivityResultEvent(TAG);
                        event.position = mPosition;
                        event.reportModel = mReport;
                        BusProvider.getInstance().post(event);
                        Toast.makeText(SupportActivity.this, "สำเร็จ", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SupportActivity.this, "ไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                    }
                }

        );

    }

    @OnCheckedChanged(R.id.like_togglebutton)
    void onLikeChecked(boolean checked) {
        viewModel.setLike(checked);
        binding.setSupportView(viewModel);
    }

    @OnCheckedChanged(R.id.me_too_togglebutton)
    void onMeTooChecked(boolean checked) {
        viewModel.setMeToo(checked);
        binding.setSupportView(viewModel);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Tracker tracker = ((MyApplication) getApplication()).getTracker(MyApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName("SupportActivity");
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
