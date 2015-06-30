
package com.synova.realestate.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.models.House;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/13/15.
 */
public class HouseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener {

    private List<AdsInfoResponseEnt> houses = new ArrayList<>();

    private OnRecyclerViewItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnRecyclerViewItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setItems(List<AdsInfoResponseEnt> houses) {
        this.houses = houses;
        notifyDataSetChanged();
    }

    public void addItem(AdsInfoResponseEnt house) {
        houses.add(house);
        notifyItemInserted(houses.size() - 1);
    }

    public void addItems(List<AdsInfoResponseEnt> houses) {
        int start = this.houses.size();
        this.houses.addAll(houses);
        notifyItemRangeInserted(start, houses.size() - 1);
    }

    @Override
    public int getItemCount() {
        return houses.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == houses.size() ? Constants.RecyclerViewType.FOOTER.ordinal()
                : Constants.RecyclerViewType.ITEM.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        Constants.RecyclerViewType type = Constants.RecyclerViewType.values()[viewType];
        switch (type) {
            case HEADER:
                break;
            case ITEM:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.layout_tab_list_item, viewGroup, false);
                view.setOnClickListener(this);
                return new HouseViewHolder(view);
            case FOOTER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.layout_footer_load_more, viewGroup, false);
                view.setOnClickListener(this);
                return new RecyclerView.ViewHolder(view) {
                };
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        int viewType = getItemViewType(position);
        Constants.RecyclerViewType type = Constants.RecyclerViewType.values()[viewType];
        switch (type) {
            case HEADER:
                break;
            case ITEM:
                AdsInfoResponseEnt house = houses.get(position);
                HouseListAdapter.HouseViewHolder holder = (HouseViewHolder) h;

                ImageLoader.getInstance().displayImage(house.imageUrl, holder.ivPhoto);
                int price = Integer.parseInt(house.mminMaxPrice.split("-")[0].replace(" ", ""));
                holder.tvPrice.setText(price <= Constants.HOUSE_PRICE_LIMIT ? Util
                        .formatPriceNumber(price) + "€" : "€");
                holder.tvTitle.setText(house.title);

                String description = String.format(
                        holder.tvDescription.getContext().getString(
                                R.string.list_item_description_template), house.roomNumber,
                        house.surface, house.distance);
                holder.tvDescription.setText(Html.fromHtml(description));
                break;
            case FOOTER:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (onItemClickedListener != null) {
            RecyclerView parent = (RecyclerView) v.getParent();
            int position = parent.getChildAdapterPosition(v);
            onItemClickedListener.onItemClicked(parent, v, position, v.getId(),
                    houses.get(position));
        }
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;
        public TextView tvPrice;
        public TextView tvTitle;
        public TextView tvDescription;

        public HouseViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.list_item_ivPhoto);
            tvPrice = (TextView) itemView.findViewById(R.id.list_item_tvPrice);
            tvTitle = (TextView) itemView.findViewById(R.id.list_item_tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.list_item_tvDescription);
        }
    }
}
