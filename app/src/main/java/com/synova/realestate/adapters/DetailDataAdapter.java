
package com.synova.realestate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synova.realestate.R;
import com.synova.realestate.models.DetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ducth on 6/18/15.
 */
public class DetailDataAdapter extends RecyclerView.Adapter<DetailDataAdapter.DetailDataViewHolder> {

    private List<DetailData> items = new ArrayList<>();

    public void setData(List<DetailData> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public DetailDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_detail_data_list_item, parent, false);
        return new DetailDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DetailDataViewHolder holder, int position) {
        DetailData detailData = items.get(position);
        holder.tvTitle.setText(detailData.title);
        holder.tvQuantity.setText(detailData.quantity + "");
    }

    static class DetailDataViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvQuantity;

        public DetailDataViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.detail_data_item_tvTitle);
            tvQuantity = (TextView) itemView.findViewById(R.id.detail_data_item_tvQuantity);
        }
    }
}
