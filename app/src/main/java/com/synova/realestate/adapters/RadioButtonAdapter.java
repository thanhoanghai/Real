
package com.synova.realestate.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.synova.realestate.R;
import com.synova.realestate.base.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RadioButtonAdapter extends RecyclerView.Adapter<RadioButtonAdapter.RadioButtonHolder>
        implements CompoundButton.OnCheckedChangeListener {

    private List<String> items = new ArrayList<>();
    private Set<Integer> selectedItems = new HashSet<>();

    private RecyclerView recyclerView;

    public RadioButtonAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setData(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void selectItem(int position) {
        if (position == 1) {
            checkAll();
        } else {
            selectedItems.remove(1);
            selectedItems.add(position);
            notifyDataSetChanged();
        }
    }

    public Set<Integer> getSelectedItems() {
        return selectedItems;
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

        holder.radioButton.setOnCheckedChangeListener(null);
        holder.radioButton.setChecked(selectedItems.contains(position));
        holder.radioButton.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        int position = recyclerView.getChildAdapterPosition(v);
        if (position == 1) {
            if (v.isChecked()) {
                checkAll();
            } else {
                uncheckAll();
            }
        } else {
            selectedItems.remove(1);
            if (v.isChecked()) {
                selectedItems.add(position);
                if (selectedItems.size() == Constants.PropertyType.values().length - 2) {
                    checkAll();
                }
            } else {
                selectedItems.remove(position);
            }
        }
        notifyDataSetChanged();
    }

    private void checkAll() {
        for (int i = 1; i < Constants.PropertyType.values().length; i++) {
            selectedItems.add(i);
        }
        notifyDataSetChanged();
    }

    private void uncheckAll() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    class RadioButtonHolder extends RecyclerView.ViewHolder {
        CheckBox radioButton;

        public RadioButtonHolder(View itemView) {
            super(itemView);
            radioButton = (CheckBox) itemView.findViewById(R.id.radio_button);
        }
    }

}
