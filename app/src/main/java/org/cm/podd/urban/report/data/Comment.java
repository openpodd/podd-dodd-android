package org.cm.podd.urban.report.data;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.databinding.CellCommentBinding;
import org.cm.podd.urban.report.helper.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by nat on 9/21/15 AD.
 */
public class Comment {
    public static final String TAG = Comment.class.getSimpleName();

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
         * id : 5351
         * reportId : 80347
         * message : vhgjhf
         * fileUrl : null
         */

        public int id;
        public int reportId;
        public String message;
        public String fileUrl;
        public String createdAt;

        public User.Model createdBy;

        public String getCreatedAt() {
            Date d = DateUtil.fromJsonDateString(createdAt, 7);
            String out = DateUtil.convertToThaiDate(d);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            out = String.format("%s %d:%02d น.", out, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            return out;
        }

    }

    //
    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private final Context mContext;
        private List<Model> mDataset;



        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final CellCommentBinding binding;
            public View layout;


            public ViewHolder(final CellCommentBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                View v = binding.getRoot();
                layout = v;

                ButterKnife.bind(this, v);
            }
        }


        // Create new views (invoked by the layout manager)
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
            // create a new view
            LayoutInflater li = LayoutInflater.from(parent.getContext());

            final CellCommentBinding reportItemBinding =
                    DataBindingUtil.inflate(li, R.layout.cell_comment, parent, false);


//
//            screenSize = Utils.getScreenSize(mContext);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(reportItemBinding);
            return vh;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            Comment.Model comment = mDataset.get(position);
            holder.binding.setComment(comment);
            holder.binding.setUser(comment.createdBy);

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

        public void setComments(ArrayList<Model> myDataset) {
            mDataset = myDataset;
            notifyDataSetChanged();
        }

    }
}
