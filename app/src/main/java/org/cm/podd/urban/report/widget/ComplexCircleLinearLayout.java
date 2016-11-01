package org.cm.podd.urban.report.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cm.podd.urban.report.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ComplexCircleLinearLayout extends LinearLayout {
    private final Context mContext;
    private int mTitleColor;
    private int mTextColor;
    private String mReportNumber;
    private String mTitle;
    private int mLevel;
    private int mColor;
    private View mRootView;

    //    @Bind(R.id.attach_img) ImageView imageView;
    private Uri uri;
    private String path;
    @Bind(R.id.circle)
    org.cm.podd.urban.report.widget.CircleView mCircle;
    @Bind(R.id.title)
    TextView mTitleTextView;
    @Bind(R.id.report_text_textview)
    TextView mReportTextTextView;
    @Bind(R.id.report_number_textview)
    TextView mReportNumberTextView;
    @Bind(R.id.icon)
    ImageView mIconView;

    public static final String TAG = ComplexCircleLinearLayout.class.getSimpleName();

    public ComplexCircleLinearLayout(Context context) {
        super(context);
        mRootView = LayoutInflater.from(context).inflate(R.layout.report_circle_layout, this);
        mContext = context;
        ButterKnife.bind(this, mRootView);
        mCircle.setCircleColor(mContext.getResources().getColor(R.color.catPeople));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    public ComplexCircleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRootView = LayoutInflater.from(context).inflate(R.layout.report_circle_layout, this);
        mContext = context;
        ButterKnife.bind(this, mRootView);


        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ComplexCircleLinearLayout,
                0, 0);

        try {
            mColor = a.getColor(R.styleable.ComplexCircleLinearLayout_od_circleColor,
                    mContext.getResources().getColor(R.color.default_circle_indicator_page_color));
            mTextColor = a.getColor(R.styleable.ComplexCircleLinearLayout_od_circleTextColor,
                    mContext.getResources().getColor(R.color.textWhite));
            mTitleColor = a.getColor(R.styleable.ComplexCircleLinearLayout_od_titleColor,
                    mContext.getResources().getColor(R.color.textHintGrey));
            mLevel = a.getInteger(R.styleable.ComplexCircleLinearLayout_od_level, 3);
            mReportNumber = a.getString(R.styleable.ComplexCircleLinearLayout_od_number);
            mTitle = a.getString(R.styleable.ComplexCircleLinearLayout_od_title);
            mIconView.setBackgroundDrawable(a.getDrawable(R.styleable.ComplexCircleLinearLayout_od_mipmap));



        } finally {
            a.recycle();
        }

//        setLevel(mLevel);

        mTitleTextView.setText(mTitle);
        mReportNumberTextView.setText(String.valueOf(mReportNumber));
        mCircle.setCircleColor(mColor);
        mTitleTextView.setTextColor(mTitleColor);
        mReportTextTextView.setTextColor(mTextColor);
        mReportNumberTextView.setTextColor(mTextColor);
    }

    public String getNumber() {
        return mReportNumber;

    }

    public void setNumber(String number) {

        mReportNumber = number;
        mReportNumberTextView.setText(number);
    }

    public void setLevel(int level) {
        mLevel = level;
        float value;

        Log.d(TAG, "scaleX: " + mRootView.getScaleX());
        Log.d(TAG, "scaleY: " + mRootView.getScaleX());

        switch (mLevel) {
            case 1:
                value = (float) 0.5992;
                mRootView.setScaleX(value);
                mRootView.setScaleY(value);

                mTitleTextView.setScaleX((1 / value));
                mTitleTextView.setScaleY((1 / value));
                mReportTextTextView.setScaleY((1 / value));
                mReportTextTextView.setScaleX((1 / value));
                mReportTextTextView.getParent().requestLayout();

                mRootView.requestLayout();
                break;
            case 2:
                value = (float) 0.72302;
                mRootView.setScaleX(value);
                mRootView.setScaleY(value);


                mTitleTextView.setScaleX(1/value);
                mTitleTextView.setScaleY(1/value);

                mReportTextTextView.setScaleY(1/value);
                mReportTextTextView.setScaleX(1/value);

                mReportTextTextView.getParent().requestLayout();

                mRootView.requestLayout();
                break;
            case 3:
                Log.d(TAG, "scaleX: " + mRootView.getScaleX());
                Log.d(TAG, "scaleY: " + mRootView.getScaleX());
                mRootView.setScaleX((float) 1.0);
                mRootView.setScaleY((float) 1.0);
                mRootView.requestLayout();

                mTitleTextView.setScaleX((1 ));
                mTitleTextView.setScaleY((1 ));
                mReportTextTextView.setScaleY((1  ));
                mReportTextTextView.setScaleX((1 ));
                mReportTextTextView.getParent().requestLayout();
                break;
        }

    }

    public int getLevel() {
        return mLevel;

    }

    public ComplexCircleLinearLayout setImageResource(String path) {
//        Glide.with(mContext).load(path).into(imageView);
        return this;
    }

    public ComplexCircleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }



    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }
}