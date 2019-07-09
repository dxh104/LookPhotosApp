package com.example.administer.lookphotosapp.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.administer.lookphotosapp.weight.GalleryPhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XHD on 2019/07/03
 */
public class GalleryPhotoAdapter extends PagerAdapter {

    private List<GalleryPhotoView> viewList = new ArrayList<>();
    private List<Integer> positions = new ArrayList<>();

    public GalleryPhotoView getGalleryPhotoView(int position) {
        return viewList.get(position);
    }

    public GalleryPhotoAdapter(List<GalleryPhotoView> list) {
        if (list != null) {
            viewList.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return this.viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (!positions.contains(position)) {
            positions.add(position);
            //创建一个新的item
            viewList.get(position).startGlide();//加载图片
        }
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}
