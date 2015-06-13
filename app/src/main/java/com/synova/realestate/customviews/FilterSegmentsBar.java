
package com.synova.realestate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.synova.realestate.R;

/**
 * Created by ducth on 6/13/15.
 */
public class FilterSegmentsBar extends FrameLayout implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup groupSegments;
    private RadioButton segmentDistance;
    private RadioButton segmentPrice;
    private RadioButton segmentDate;

    private OnSegmentSelectedListener onSegmentSelectedListener;

    public void setOnSegmentSelectedListener(OnSegmentSelectedListener listener) {
        this.onSegmentSelectedListener = listener;
    }

    public FilterSegmentsBar(Context context) {
        super(context);
        init();
    }

    public FilterSegmentsBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterSegmentsBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_filter_bar, this, true);

        groupSegments = (RadioGroup) findViewById(R.id.groupSegments);
        segmentDistance = (RadioButton) findViewById(R.id.segment_distance);
        segmentPrice = (RadioButton) findViewById(R.id.segment_price);
        segmentDate = (RadioButton) findViewById(R.id.segment_date);

        groupSegments.setOnCheckedChangeListener(this);
    }

    public void selectSegment(int position) {
        groupSegments.check(-1);
        switch (position) {
            case 1:
                groupSegments.check(segmentDistance.getId());
                break;
            case 2:
                groupSegments.check(segmentPrice.getId());
                break;
            case 3:
                groupSegments.check(segmentDate.getId());
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (onSegmentSelectedListener != null) {
            switch (checkedId) {
                case R.id.segment_distance:
                    onSegmentSelectedListener.onSegmentSelected(0, checkedId);
                    break;
                case R.id.segment_price:
                    onSegmentSelectedListener.onSegmentSelected(1, checkedId);
                    break;
                case R.id.segment_date:
                    onSegmentSelectedListener.onSegmentSelected(2, checkedId);
                    break;
            }
        }
    }

    public interface OnSegmentSelectedListener {
        public void onSegmentSelected(int position, int segmentId);
    }
}
