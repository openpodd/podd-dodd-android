package org.cm.podd.urban.report.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.data.Report;
import org.cm.podd.urban.report.data.User;
import org.cm.podd.urban.report.databinding.CellReportItemBinding;
import org.cm.podd.urban.report.databinding.ContentProfileBinding;
import org.cm.podd.urban.report.delegate.ReportDelegate;
import org.cm.podd.urban.report.helper.AppHelper;

import java.util.List;


/**
 * Created by anandbose on 09/06/15.
 */
public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private User.Model mUser;
    private List<Item> data;

    public static final String TAG = ProfileAdapter.class.getSimpleName();


    public ProfileAdapter setCallback(Report.Adapter.MainFeedListener mListener) {
        this.mListener = mListener;
        return this;
    }

    Report.Adapter.MainFeedListener mListener;

    public ProfileAdapter(List<Item> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (type) {
            case HEADER:
                ContentProfileBinding bindingHeader = DataBindingUtil.inflate(inflater,
                        R.layout.content_profile, parent, false);

                view = bindingHeader.getRoot();
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);


                return header;
            case CHILD:
                CellReportItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.cell_report_item, parent, true);
                view = binding.getRoot();
                return new RecyclerView.ViewHolder(view) {
                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                ContentProfileBinding bindingHeader = DataBindingUtil.getBinding(holder.itemView);
                bindingHeader.setUser(AppHelper.getUserSerialized(holder.itemView.getContext()));
                break;
            case CHILD:
                CellReportItemBinding binding = DataBindingUtil.getBinding(holder.itemView);

                ReportDelegate.manualBindDelegateEvent(holder.itemView, item.report, position, mListener);


                binding.setReport(item.report);

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


    public void updateReport(Report.Model model, int position) {

        if (position >= getItemCount()) {
            // no report
            String string = String.format("ProfileAdapter: position>= getItemCount: " +
                    "size=%d position = %d ", getItemCount(), position);
            Crashlytics.getInstance().core.log(string);
            return;
        }


        Item item  = data.get(position);
        Report.Model _model = item.report;
        _model.likeId = model.likeId;
        _model.meTooId = model.meTooId;
        _model.meTooCount = model.meTooCount;
        _model.commentCount = model.commentCount;
        _model.likeCount = model.likeCount;

        item.report = _model;
        data.set(position, item);
        Gson gson = new Gson();
        Log.d(TAG, " (line ): " + gson.toJson(_model));
        notifyItemChanged(position, _model);
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout wrapper;
        //        public TextView header_title;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
        // header_title = (TextView) itemView.findViewById(R.id.header_title);
        }
    }

    public static class Item {
        public int type;
        public Report.Model report;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, Report.Model report) {
            this.type = type;
            this.report = report;
        }
    }
}
