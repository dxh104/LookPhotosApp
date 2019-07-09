package com.example.administer.lookphotosapp.entity;

import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by XHD on 2019/07/03
 */
public class ImageViewInfo {

    //路径
    public List<GalleryPhotoData> path;
    // 在屏幕上的位置
    public List<View> view;

    public ImageViewInfo(List<GalleryPhotoData> path, List<View> view) {
        this.path = path;
        this.view = view;
    }

    public List<GalleryPhotoData> getPath() {
        return path;
    }

    public void setPath(List<GalleryPhotoData> path) {
        this.path = path;
    }

    public List<View> getView() {
        return view;
    }

    public void setView(List<View> view) {
        this.view = view;
    }
}
