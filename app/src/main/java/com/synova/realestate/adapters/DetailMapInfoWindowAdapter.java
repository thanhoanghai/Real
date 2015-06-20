
package com.synova.realestate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.synova.realestate.R;

/**
 * Created by ducth on 6/20/15.
 */
public class DetailMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private View contentRootView;

    public DetailMapInfoWindowAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        InfoWindowViewHolder holder;
        if (contentRootView == null) {
            contentRootView = inflater.inflate(R.layout.layout_infowindow, null, false);

            holder = new InfoWindowViewHolder();
            holder.tvContent = (TextView) contentRootView.findViewById(R.id.infowindow_tvContent);

            contentRootView.setTag(holder);
        } else {
            holder = (InfoWindowViewHolder) contentRootView.getTag();
        }

        holder.tvContent.setText(marker.getTitle());

        return contentRootView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private class InfoWindowViewHolder {
        public TextView tvContent;
    }
}
