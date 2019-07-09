package com.example.administer.lookphotosapp.weight;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.example.administer.lookphotosapp.R;
import com.example.administer.lookphotosapp.entity.GalleryPhotoData;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by XHD on 2019/07/03
 */
public class GalleryPhotoView extends PhotoView {

    private GalleryPhotoData galleryPhotoData;

    public GalleryPhotoView(Context context, GalleryPhotoData galleryPhotoData) {
        super(context);
        this.galleryPhotoData = galleryPhotoData;
    }

    public void startGlide() {
        Glide.with(getContext())
                .asDrawable()
                .load(galleryPhotoData.photoSource)
//                .placeholder(R.mipmap.ic_launcher)//加载过程中图片未显示时显示的本地图片
                .error(R.mipmap.bg_error)
//                .fitCenter()//缩放图像测量出来等于或小于ImageView的边界范围,该图像将会完全显示
                .into(this);
    }


}
