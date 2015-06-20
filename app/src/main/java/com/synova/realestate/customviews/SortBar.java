
package com.synova.realestate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.synova.realestate.R;

/**
 * Created by ducth on 6/13/15.
 */
public class SortBar extends FrameLayout implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private ViewGroup groupDistance;
    private ViewGroup groupPrice;
    private ViewGroup groupDate;

    private CheckBox segmentDistance;
    private CheckBox segmentPrice;
    private CheckBox segmentDate;

    private int selectedTextColor;
    private int unselectedTextColor;

    private OnSortBarItemSelectedListener onSortBarItemSelectedListener;

    public SortBar(Context context) {
        super(context);
        init();
    }

    public SortBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SortBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        selectedTextColor = getResources().getColor(R.color.cyan);
        unselectedTextColor = getResources().getColor(R.color.text_gray);

        LayoutInflater.from(getContext()).inflate(R.layout.layout_filter_bar, this, true);

        groupDistance = (ViewGroup) findViewById(R.id.group_distance);
        groupPrice = (ViewGroup) findViewById(R.id.group_price);
        groupDate = (ViewGroup) findViewById(R.id.group_date);

        groupDistance.setOnClickListener(this);
        groupPrice.setOnClickListener(this);
        groupDate.setOnClickListener(this);

        segmentDistance = (CheckBox) findViewById(R.id.segment_distance);
        segmentPrice = (CheckBox) findViewById(R.id.segment_price);
        segmentDate = (CheckBox) findViewById(R.id.segment_date);

        segmentDistance.setOnCheckedChangeListener(this);
        segmentPrice.setOnCheckedChangeListener(this);
        segmentDate.setOnCheckedChangeListener(this);

        selectItem(0);
    }

    public void setOnSortBarItemSelectedListener(OnSortBarItemSelectedListener listener) {
        this.onSortBarItemSelectedListener = listener;
    }

    public void selectItem(int position) {
        switch (position) {
            case 0:
                segmentDistance.setChecked(true);
                break;
            case 1:
                segmentPrice.setChecked(true);
                break;
            case 2:
                segmentDate.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_distance:
                segmentDistance.toggle();
                break;
            case R.id.group_price:
                segmentPrice.toggle();
                break;
            case R.id.group_date:
                segmentDate.toggle();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int checkedId = buttonView.getId();
        switch (checkedId) {
            case R.id.segment_distance:
                changeSelectedItemUI(segmentDistance, isChecked);
                changeUnselectedItemUI(segmentPrice);
                changeUnselectedItemUI(segmentDate);

                if (onSortBarItemSelectedListener != null) {
                    onSortBarItemSelectedListener.onSortBarItemSelected(0, isChecked, checkedId);
                }
                break;
            case R.id.segment_price:
                changeUnselectedItemUI(segmentDistance);
                changeSelectedItemUI(segmentPrice, isChecked);
                changeUnselectedItemUI(segmentDate);

                if (onSortBarItemSelectedListener != null) {
                    onSortBarItemSelectedListener.onSortBarItemSelected(1, isChecked, checkedId);
                }
                break;
            case R.id.segment_date:
                changeUnselectedItemUI(segmentDistance);
                changeUnselectedItemUI(segmentPrice);
                changeSelectedItemUI(segmentDate, isChecked);

                if (onSortBarItemSelectedListener != null) {
                    onSortBarItemSelectedListener.onSortBarItemSelected(2, isChecked, checkedId);
                }
                break;
        }
    }

    private void changeSelectedItemUI(CompoundButton button, boolean isChecked) {
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, isChecked ? R.drawable.ico_sort_up
                : R.drawable.ico_sort_down, 0);
        button.setTextColor(selectedTextColor);
    }

    private void changeUnselectedItemUI(CompoundButton button) {
        setCheckStateWithoutListener(button, false);
        button.setTextColor(unselectedTextColor);
        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void setCheckStateWithoutListener(CompoundButton button, boolean isChecked) {
        button.setOnCheckedChangeListener(null);
        button.setChecked(isChecked);
        button.setOnCheckedChangeListener(this);
    }

    public interface OnSortBarItemSelectedListener {
        public void onSortBarItemSelected(int position, boolean isSortAsc, int segmentId);
    }
}
