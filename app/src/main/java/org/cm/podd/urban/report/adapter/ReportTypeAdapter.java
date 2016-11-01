package org.cm.podd.urban.report.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.cm.podd.urban.report.MyApplication;
import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.activity.report.ReportFormActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by anandbose on 09/06/15.
 */
public class ReportTypeAdapter extends RecyclerView.Adapter<ReportTypeAdapter.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    private Activity mActivity;
    private Tracker mTracker;
    public Context mContext;

    private boolean isFirstTime = true;

    private List<Item> data;

//    public IMyViewHolderClicks mListener;

    public static String CAT_TYPE_STR_RES_KEY = "STRING_RES";
    public static String CAT_CODE_STRING = "CAT_CODE_STRING";
    public static String CAT_COLOUR_INT = "CAT_COLOUR_INT";

    public static final String TAG = ReportTypeAdapter.class.getSimpleName();
    private int COLLAPSE = 0x01;
    private int EXPAND = 0x02;

    public ReportTypeAdapter(List<Item> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final int mType;
        private final Context context;
        @Bind(R.id.textview)
        TextView textView;

        public ViewHolder(View itemView, final int type) {
            super(itemView);
            mType = type;
            context = itemView.getContext();
            mContext = context;
            mActivity = (Activity) context;
            MyApplication app = (MyApplication) mActivity.getApplication();
            mTracker = app.getTracker(MyApplication.TrackerName.APP_TRACKER);
            if (type == CHILD) {
                ButterKnife.bind(this, itemView);
            }
        }

        @OnClick(R.id.textViewWrapper)
        public void onClickCell(View v) {
            Item item = data.get(getAdapterPosition());
            Intent i = new Intent(mContext, ReportFormActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt(CAT_TYPE_STR_RES_KEY, item.stringRes);
            bundle.putString(CAT_CODE_STRING, item.code);
            bundle.putInt(CAT_COLOUR_INT, item.colour);
            i.putExtras(bundle);
            if (mTracker != null) {
                mTracker.setScreenName("REPORT TYPE:" + mContext.getString(item.stringRes));
                mTracker.setScreenName("REPORT CODE:" + item.code);
                mTracker.send(new HitBuilders.AppViewBuilder().build());
            }
            mContext.startActivity(i);
        }
    }


    private class ListHeaderViewHolder extends ViewHolder {
        public LinearLayout wrapper;
        public TextView header_title;
        public ImageView btn_expand_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView, HEADER);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            wrapper = (LinearLayout) itemView.findViewById(R.id.rootView);
            btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (type) {
            case HEADER:
                view = inflater.inflate(R.layout.list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                view = inflater.inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view, CHILD);
        }
        return null;
    }

    private void set_image_res(ImageView view, int mipmap, int mode) {
        if (mode == COLLAPSE) {
            switch (mipmap) {
                case R.mipmap.ic_cat_people:
                    view.setImageResource(R.mipmap.ic_arrow_collapse_yellow);
                    break;
                case R.mipmap.ic_cat_animal:
                    view.setImageResource(R.mipmap.ic_arrow_collapse_orange);
                    break;
                case R.mipmap.ic_cat_enviroment:
                    view.setImageResource(R.mipmap.ic_arrow_collapse_green);
                    break;
            }

        } else if (mode == EXPAND) {
            switch (mipmap) {
                case R.mipmap.ic_cat_people:
                    view.setImageResource(R.mipmap.ic_arrow_expand_yellow);
                    break;
                case R.mipmap.ic_cat_animal:
                    view.setImageResource(R.mipmap.ic_arrow_expand_orange);
                    break;
                case R.mipmap.ic_cat_enviroment:
                    view.setImageResource(R.mipmap.ic_arrow_expand_green);
                    break;
            }

        }


    }

    public void collapse(Item item, ListHeaderViewHolder itemController, View v) {

        item.invisibleChildren = new ArrayList<Item>();
        int count = 0;
        int pos = data.indexOf(itemController.refferalItem);
        while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
            item.invisibleChildren.add(data.remove(pos + 1));
            count++;
        }
        notifyItemRangeRemoved(pos + 1, count);
        set_image_res(itemController.btn_expand_toggle, item.mipmap, EXPAND);

        scrollToPosition(pos, 50, v);
    }

    public void expand(Item item, ListHeaderViewHolder itemController, View v) {
        int pos = data.indexOf(itemController.refferalItem);
        int index = pos + 1;
        for (Item i : item.invisibleChildren) {
            data.add(index, i);
            index++;
        }
        notifyItemRangeInserted(pos + 1, index - pos - 1);
        set_image_res(itemController.btn_expand_toggle, item.mipmap, COLLAPSE);
        item.invisibleChildren = null;

        scrollToPosition(pos + 1, 10, v);

    }

    public void scrollToPosition(int position, int dy, View v) {
        try {
            RecyclerView recyclerView = (RecyclerView) v.getParent();
            recyclerView.scrollToPosition(position);
            if (position > 0)
                recyclerView.smoothScrollBy(0, dy);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void expandEvent(Item item, ListHeaderViewHolder itemController, View v) {
        if (item.invisibleChildren == null) {
            if (mTracker != null) {
                mTracker.setScreenName("Collapse:" + mContext.getString(item.stringRes));
                mTracker.send(new HitBuilders.AppViewBuilder().build());
            }
            collapse(item, itemController, v);
        } else {
            if (mTracker != null) {
                mTracker.setScreenName("Expand:" + mContext.getString(item.stringRes));
                mTracker.send(new HitBuilders.AppViewBuilder().build());
            }
            expand(item, itemController, v);
        }

    }


    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                TextView textView = (TextView) ((ListHeaderViewHolder) holder).itemView.findViewById(R.id.header_title);
                TextView textView1 = (TextView) ((ListHeaderViewHolder) holder).itemView.findViewById(R.id.title_textview);
                ImageView imageView = (ImageView)((ListHeaderViewHolder) holder).itemView.findViewById(R.id.report_type_imageview);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(mContext.getResources().getDrawable(item.mipmap));
                    if (item.mipmap == R.mipmap.ic_cat_people) {
                        ((ListHeaderViewHolder) holder).itemView.setBackgroundColor(mContext.getResources().getColor(item.colour));
                        textView1.setText("คน");

                    } else if (item.mipmap == R.mipmap.ic_cat_animal) {
                        ((ListHeaderViewHolder) holder).itemView.setBackgroundColor(mContext.getResources().getColor(item.colour));
                        textView1.setText("สัตว์");

                    } else if (item.mipmap == R.mipmap.ic_cat_enviroment) {
                        ((ListHeaderViewHolder) holder).itemView.setBackgroundColor(mContext.getResources().getColor(item.colour));
                        textView1.setText("สิ่งแวดล้อม");
                    }
                } else {
                    itemController.header_title.setText(item.text);
                }

                itemController.refferalItem = item;

                if (item.invisibleChildren == null) {
                    set_image_res(itemController.btn_expand_toggle, item.mipmap, EXPAND);
                } else {
                    set_image_res(itemController.btn_expand_toggle, item.mipmap, EXPAND);
                }

                itemController.wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFirstTime = false;
                        expandEvent(item, itemController, v);
                    }
                });

                break;
            case CHILD:
                ImageView itemImageView = (ImageView) holder.itemView.findViewById(R.id.imageView);
                holder.textView.setText(data.get(position).text);
                int width = (int) mContext.getResources().getDimension(R.dimen.report_type_icon_width);
                int height = (int) mContext.getResources().getDimension(R.dimen.report_type_icon_height);

                Log.d(TAG, "CHILD (line 189): " + data.get(position).text);
                Glide.with(holder.itemView.getContext())
                        .load(data.get(position).mipmap)
                        .override(width, height)
                        .into(itemImageView);

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


    public static class Item {
        public String code;
        public int stringRes;
        public int type;
        public int mipmap;
        public String text;
        public int colour;
        public List<Item> invisibleChildren;


        public Item(int type, Context c, int stringRes) {
            this.type = type;
            this.stringRes = stringRes;
            this.text = c.getString(stringRes);
            this.mipmap = R.mipmap.arrow_comment;

            switch (stringRes) {
                case R.string.IC_CAT_HUMAN:
                    mipmap = R.mipmap.ic_cat_people;
                    colour = R.color.catPeople;
                    this.code = "-";
                    break;
                case R.string.IC_CAT_HUMAN_FOOD_POISON:
                    mipmap = R.mipmap.ic_people_poison;
                    this.code = "public-1-human-food-poisoning";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_FOOD_DIRTY_SHOP:
                    mipmap = R.mipmap.ic_people_dirty;
                    this.code = "public-1-human-unclean-food";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_FOOD_CONTAMINATED_SUSPECTED:
                    mipmap = R.mipmap.ic_people_mix;
                    this.code = "public-1-human-food-contaminant";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT:
                    mipmap = R.mipmap.ic_people_cheepmeat;
                    this.code = "public-1-human-cheap-meat";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL:
                    mipmap = R.mipmap.ic_people_oil;
                    this.code = "public-1-human-repeatedly-used-oil";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_DRUG_FAKE_DRUG:
                    mipmap = R.mipmap.ic_people_medicine;
                    this.code = "public-1-human-fake-drug";
                    colour = R.color.catPeople;
                    break;
                case R.string.IC_CAT_HUMAN_DRUG_NON_FDA:
                    mipmap = R.mipmap.ic_people_herb;
                    colour = R.color.catPeople;
                    this.code = "public-1-human-drug-no-fda";
                    break;
                case R.string.IC_CAT_HUMAN_DANGER_COSMETIC:
                    mipmap = R.mipmap.ic_people_cosmetic;
                    colour = R.color.catPeople;
                    this.code = "public-1-human-dangerous-cosmetic";
                    break;
                case R.string.IC_CAT_ANIMAL:
                    colour = R.color.catAnimal;
                    mipmap = R.mipmap.ic_cat_animal;
                    this.code = "-";
                    break;
                case R.string.IC_CAT_ANIMAL_BITTEN:
                    colour = R.color.catAnimal;
                    mipmap = R.mipmap.ic_animal_dog;
                    this.code = "public-1-animal-bites";
                    break;
                case R.string.IC_CAT_ANIMAL_DEAD:
                    colour = R.color.catAnimal;
                    mipmap = R.mipmap.ic_animal_sick;
                    this.code = "public-2-animal-sickdeath";
                    break;
                case R.string.IC_CAT_ENVI:
                    colour = R.color.catEnvironment;
                    mipmap = R.mipmap.ic_cat_enviroment;
                    this.code = "-";
                    break;

                case R.string.IC_CAT_ENVI_NOISY:
                    colour = R.color.catEnvironment;
                    mipmap = R.mipmap.ic_ev_lound;
                    this.code = "public-1-env-loud-noise";
                    break;
                case R.string.IC_CAT_ENVI_GARBAGE:
                    mipmap = R.mipmap.ic_ev_garbage;
                    colour = R.color.catEnvironment;
                    this.code = "public-1-env-junk";
                    break;
                case R.string.IC_CAT_ENVI_DIRTY_WATER:
                    mipmap = R.mipmap.ic_ev_waterdirty;
                    colour = R.color.catEnvironment;
                    this.code = "public-1-env-polluted-water";
                    break;
                case R.string.IC_CAT_ENVI_MOSQUITO_PROPAGATE:
                    mipmap = R.mipmap.ic_ev_mosqi;
                    colour = R.color.catEnvironment;
                    this.code = "public-1-env-mosquito";
                    break;
                case R.string.IC_CAT_ENVI_SMOG:
                    mipmap = R.mipmap.ic_ev_smoke;
                    colour = R.color.catEnvironment;
                    this.code = "public-1-env-smoke";
                    break;
                case R.string.IC_CAT_ENVI_FIRE:
                    mipmap = R.mipmap.ic_ev_fire;
                    this.code = "public-1-env-wildfire";
                    colour = R.color.catEnvironment;
                    break;
                case R.string.IC_CAT_ENVI_FLOOD:
                    mipmap = R.mipmap.ic_ev_flood;
                    this.code = "public-1-env-flash-flood";
                    colour = R.color.catEnvironment;
                    break;
            }
        }

    }

     public static int getItem(Context c, String name) {
        int stringRes = -1;

//            if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN))){
//                stringRes = R.string.IC_CAT_HUMAN;
//            }

        if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_FOOD_POISON))) {
            stringRes = R.string.IC_CAT_HUMAN_FOOD_POISON;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_FOOD_DIRTY_SHOP))) {
            stringRes = R.string.IC_CAT_HUMAN_FOOD_DIRTY_SHOP;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_FOOD_CONTAMINATED_SUSPECTED))) {
            stringRes = R.string.IC_CAT_HUMAN_FOOD_CONTAMINATED_SUSPECTED;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT))) {
            stringRes = R.string.IC_CAT_HUMAN_FOOD_TOO_CHEAP_MEAT;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL))) {
            stringRes = R.string.IC_CAT_HUMAN_FOOD_REPEATED_USED_OIL;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_DRUG_FAKE_DRUG))) {
            stringRes = R.string.IC_CAT_HUMAN_DRUG_FAKE_DRUG;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_DRUG_NON_FDA))) {
            stringRes = R.string.IC_CAT_HUMAN_DRUG_NON_FDA;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_HUMAN_DANGER_COSMETIC))) {
            stringRes = R.string.IC_CAT_HUMAN_DANGER_COSMETIC;
        }

//            else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ANIMAL))) {
//                stringRes = R.string.IC_CAT_ANIMAL;
//            }
        else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ANIMAL_BITTEN))) {
            stringRes = R.string.IC_CAT_ANIMAL_BITTEN;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ANIMAL_DEAD))) {
            stringRes = R.string.IC_CAT_ANIMAL_DEAD;
        }

//            else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI))) {
//                stringRes = R.string.IC_CAT_ENVI;
//            }

        else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_NOISY))) {
            stringRes = R.string.IC_CAT_ENVI_NOISY;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_GARBAGE))) {
            stringRes = R.string.IC_CAT_ENVI_GARBAGE;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_DIRTY_WATER))) {
            stringRes = R.string.IC_CAT_ENVI_DIRTY_WATER;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_MOSQUITO_PROPAGATE))) {
            stringRes = R.string.IC_CAT_ENVI_MOSQUITO_PROPAGATE;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_SMOG))) {
            stringRes = R.string.IC_CAT_ENVI_SMOG;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_FIRE))) {
            stringRes = R.string.IC_CAT_ENVI_FIRE;
        } else if (name.equalsIgnoreCase(c.getString(R.string.IC_CAT_ENVI_FLOOD))) {
            stringRes = R.string.IC_CAT_ENVI_FLOOD;
        } else {
            stringRes = -1;
        }

        return stringRes;
    }
}
