
package com.synova.realestate.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

public class ReclickableTabHost extends TabHost {

    public interface OnCurrentTabClickListener {
        public void onCurrentTabClick(int tabIndex);
    }

    private OnCurrentTabClickListener listener;

    public void setOnCurrentTabClickListener(OnCurrentTabClickListener listener){
        this.listener = listener;
    }

    public ReclickableTabHost(Context context) {
        super(context);
    }

    public ReclickableTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentTab(int index) {
        if (index == getCurrentTab()) {
            if (listener != null){
                listener.onCurrentTabClick(index);
            }
        } else {
            super.setCurrentTab(index);
        }
    }
}
