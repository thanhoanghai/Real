
package com.synova.realestate.adapters;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.synova.realestate.R;

public class RadioButtonAdapter extends RecyclerView.Adapter<RadioButtonAdapter.RadioButtonHolder>
        implements View.OnClickListener {

    private RadioButton mSelectedRB;
    private int mSelectedPosition = 0;

    private List<String> items = new ArrayList<>();

    private RecyclerView recyclerView;

    public RadioButtonAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setData(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void selectItem(int position){
        this.mSelectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RadioButtonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_radio_button_item, parent, false);
        return new RadioButtonHolder(view);
    }

    @Override
    public void onBindViewHolder(RadioButtonHolder holder, final int position) {
        if (position == 0) {
            holder.itemView.setVisibility(View.GONE);
            return;
        }

        holder.itemView.setVisibility(View.VISIBLE);

        String item = items.get(position);

        holder.radioButton.setText(item);
        holder.radioButton.setOnClickListener(this);

        if (mSelectedPosition != position) {
            holder.radioButton.setChecked(false);
        } else {
            holder.radioButton.setChecked(true);
            if (holder.radioButton != mSelectedRB) {
                mSelectedRB = holder.radioButton;
            }
        }
    }

    @Override
    public void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        if ((position != mSelectedPosition && mSelectedRB != null)) {
            mSelectedRB.setChecked(false);
        }

        mSelectedPosition = position;
        mSelectedRB = (RadioButton) v;
    }

    class RadioButtonHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public RadioButtonHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
        }
    }

}
