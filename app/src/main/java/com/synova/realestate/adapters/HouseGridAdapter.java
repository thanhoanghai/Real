
package com.synova.realestate.adapters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.models.AdsInfoResponseEnt;
import com.synova.realestate.utils.Util;

/**
 * Created by ducth on 6/13/15.
 */
public class HouseGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>implements
        View.OnClickListener {

    private List<AdsInfoResponseEnt> houses = new ArrayList<>();
    private Set<Integer> selectedItems = new HashSet<>();

    private OnRecyclerViewItemClickedListener onItemClickedListener;

    public void setOnItemClickedListener(OnRecyclerViewItemClickedListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setItems(List<AdsInfoResponseEnt> houses) {
        this.houses = houses;
        notifyDataSetChanged();
    }

    public List<AdsInfoResponseEnt> getItems() {
        return houses;
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
        return houses.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 3 ? Constants.RecyclerViewType.HEADER.ordinal()
                : Constants.RecyclerViewType.ITEM.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        Constants.RecyclerViewType type = Constants.RecyclerViewType.values()[viewType];
        switch (type) {
            case HEADER:
                view = new View(viewGroup.getContext());
                view.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Util.dpToPx(viewGroup.getContext(), 60)));
                return new RecyclerView.ViewHolder(view) {
                };
            case ITEM:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.layout_square_grid_item, viewGroup, false);
                view.setOnClickListener(this);
                return new HouseViewHolder(view);
            case FOOTER:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.layout_footer_load_more, viewGroup, false);
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
                AdsInfoResponseEnt house = houses.get(position - 3);
                HouseViewHolder holder = (HouseViewHolder) h;

                ImageLoader.getInstance().displayImage(house.imageUrl, holder.ivPhoto);
                int price = Util.convertMinMaxPriceToPrice(house.mminMaxPrice);
                holder.tvPrice.setText(price <= Constants.HOUSE_PRICE_LIMIT ? Util
                        .formatPriceNumber(price) + "€" : "€");
                holder.tvTitle.setText(house.title);

                String description = String.format(
                        holder.tvDescription.getContext().getString(
                                R.string.list_item_description_template),
                        house.roomNumber,
                        house.surface, house.distance);
                holder.tvDescription.setText(Html.fromHtml(description));

                holder.containerView.setBackgroundResource(selectedItems.contains(house.id)
                        ? R.drawable.shape_grid_item_pressed_bg : R.drawable.selector_grid_item_bg);
                break;
            case FOOTER:
                if (position == houses.size()) {
                    h.itemView.setVisibility(View.GONE);
                } else {
                    h.itemView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (onItemClickedListener != null) {
            RecyclerView parent = (RecyclerView) v.getParent();
            int position = parent.getChildAdapterPosition(v) - 3;

            AdsInfoResponseEnt adsInfoResponseEnt = houses.get(position);
            selectedItems.add(adsInfoResponseEnt.id);
            notifyDataSetChanged();

            onItemClickedListener.onItemClicked(parent, v, position, v.getId(), adsInfoResponseEnt);
        }
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup containerView;
        public ImageView ivPhoto;
        public TextView tvPrice;
        public TextView tvTitle;
        public TextView tvDescription;

        public HouseViewHolder(View itemView) {
            super(itemView);
            containerView = (ViewGroup) itemView.findViewById(R.id.grid_item_container);
            ivPhoto = (ImageView) itemView.findViewById(R.id.grid_item_ivPhoto);
            tvPrice = (TextView) itemView.findViewById(R.id.grid_item_tvPrice);
            tvTitle = (TextView) itemView.findViewById(R.id.grid_item_tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.grid_item_tvDescription);
        }
    }

}
