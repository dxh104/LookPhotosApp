package com.example.administer.lookphotosapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.administer.lookphotosapp.entity.GalleryPhotoData;
import com.example.administer.lookphotosapp.entity.ImageViewInfo;
import com.example.administer.lookphotosapp.weight.PhotosGalleryView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    private PhotosGalleryView photosGalley;
    //路径
    public List<GalleryPhotoData> path;
    // 控件
    public List<View> viewList;
    private ImageView iv2;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//适配6.0权限
            if (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CHANGE_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.CHANGE_WIFI_STATE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.REQUEST_INSTALL_PACKAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE
                                , Manifest.permission.ACCESS_WIFI_STATE
                                , Manifest.permission.ACCESS_NETWORK_STATE
                                , Manifest.permission.CHANGE_NETWORK_STATE
                                , Manifest.permission.CHANGE_WIFI_STATE
                                , Manifest.permission.READ_EXTERNAL_STORAGE
                                , Manifest.permission.WRITE_EXTERNAL_STORAGE
                                , Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initView();
        RequestOptions options = new RequestOptions();
        options.skipMemoryCache(true);//默认跳过内存缓存
        options.diskCacheStrategy(DiskCacheStrategy.ALL);//默认全部使用磁盘缓存
        options.error(R.mipmap.bg_error);//错误位图
        Glide.with(this).asDrawable().apply(options).load("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3300305952,1328708913&fm=27&gp=0.jpg").into(iv);
        Glide.with(this).asDrawable().apply(options).load("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3300305952,1328708913&fm=27&gp=0.jpg").into(iv2);

        path = new ArrayList<>();
        viewList = new ArrayList<>();
        path.add(new GalleryPhotoData("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3300305952,1328708913&fm=27&gp=0.jpg"));
        path.add(new GalleryPhotoData("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3300305952,1328708913&fm=27&gp=0.jpg"));
        viewList.add(iv);
        viewList.add(iv2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photosGalley.setVisibility(View.VISIBLE);
                photosGalley.showPhotoAnimator(new ImageViewInfo(path, viewList), 0, MainActivity.this);
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photosGalley.setVisibility(View.VISIBLE);
                photosGalley.showPhotoAnimator(new ImageViewInfo(path, viewList), 1, MainActivity.this);
            }
        });
    }

    private void initView() {
        iv = (ImageView) findViewById(R.id.iv);
        photosGalley = (PhotosGalleryView) findViewById(R.id.photosGalley);
        iv2 = (ImageView) findViewById(R.id.iv2);
    }

    @Override
    public void onBackPressed() {
        if (photosGalley.getVisibility() == View.VISIBLE) {
            photosGalley.hidePhotoAnimator();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        photosGalley.recycle();//回收bitmap
        super.onDestroy();
    }
}
