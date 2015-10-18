
package com.synova.realestate.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;
import com.synova.realestate.models.Publisher;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/17/15.
 */
public class PublisherAdapter extends BaseAdapter {

    private List<Publisher> publishers = new ArrayList<>();

    @Override
    public int getCount() {
        return publishers != null ? publishers.size() : 0;
    }

    @Override
    public Publisher getItem(int position) {
        return publishers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Publisher publisher = publishers.get(position);
        SellerItemViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_tab_location_bottom_detail, parent, false);

            holder = new SellerItemViewHolder(view);
        } else {
            holder = (SellerItemViewHolder) view.getTag();
        }

        if (!Util.isNullOrEmpty(publisher.logoUrl)) {
            holder.ivThumbnail.setImageURI(Uri.parse(publisher.logoUrl));
        }

        holder.tvTitle.setText(publisher.name);
        holder.tvPrice.setText(publisher.amount + " €");
        holder.tvDescription.setText(publisher.address);
        return view;
    }

    public void setItems(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    public void addItems(List<Publisher> publishers) {
        this.publishers.addAll(publishers);
    }

    static class SellerItemViewHolder {

        public SimpleDraweeView ivThumbnail;
        public TextView tvTitle;
        public TextView tvPrice;
        public TextView tvDescription;

        public SellerItemViewHolder(View itemView) {
            ivThumbnail = (SimpleDraweeView) itemView
                    .findViewById(R.id.tab_location_bottom_ivThumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tab_location_bottom_tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tab_location_bottom_tvPrice);
            tvDescription = (TextView) itemView
                    .findViewById(R.id.tab_location_bottom_tvDescription);

            itemView.setTag(this);
        }
    }
}
