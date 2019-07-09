package com.example.administer.lookphotosapp.entity;

import android.support.annotation.DrawableRes;

/**
 * Created by XHD on 2019/07/03
 */
public class GalleryPhotoData {
    public Object photoSource;

    public GalleryPhotoData(@DrawableRes int drawableRes) {
        this.photoSource = drawableRes;
    }

    public GalleryPhotoData(String path) {
        this.photoSource = path;
    }
}
