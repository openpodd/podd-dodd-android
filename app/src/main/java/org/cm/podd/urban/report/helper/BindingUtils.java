package org.cm.podd.urban.report.helper;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.transform.CircleTransform;
import org.cm.podd.urban.report.widget.ComplexCircleLinearLayout;

public class BindingUtils {
    public static final String TAG = BindingUtils.class.getSimpleName();

    @BindingAdapter({"bind:thumbnailImage"})
    public static void loadImage30(ImageView view, String url) {
        Context context = view.getContext();
        Glide.with(context).load(url).
                error(R.mipmap.img_slide000).
                fitCenter().into(view);
    }

    @BindingAdapter({"bind:profileInListView"})
    public static void loadImage90(ImageView view, String url) {
        Context context = view.getContext();
        Glide.with(context).load(url).
                placeholder(R.mipmap.arrow_comment).
                error(R.mipmap.default_image_profile).
                transform(new CircleTransform(context)).into(view);
    }


    @BindingAdapter({"bind:catImage"})
    public static void loadIcon(ImageView view, int mipmap) {
        Context context = view.getContext();
        Glide.with(context).load(mipmap)
                .placeholder(R.mipmap.arrow_comment).
                transform(new CircleTransform(context)).into(view);
    }

    @BindingAdapter({"bind:catBgColor"})
    public static void carBgColor(LinearLayout view, Report.Model r) {
        view.setBackgroundResource(r.getBackgroundResId());

    }


    @BindingAdapter({"bind:circleNumber"})
    public static void circleNumber(ComplexCircleLinearLayout view, String text) {
        view.setNumber(text);
    }

    @BindingAdapter({"bind:circleLevel"})
    public static void circleLevel(ComplexCircleLinearLayout view, int text) {
        Log.d(TAG, "circleLevel: " + text);
        view.setLevel(text);
    }

    @BindingAdapter({"bind:viewVisible"})
    public static void viewVisible(TextView view, int canView) {
        view.setVisibility(canView);
    }


    @BindingAdapter({"bind:resourceView"})
    public static void loadResourceView(ImageView view, int resource) {
        view.setImageResource(resource);
    }

    @BindingAdapter({"bind:resourceLeftImage"})
    public static void loadIconTextView(TextView view, int resource) {
        view.setCompoundDrawablesWithIntrinsicBounds(resource, 0, 0, 0);
    }

//
//    @BindingAdapter({"bind:fullImage"})
//    public static void loadFullImage(ImageView view, String url) {
//        Picasso.with(view.getContext()).load(url).into(view);
////        Context context = view.getContext();
////        AppHelper.loadCircleImage(context, view, url);
//    }
//
//    @BindingAdapter({"bind:passDuration"})
//    public static void pass(TextView textView, String dateString) {
//        try {
//            Date d =
//                    AppHelper.
//                            parseDateFromString(dateString);
//            String passString = AppHelper.getPassDuration(d) + "ที่แล้ว";
//            textView.setText(passString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

}

