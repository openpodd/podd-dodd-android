package org.cm.podd.urban.report.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cm.podd.urban.report.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolumnLinearLayout extends LinearLayout {
    private final Context mContext;
    private String mTitle;
    private View mRootView;

//    @Bind(R.id.attach_img) ImageView imageView;
    private int mValue = 0;
    @Bind(R.id.value_textview) TextView mValueTextView;
    @Bind(R.id.minus_imagebutton) ImageButton minusImageButton;
    @Bind(R.id.add_imagebutton) ImageButton addImageButton;


    @OnClick(R.id.minus_imagebutton)
    public void onClickMinus(View v) {
        mValue--;
        if (mValue < 0) {
            mValue = 0;
        }
        mValueTextView.setText(String.valueOf(mValue));
    }

    @OnClick(R.id.add_imagebutton)
    public void onClickAdd(View v) {
        mValue++;
        mValueTextView.setText(String.valueOf(mValue));
    }

    public VolumnLinearLayout(Context context) {
        super(context);
        mRootView = LayoutInflater.from(context).inflate(R.layout.volumn_linear_layout, this);
        ButterKnife.bind(this, mRootView);
        mContext = context;
    }

    public VolumnLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.volumn_linear_layout, this);
        mContext = context;
        ButterKnife.bind(this, mRootView);

        mValueTextView.setText(String.valueOf(mValue));

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.VolumnLinearLayout,
                0, 0);

        try {
            mTitle = a.getString(R.styleable.VolumnLinearLayout_volumn_title);
//            mRootView.findViewById(R.id.volumn_title_textview)
            TextView tv = ButterKnife.findById(mRootView, R.id.volumn_title_textview);
            tv.setText(mTitle);
        } finally {
            a.recycle();

        }


    }

    public VolumnLinearLayout setImageResource(String path) {
//        Glide.with(mContext).load(path).into(imageView);
        return this;
    }

    public VolumnLinearLayout setImageResource(Uri path) {
        int width = (int) mContext.getResources().getDimension(R.dimen.close_icon_width);
        int height = (int) mContext.getResources().getDimension(R.dimen.close_icon_height);
//        Glide.with(mContext).load(path)
//                .centerCrop()
//                .override(width, height)
//                .into(imageView);
        return this;
    }

    public VolumnLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }


    public static class VolumnViewModel {

    }

    public int getValue() {
        return mValue;
    }


}