
package com.synova.realestate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class TouchableWrapperView extends FrameLayout {

    private ViewGroup scrollableView;

    public TouchableWrapperView(Context context) {
        super(context);
    }

    public TouchableWrapperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableWrapperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollableView(ViewGroup scrollableView) {
        this.scrollableView = scrollableView;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                scrollableView.requestDisallowInterceptTouchEvent(true);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

}
