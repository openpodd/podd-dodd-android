package org.cm.podd.urban.report.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.data.Comment;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.CellCommentBinding;
import org.cm.podd.urban.report.databinding.CellReportItemBinding;
import org.cm.podd.urban.report.delegate.ReportDelegate;

import java.util.List;

import butterknife.OnClick;


/**
 * Created by anandbose on 09/06/15.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private Context mContext;

    private User.Model mUser;
    private List<Item> data;

    private Report.Model mReport;

    Report.Adapter.MainFeedListener listener;
    private static final int COMMENT_SERIALIZED = 0x01;
    private static final int COMMENT_POSITION = 0x02;

    public void updateReport(Report.Model model) {


    }

    public CommentAdapter setListener(Report.Adapter.MainFeedListener listener) {
        this.listener = listener;
        return this;
    }

    public CommentAdapter(List<Item> data) {
        this.data = data;
    }


    @OnClick(R.id.share_imageview)
    public void onClickShare(View v) {
        if (listener != null) {
//            listener.onShareButtonClick();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CellReportItemBinding bindingHeader = DataBindingUtil.inflate(inflater,
                        R.layout.cell_report_item, parent, false);

                view = bindingHeader.getRoot();
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);

                mContext = view.getContext();

                return header;
            case CHILD:
                LayoutInflater inflater2 = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                CellCommentBinding binding = DataBindingUtil.inflate(inflater2, R.layout.cell_comment, parent, false);
                view = binding.getRoot();
                mContext = view.getContext();
                return new RecyclerView.ViewHolder(view) {
                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                CellReportItemBinding bindingHeader = DataBindingUtil.getBinding(holder.itemView);

                User.Model u = new User.Model();
                u.avatarUrl =  item.report.createdByThumbnailUrl;
                u.thumbnailAvatarUrl =  item.report.createdByThumbnailUrl;
                u.username = item.report.createdByName;
                u.name = item.report.createdByName;
                u.id = item.report.createdById;


                bindingHeader.setUser(u);
                bindingHeader.setReport(item.report);
                mReport = item.report;


                ReportDelegate.manualBindDelegateEvent(holder.itemView, item.report, position, listener);

//                View view = bindingHeader.getRoot();

                break;
            case CHILD:
                CellCommentBinding binding = DataBindingUtil.getBinding(holder.itemView);
                binding.setUser(item.comment.createdBy);
                binding.setComment(item.comment);
                Gson gson = new Gson();
//                TextView itemTextView = (TextView) holder.itemView;
//                itemTextView.setText(data.get(position).text);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout wrapper;
        //        public TextView header_title;
//        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
//            header_title = (TextView) itemView.findViewById(R.id.header_title);
//            wrapper = (LinearLayout) itemView.findViewById(R.id.rootView);
//            wrapper.setBackgroundDrawable(itemView.getContext().getResources().getDrawable(R.mipmap.ic_cat_people));
//            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    public static class Item {
        public int type;
        public Comment.Model comment;
        public Report.Model report;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, Comment.Model comment, Report.Model report) {
            this.type = type;
            this.report = report;
            this.comment = comment;
        }

        public Item(int type, Report.Model report) {
            this.type = type;
            this.report = report;
        }
    }

}
