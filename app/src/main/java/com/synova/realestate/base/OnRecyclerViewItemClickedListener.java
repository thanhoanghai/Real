
package com.synova.realestate.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ducth on 6/17/15.
 */
public interface OnRecyclerViewItemClickedListener<T> {
    public void onItemClicked(RecyclerView recyclerView, View view, int position, long id, T data);
}
