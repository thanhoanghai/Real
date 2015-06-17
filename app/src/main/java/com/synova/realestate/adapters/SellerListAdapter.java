
package com.synova.realestate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;
import com.synova.realestate.models.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/17/15.
 */
public class SellerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private List<Seller> sellers = new ArrayList<>();

    public void setItems(List<Seller> sellers) {
        this.sellers = sellers;
        notifyDataSetChanged();
    }

    public void addItems(List<Seller> sellers) {
        int start = sellers.size();
        this.sellers.addAll(sellers);
        notifyItemRangeInserted(start, sellers.size() - 1);
    }

    @Override
    public int getItemCount() {
        return sellers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == sellers.size() ? Constants.RecyclerViewType.FOOTER.ordinal()
                : Constants.RecyclerViewType.ITEM.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        Constants.RecyclerViewType type = Constants.RecyclerViewType.values()[viewType];
        switch (type) {
            case HEADER:
                break;
            case ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_seller_list_item, parent, false);
                view.setOnClickListener(this);
                return new SellerItemViewHolder(view);
            case FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_footer_load_more, parent, false);
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
                Seller seller = sellers.get(position);
                SellerItemViewHolder holder = (SellerItemViewHolder) h;

                ImageLoader.getInstance().displayImage(seller.thumbnail, holder.ivThumbnail);
                holder.tvTitle.setText(seller.title);
                holder.tvAnnonces.setText(seller.annonces + " annonces");
                holder.tvWebsite.setText(seller.website);
                holder.tvPhone.setText(seller.phone);
                holder.tvMail.setText(seller.mail);
                break;
            case FOOTER:
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    static class SellerItemViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivThumbnail;
        public TextView tvTitle;
        public TextView tvAnnonces;
        public TextView tvWebsite;
        public TextView tvPhone;
        public TextView tvMail;

        public SellerItemViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.seller_item_ivThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.seller_item_tvTitle);
            tvAnnonces = (TextView) itemView.findViewById(R.id.seller_item_tvAnnonces);
            tvWebsite = (TextView) itemView.findViewById(R.id.seller_item_tvWebsite);
            tvPhone = (TextView) itemView.findViewById(R.id.seller_item_tvPhone);
            tvMail = (TextView) itemView.findViewById(R.id.seller_item_tvMail);
        }
    }
}
