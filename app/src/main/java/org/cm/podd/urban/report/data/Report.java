package org.cm.podd.urban.report.data;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.databinding.CellReportItemBinding;
import org.cm.podd.urban.report.fragment.ReportFeedFragment;
import org.cm.podd.urban.report.helper.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Report {
    public static final String INTENT_TARGET_TAG = ReportFeedFragment.class.getSimpleName();
    public static String REPORT_MODEL_KEY = "Report.REPORT_MODEL";
    public static String REPORT_POSITION_KEY = "Report.REPORT_POSITION";
    public static String REPORT_ID_KEY = "Report.REPORT_ID";


    public static final String TAG = Report.class.getSimpleName();


    public static class SummaryModel {
        public String texts[] = {"ยังไม่มีข้อมูล", "ยังไม่มีข้อมูล", "ยังไม่มีข้อมูล"};
        public String reports[] = {"0", "0", "0"};
        public String total = "0";

        public int humanCount;
        public int animalCount;
        public int envCount;

        public int reportTotal;

        public String getHumanCount() {
            return (humanCount) + "";
        }

        public String getAnimalCount() {
            return (animalCount) + "";
        }

        public String getEnvCount() {
            return (envCount) + "";
        }

        public int getHumanLevel() {
            return getLevel(humanCount);
        }

        public int getAnimalLevel() {
            return getLevel(animalCount);
        }

        public int getEnvLevel() {
            return getLevel(envCount);
        }

        public int getLevel(int count) {
            if (count <= 0) return 1;

            int[] orders = new int[]{
                    humanCount,
                    animalCount,
                    envCount,
            };

            int mLevel = 0;
            for (int i = 0; i < orders.length; i++)
                if (count >= orders[i]) mLevel++;

            return mLevel;
        }

    }

    public static class Model {
        public String note;


        /**
         * createdBy : 896
         * createdByName :
         * createdByThumbnailUrl :
         * remark :
         * administrationAreaAddress : ซ่อนเร้น
         * administrationAreaId : 114
         * likeCount : 0
         * meTooCount : 0
         * commentCount : 0
         * likeId : 0
         * meTooId : 0
         * date : 2015-10-10T11:06:38Z
         * firstImageThumbnail :
         * flag :
         * reportGuid : 4d542eec-a2af-4fa6-95c5-a8493e6ade9e
         * id : 79382
         * incidentDate : 2015-10-10
         * negative : true
         */

        /**
         * reportTypeCategoryCode : animal
         * stateId : 40
         * createdAt : 2015-10-26T07:55:10.083683+00:00
         * createdById : 1255
         */

        public String createdBy;
        public String createdByName;
        public String createdByThumbnailUrl;
        public String remark;
        public String administrationAreaAddress;
        public int administrationAreaId;
        public int likeCount;
        public int meTooCount;
        public int commentCount;
        public int likeId;
        public int meTooId;
        public String date;
        public String firstImageThumbnail;
        public String flag;
        public String guid;
        public int id;
        public String incidentDate;
        public boolean negative;

        public String reportTypeCategoryCode;
        public int stateId;
        public String createdAt;
        public int createdById;
        public boolean isPublic;
        public LocationEntity reportLocation;

        public int reportId;
        public int state;
        public String stateCode;
        public boolean testFlag;
        public List<ImagesEntity> images;
        public int reportTypeId;
        public String reportTypeName;
        public String formDataExplanation;
        public String renderedOriginalFormData;


        public User.Model createdByObject;

        public boolean isMeToo() {
            return meTooId != 0;
        }

        public boolean isLike() {
            return likeId != 0;
        }


        public static class ImagesEntity {
            /**
             * report : 76332
             * reportGuid : c312b30e-a8a3-4bc8-90f7-eba851f5f4ed
             * imageUrl : https://s3-ap-southeast-1.amazonaws.com/podd/c312b30e-a8a3-4bc8-90f7-eba851f5f4ed
             * thumbnailUrl : https://s3-ap-southeast-1.amazonaws.com/podd/c312b30e-a8a3-4bc8-90f7-eba851f5f4ed-thumbnail
             * note :
             * location : {}
             */

            public int report;
            public String guid;
            public String imageUrl;
            public String thumbnailUrl;
            public LocationEntity location;

            public LocationEntity getLocation() {
                return location;
            }

            public static class LocationEntity {
            }

        }

        public Model() {
        }

        public boolean hideImageView() {
            return (this.images.size() == 0);
        }

        public boolean hideFormData() {
            return "".equalsIgnoreCase(renderedOriginalFormData);
        }

        public boolean hideRemark() {
            return "".equalsIgnoreCase(remark);
        }

        public String getFirstImageThumbnail() {
            if (this.images.size() > 0) {
                return images.get(0).thumbnailUrl;
            } else {
                return null;
            }

        }

        public String getProfileImageThumbnail() {
            if ("".equalsIgnoreCase(this.createdByThumbnailUrl) == false) {
                return this.createdByThumbnailUrl;
            } else {
                return null;
            }


        }


        public String getJsonSerialized(Context context) {
            Gson gson = new Gson();
            String str = gson.toJson(this);
            return str;
        }

        public String getFormatCreatedAt() {
            return getCreatedStringFeed();

        }


        public String getCreatedAt() {
            return getCreatedStringFeed();
        }

        public String getCreatedStringFeed() {
            Date d = DateUtil.fromJsonDateString(createdAt, 7);
            String out = DateUtil.convertToThaiDate(d);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            out = String.format("%s %d:%02d น.", out, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
            return out;
        }


        public int getCatImageMipMap() {
            int mipmap;
            if (isPublic) {
                if ("animal".equalsIgnoreCase(reportTypeCategoryCode)) {
                    mipmap = R.mipmap.ic_cat_animal;
                } else if ("human".equalsIgnoreCase(reportTypeCategoryCode)) {
                    mipmap = R.mipmap.ic_cat_people;
                } else if ("environment".equalsIgnoreCase(reportTypeCategoryCode)) {
                    mipmap = R.mipmap.ic_cat_enviroment;
                } else {
                    mipmap = R.mipmap.ic_anonymous;
                }
            } else {
                mipmap = R.mipmap.ic_podd;
            }
            return mipmap;
        }

        public int getBackgroundResId() {
            int resId;
            if (isPublic) {
                if ("animal".equalsIgnoreCase(reportTypeCategoryCode)) {
                    resId = R.color.catAnimal;
                } else if ("human".equalsIgnoreCase(reportTypeCategoryCode)) {
                    resId = R.color.catPeople;
                } else if ("environment".equalsIgnoreCase(reportTypeCategoryCode)) {
                    resId = R.color.catEnvironment;
                } else {
                    resId = R.color.catPodd;
                }
            } else {
                resId = R.color.catPodd;
            }
            return resId;
        }

        public BitmapDescriptor getReportMakerColor() {
            if (isPublic) {
                if ("animal".equalsIgnoreCase(reportTypeCategoryCode)) {
                    return BitmapDescriptorFactory.fromResource(R.mipmap.pin_orange);
                } else if ("human".equalsIgnoreCase(reportTypeCategoryCode)) {
                    return BitmapDescriptorFactory.fromResource(R.mipmap.pin_yellow);
                } else if ("environment".equalsIgnoreCase(reportTypeCategoryCode)) {
                    return BitmapDescriptorFactory.fromResource(R.mipmap.pin_green);
                } else {
                    return BitmapDescriptorFactory.fromResource(R.mipmap.pin_violet);
                }
            } else {
                return BitmapDescriptorFactory.fromResource(R.mipmap.pin_violet);
            }
        }

        public String getRenderedOriginalFormData() {
            return Html.fromHtml(renderedOriginalFormData).toString();
        }

        public String getReportAddress() {
            if ("".equalsIgnoreCase(administrationAreaAddress))
                return "นอกพื้นที่จังหวัดเชียงใหม่";
            return administrationAreaAddress;
        }

        public int getMetooImageMipMap() {
            int mipmap;
            if (isMeToo()) {
                mipmap = R.mipmap.ic_like_active;
            } else {
                mipmap = R.mipmap.ic_like;
            }

            return mipmap;
        }

        public int getLikeImageMipMap() {
            int mipmap;
            if (isLike()) {
                mipmap = R.mipmap.ic_support_active;
            } else {
                mipmap = R.mipmap.ic_support;
            }

            return mipmap;
        }

    }


    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER
    // ADAPTER


    public static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        public static final String TAG = Adapter.class.getSimpleName();


        public static interface MainFeedListener {
            public void onLikeClick(boolean isLiked, Report.Model r, int position, View v);

            public void onDoLike(Report.Model r, int position, View v);

            public void onUnLike(Report.Model r, int position, View v);

            public void onCommentsClick(Report.Model r, int position, View v);

            public void onReportAbuse(Report.Model r, int position, int which, String str);

            public void onInfoIconClick(Report.Model r, int position, View v);

            public void onShareButtonClick(Report.Model r, int position, View v);

            public void onSupportButtonClick(Report.Model r, int position, View v);

            public void onImageClick(Report.Model r, int position, View v);

//            public void onProjectPhotoClick(Report.Model report, int position, View view);
        }


        private final Context mContext;
        private List<Model> mDataset;
        public MainFeedListener mListener = null;


        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final CellReportItemBinding binding;
            public View layout;

            @Bind(R.id.report_imageview)
            ImageView imageView;

            @Bind(R.id.image_section)
            RelativeLayout imageSection;

            @OnClick(R.id.share_imageview)
            public void onClick(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onShareButtonClick(report, position, v);
                }
            }

            @OnClick(R.id.support_button)
            public void onsupportbuttonclick(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onSupportButtonClick(report, position, v);
                }
            }

            @OnClick(R.id.comment_imageview)
            public void onClickComment(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onCommentsClick(report, position, v);
                }
            }


            @OnClick(R.id.report_imageview)
            public void onClickImageView(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onImageClick(report, position, v);
                }
            }


            @OnClick(R.id.profile_imageview)
            public void onClickProfile(View v) {

            }

            @OnClick(R.id.comment_view)
            public void onClickViewComment(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onCommentsClick(report, position, v);
                }
            }

            @OnClick(R.id.report_title_linearlayout)
            public void onClickViewTitle(View v) {
                int position = getAdapterPosition();
                final Report.Model report = mDataset.get(position);
                if (mListener != null) {
                    mListener.onCommentsClick(report, position, v);
                }
            }

            public ViewHolder(final CellReportItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                View v = binding.getRoot();
                layout = v;

                ButterKnife.bind(this, v);
            }
        }

        public void setCallback(Adapter.MainFeedListener listener) {
            mListener = listener;

        }

        // Create new views (invoked by the layout manager)
        @Override
        public Adapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
            // create a new view
            LayoutInflater li = LayoutInflater.from(parent.getContext());

            final CellReportItemBinding reportItemBinding =
                    DataBindingUtil.inflate(li, R.layout.cell_report_item, parent, false);

            ViewHolder vh = new ViewHolder(reportItemBinding);
            return vh;
        }

        @Override
        public void onBindViewHolder(Adapter.ViewHolder holder, int position) {
            Model report = mDataset.get(position);
            final User.Model u = new User.Model();

            u.avatarUrl = report.createdByThumbnailUrl;
            u.thumbnailAvatarUrl = u.avatarUrl;
            u.username = report.createdByName;
            u.id = report.createdById;

            holder.binding.setReport(report);

            holder.binding.setUser(report.createdByObject);

            if (report.images.size() > 0) {
                holder.imageSection.setVisibility(View.VISIBLE);
            } else {
                holder.imageSection.setVisibility(View.GONE);
            }
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

        public void updateReport(Report.Model model, int position) {
            Report.Model _model = mDataset.get(position);
            _model.likeId = model.likeId;
            _model.meTooId = model.meTooId;
            _model.meTooCount = model.meTooCount;
            _model.commentCount = model.commentCount;
            _model.likeCount = model.likeCount;

            mDataset.set(position, _model);
            notifyItemChanged(position, _model);
        }

    }


}
