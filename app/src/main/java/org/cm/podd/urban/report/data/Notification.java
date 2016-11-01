package org.cm.podd.urban.report.data;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.report.ReportDetailActivity;
import org.cm.podd.urban.report.databinding.CellNotiBinding;
import org.cm.podd.urban.report.helper.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by nat on 9/21/15 AD.
 */
public class Notification {
    public static final String TAG = Notification.class.getSimpleName();

    /**
     * id : 641
     * action : update follow project
     * target : null
     * target_anchor : null
     * message : มีรายงานใหม่สำหรับโครงการ: michaeldavidpaul 49
     * send_user : {"id":94,"email":"aaaa@aaaa.com","avatar_url":"https://thaispendingwatch.s3.amazonaws.com/309544a5-c67d-4e58-993e-4df9bfda4c04.JPG","is_anonymous":false,"display_name":"34234234","bio":"สถานการณ์ \u201cน้ำแล้ง\u201d แทรกมาเป็นวาระด่วนเฉพาะหน้าให้รัฐบาลภายใต้การนำของ \u201cบิ๊กตู่\u201d พล.อ.ประยุทธ์ จันทร์โอชา นายกรัฐมนตรี หัวหน้า คสช.ต้องเหนื่อยขึ้นอีกหลายเท่า","followers_count":3,"followings_count":6,"reports_count":32,"projects_count":1,"is_followed":false}
     * created_at : 2015-09-21T08:14:18.803Z
     * seen_at : null
     * is_deleted : false
     */

    public static class Model {
        /**
         * id : 4835
         * report : 80377
         * receiveUser : 1255
         * type : SUPPORT_ME_TOO
         * createdAt : 2015-10-27T16:08:35.999Z
         * reportTypeName : สัตว์กัด
         * reportFirstThumbnailUrl : null
         * renderWebMessage : rrr@rrr.com ประสบด้วย กับรายงาน สัตว์กัด ของคุณ
         * message : null
         */

        public int id;
        public int report;
        public int receiveUser;
        public String type;
        public String createdAt;
        public String reportTypeName;
        public String reportFirstThumbnailUrl;
        public String renderWebMessage;
        public String message;
        public User.Model createdBy;

        public String getCreatedString() {
            Date d = DateUtil.fromJsonDateString(createdAt, 7);
            String out = DateUtil.convertToThaiDate(d);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            out = String.format("%s %d:%02d น.", out, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            return out;
        }

        public String getRenderWebMessage() {
            if (renderWebMessage!= null) {
                String render = renderWebMessage;
                render = render.replaceAll("@\\[reportType:\\((.*?)\\)\\]", "<b></b>");
//                render = render.replaceAll("((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])", "<a href=\"$1\">$1</a>");
                return Html.fromHtml(render).toString();
            }
            else
                return "";
        }

        public String getLink() {
            if (renderWebMessage!= null) {
                Pattern WEB_PATTERN = Pattern.compile("((https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
                Matcher m = WEB_PATTERN.matcher(renderWebMessage);
                if (m.find()) {
                    String web = m.group(1);
                    return web;
                }

                return null;
            }
            else
                return null;
        }

        public int getNotificationIcon() {
            if (type.startsWith("SUPPORT_LIKE"))
                return R.mipmap.ic_support_active;
            else if (type.startsWith("SUPPORT_ME_TOO"))
                return R.mipmap.ic_like_active;
            else if (type.startsWith("SUPPORT_COMMENT"))
                return R.mipmap.ic_noti_comment;
            else
                return R.mipmap.ic_noti_dodd;
        }

        public boolean hideImageView() {
            return (reportFirstThumbnailUrl == null);
        }
    }


    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private final Context mContext;
        private List<Model> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final CellNotiBinding binding;
            public View layout;
            @Bind(R.id.textview)
            TextView textView;

            @OnClick(R.id.cell_wrapper)
            public void onClick(View v) {
                nextPage();
            }

            @OnClick(R.id.textview)
            public void onClickText(View v) {
                nextPage();
            }

            public ViewHolder(final CellNotiBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                View v = binding.getRoot();
                layout = v;

                ButterKnife.bind(this, v);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            public void nextPage() {
                int position = getAdapterPosition();
                final Notification.Model notification = mDataset.get(position);

                final int reportId = notification.report;
                final Intent intent = new Intent(mContext, ReportDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Report.REPORT_ID_KEY, reportId);
                intent.putExtras(bundle);

                if (reportId > 0)
                    mContext.startActivity(intent);

                if (notification.getLink() != null) {
                    String url = notification.getLink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    mContext.startActivity(i);
                }
            }

        }


        // Create new views (invoked by the layout manager)
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
            // create a new view
            LayoutInflater li = LayoutInflater.from(parent.getContext());

            final CellNotiBinding reportItemBinding =
                    DataBindingUtil.inflate(li, R.layout.cell_noti, parent, false);


//
//            screenSize = Utils.getScreenSize(mContext);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(reportItemBinding);
            return vh;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            Notification.Model notification = mDataset.get(position);
            holder.binding.setNotification(notification);
            holder.binding.setUser(notification.createdBy);


        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void add(Model item, int position) {
            mDataset.add(position, item);
            try {
                notifyItemInserted(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clearDataSet() {
            mDataset = new ArrayList<Model>();
            notifyDataSetChanged();
        }


        public Adapter(Context c, ArrayList<Model> myDataset) {
            super();
            mContext = c;
            mDataset = myDataset;
        }

        public Adapter(Context c) {
            super();
            mContext = c;
            mDataset = new ArrayList<Model>();
        }

        public void setReports(ArrayList<Model> myDataset) {
            mDataset = myDataset;
            notifyDataSetChanged();
        }

    }
}
