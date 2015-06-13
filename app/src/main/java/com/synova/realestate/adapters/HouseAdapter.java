
package com.synova.realestate.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;
import com.synova.realestate.models.House;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/13/15.
 */
public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.HouseViewHolder> implements
        View.OnClickListener {

    private List<House> houses = new ArrayList<>();

    private AdapterView.OnItemClickListener onItemClickedListener;

    public void setOnItemClickedListener(AdapterView.OnItemClickListener onItemClickedListener) {
        this.onItemClickedListener = onItemClickedListener;
    }

    public void setItems(List<House> houses) {
        this.houses = houses;
        notifyDataSetChanged();
    }

    public void addItem(House house) {
        houses.add(house);
        notifyItemInserted(houses.size() - 1);
    }

    public void addItems(List<House> houses) {
        int start = this.houses.size();
        this.houses.addAll(houses);
        notifyItemRangeInserted(start, houses.size() - 1);
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    @Override
    public HouseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.layout_square_grid_item, viewGroup, false);
        view.setOnClickListener(this);
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HouseViewHolder holder, int i) {
        House house = houses.get(i);

        ImageLoader.getInstance().displayImage(house.photo, holder.ivPhoto);
        holder.tvPrice
                .setText(house.price <= Constants.HOUSE_PRICE_LIMIT ? house.price + "€" : "€");
        holder.tvTitle.setText(house.title);

        String description = house.pieces + " piece(s) | " + house.surface + " m2 | "
                + house.distance + " m";
        Spannable spannable = new SpannableString(description);
        int start = description.indexOf("m2") + 1;
        spannable
                .setSpan(new SuperscriptSpan(), start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvDescription.setText(spannable);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickedListener != null) {
            AdapterView parent = (AdapterView) v.getParent();
            int position = parent.getPositionForView(v);
            onItemClickedListener.onItemClick((AdapterView) v.getParent(), v, position, v.getId());
        }
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPhoto;
        public TextView tvPrice;
        public TextView tvTitle;
        public TextView tvDescription;

        public HouseViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.grid_item_ivPhoto);
            tvPrice = (TextView) itemView.findViewById(R.id.grid_item_tvPrice);
            tvTitle = (TextView) itemView.findViewById(R.id.grid_item_tvTitle);
            tvDescription = (TextView) itemView.findViewById(R.id.grid_item_tvDescription);
        }
    }
}
