
package com.synova.realestate.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.synova.realestate.R;
import com.synova.realestate.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class DetailSlideShowAdapter extends RecyclingPagerAdapter {

    private List<String> photoUrls = new ArrayList<>();

    public void setData(List<String> photos) {
        this.photoUrls = photos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return photoUrls.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup container) {
        String photoUrl = photoUrls.get(position);
        SlideShowViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(container.getContext()).inflate(
                    R.layout.layout_detail_slideshow_item, container, false);

            holder = new SlideShowViewHolder();
            holder.ivPhoto = (SimpleDraweeView) view.findViewById(R.id.detail_slideShow_ivPhoto);

            view.setTag(holder);
        } else {
            holder = (SlideShowViewHolder) view.getTag();
        }

        if (!Util.isNullOrEmpty(photoUrl)) {
            holder.ivPhoto.setImageURI(Uri.parse(photoUrl));
        }

        return view;
    }

    private class SlideShowViewHolder {
        public SimpleDraweeView ivPhoto;
    }

}
