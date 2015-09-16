
package com.synova.realestate.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;
import com.synova.realestate.base.Constants;
import com.synova.realestate.base.OnRecyclerViewItemClickedListener;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/17/15.
 */
public class SellerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements View.OnClickListener {

    private List<Publisher> publishers = new ArrayList<>();

    private OnRecyclerViewItemClickedListener<Publisher> onRecyclerViewItemClickedListener;

    public void setOnRecyclerViewItemClickedListener(
            OnRecyclerViewItemClickedListener<Publisher> onRecyclerViewItemClickedListener) {
        this.onRecyclerViewItemClickedListener = onRecyclerViewItemClickedListener;
    }

    public void setItems(List<Publisher> publishers) {
        this.publishers = publishers;
        notifyDataSetChanged();
    }

    public void addItems(List<Publisher> publishers) {
        int start = publishers.size();
        this.publishers.addAll(publishers);
        notifyItemRangeInserted(start, publishers.size() - 1);
    }

    // @Override
    // public int getItemCount() {
    // return publishers.size() + 1;
    // }

    @Override
    public int getItemCount() {
        return publishers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == publishers.size() ? Constants.RecyclerViewType.FOOTER.ordinal()
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
                Publisher publisher = publishers.get(position);
                SellerItemViewHolder holder = (SellerItemViewHolder) h;

                if (!Util.isNullOrEmpty(publisher.logoUrl)){
                    holder.ivThumbnail.setImageURI(Uri.parse(publisher.logoUrl));
                }

                holder.tvTitle.setText(publisher.name);
                holder.tvAnnonces.setText(publisher.nbAds + " annonces");
                // holder.tvWebsite.setText(publisher.website);
                holder.tvPhone.setText(publisher.tel);
                holder.tvMail.setText(publisher.mail);
                break;
            case FOOTER:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickedListener != null) {
            RecyclerView parent = (RecyclerView) v.getParent();
            int position = parent.getChildAdapterPosition(v);
            onRecyclerViewItemClickedListener.onItemClicked(parent, v, position, v.getId(),
                    publishers.get(position));
        }
    }

    static class SellerItemViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView ivThumbnail;
        public TextView tvTitle;
        public TextView tvAnnonces;
        public TextView tvWebsite;
        public TextView tvPhone;
        public TextView tvMail;

        public SellerItemViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (SimpleDraweeView) itemView.findViewById(R.id.seller_item_ivThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.seller_item_tvTitle);
            tvAnnonces = (TextView) itemView.findViewById(R.id.seller_item_tvAnnonces);
            tvWebsite = (TextView) itemView.findViewById(R.id.seller_item_tvWebsite);
            tvPhone = (TextView) itemView.findViewById(R.id.seller_item_tvPhone);
            tvMail = (TextView) itemView.findViewById(R.id.seller_item_tvMail);
        }
    }
}
