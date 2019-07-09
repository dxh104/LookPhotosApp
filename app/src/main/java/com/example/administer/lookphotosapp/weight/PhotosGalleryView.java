package com.example.administer.lookphotosapp.weight;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.administer.lookphotosapp.R;
import com.example.administer.lookphotosapp.adapter.GalleryPhotoAdapter;
import com.example.administer.lookphotosapp.entity.ImageViewInfo;
import com.example.administer.lookphotosapp.util.FileHelpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by XHD on 2019/07/03
 */
public class PhotosGalleryView extends RelativeLayout {

    private ViewPager viewpager;
    private TextView tvNum;//下标
    private TextView tvSave;//保存
    private List<GalleryPhotoView> list;//PhotoView集合
    private GalleryPhotoAdapter galleryPhotoAdapter;//照片画廊适配器
    private ImageView scaleImageView;//缩放图片控件
    View view;//填充布局
    final ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();//bitmap集合
    private int photosGalleryViewWidth;
    private int photosGalleryViewHeight;
    private int animDuration;
    private int currentPagePosition;
    private Context mContext;
    private int topTitleActionBarHeight;//顶部标题栏状态栏高度
    private int screenContentHeight;//界面内容高度
    private int screenHeight;//屏幕高度
    int[] locationScreen;//传入控件绝对坐标不受布局影响

    public PhotosGalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context, attrs);
    }


    /**
     * 初始化视图
     */
    private void initView(Context context, AttributeSet attrs) {
        view = View.inflate(mContext, R.layout.gallery_photos, this);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tvNum = (TextView) findViewById(R.id.tv_num);
        tvSave = (TextView) findViewById(R.id.tv_save);
        scaleImageView = (ImageView) findViewById(R.id.scaleImageView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PhotosGalleryView);
        int viewPagerBgColor = typedArray.getColor(R.styleable.PhotosGalleryView_PhotosGalleryViewBgColor, Color.parseColor("#000000"));
        int tvNumColor = typedArray.getColor(R.styleable.PhotosGalleryView_PhotosGalleryViewPositionTextColor, Color.parseColor("#ffffff"));
        float tvNumSize = typedArray.getDimensionPixelOffset(R.styleable.PhotosGalleryView_PhotosGalleryViewPositionTextSize, 30);
        boolean tvNumVisibility = typedArray.getBoolean(R.styleable.PhotosGalleryView_PhotosGalleryViewPositionVisibility, true);
        animDuration = typedArray.getInteger(R.styleable.PhotosGalleryView_PhotosGalleryViewAnimDuration, 500);
        int tvSaveColor = typedArray.getColor(R.styleable.PhotosGalleryView_PhotosGalleryViewSaveTextColor, Color.parseColor("#ffffff"));
        float tvSaveSize = typedArray.getDimensionPixelOffset(R.styleable.PhotosGalleryView_PhotosGalleryViewSaveTextSize, 20);
        boolean tvSaveVisibility = typedArray.getBoolean(R.styleable.PhotosGalleryView_PhotosGalleryViewSaveVisibility, true);

        viewpager.setBackgroundColor(viewPagerBgColor);
        tvNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvNumSize);
        tvNum.setTextColor(tvNumColor);
        tvNum.setVisibility(tvNumVisibility ? VISIBLE : INVISIBLE);
        tvSave.setTextSize(TypedValue.COMPLEX_UNIT_PX, tvSaveSize);
        tvSave.setTextColor(tvSaveColor);
        tvSave.setVisibility(tvSaveVisibility ? VISIBLE : INVISIBLE);

    }

    /**
     * 隐藏照片动画
     *
     * @param imageViewInfo 控件集合信息
     * @param position      指定控件下标
     */
    public void hidePhotoAnimator() {
        locationScreen = new int[2];
        imageViewInfo.getView().get(currentPagePosition).getLocationOnScreen(locationScreen);//存储当前控件绝对坐标（不受布局影响）
        tvNum.setVisibility(GONE);
        tvSave.setVisibility(GONE);
        galleryPhotoAdapter.getGalleryPhotoView(currentPagePosition).setImageBitmap(null);
        galleryPhotoAdapter.getGalleryPhotoView(currentPagePosition).setVisibility(GONE);//隐藏照片
        Glide.with(mContext)
                .asDrawable()
                .load(imageViewInfo.getPath().get(currentPagePosition).photoSource)
//                .placeholder(R.mipmap.ic_launcher)//加载过程中图片未显示时显示的本地图片
                .error(R.mipmap.bg_error)//加载异常时显示的图片
//                .fitCenter()//缩放图像测量出来等于或小于ImageView的边界范围,该图像将会完全显示
                .into(scaleImageView);
        scaleImageView.setVisibility(VISIBLE);

        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(animDuration);
        //透明度
        ObjectAnimator viewPagerAlpha = ObjectAnimator.ofFloat(viewpager, "alpha", 1, 0);
        animator.play(viewPagerAlpha);
        //透明度
        ObjectAnimator alpha = ObjectAnimator.ofFloat(scaleImageView, "alpha", 1, 0);
        //translationY Y轴平移 translationX  Y轴平移
        ObjectAnimator translationX = ObjectAnimator.ofFloat(scaleImageView, "translationX", 0, -(photosGalleryViewWidth / 2 - locationScreen[0] - imageViewInfo.getView().get(currentPagePosition).getWidth() / 2));
        ObjectAnimator translationY = ObjectAnimator.ofFloat(scaleImageView, "translationY", 0, -(photosGalleryViewHeight / 2 - locationScreen[1] - topTitleActionBarHeight - imageViewInfo.getView().get(currentPagePosition).getHeight() / 2));
        //缩放 至中心点
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(scaleImageView, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(scaleImageView, "scaleY", 1, 0);

        animator
                .play(alpha)
                .with(translationX)
                .with(translationY)
                .with(scaleX).with(scaleY);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onAnimationEnd(Animator animation) {
                scaleImageView.setBackground(null);
                scaleImageView.setVisibility(GONE);
                viewpager.setVisibility(GONE);
                PhotosGalleryView.this.setVisibility(GONE);
                recycle();//检查之前有没有bitmap没回收
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();

    }

    ImageViewInfo imageViewInfo;

    /**
     * 显示照片动画
     *
     * @param imageViewInfo 控件集合信息
     * @param position      指定控件下标
     */
    public void showPhotoAnimator(ImageViewInfo imageViewInfos, final int position, Activity activity) {
        imageViewInfo = imageViewInfos;
        tvSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//适配6.0权限
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(mContext, "请检查存储权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = null;
                        try {
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                file = Glide.with(mContext)
                                        .load(imageViewInfo.path.get(currentPagePosition).photoSource)
                                        .downloadOnly(0, 0)
                                        .get();
                                final String filePath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
//                                filePath=/storage/emulated/0/Android/data/com.example.administer.lookphotosapp/files/Pictures
                                File imgDir = new File(filePath);
                                if (!imgDir.exists()) {
                                    imgDir.mkdirs();
                                }
                                final File imgFile = new File(filePath + "/" + System.currentTimeMillis() + ".jpg");
                                if (!imgFile.exists()) {
                                    imgFile.createNewFile();
                                }
                                FileHelpUtil.copyFile(file, imgFile, new FileHelpUtil.OnCopyFileListenner() {
                                    @Override
                                    public void onSucceed() {
                                        //把文件插入到系统图库
                                        try {
                                            MediaStore.Images.Media.insertImage(mContext.getContentResolver(), imgFile.getAbsolutePath(), imgFile.getName(), null);
                                            //把图片保存后声明这个广播事件通知系统相册有新图片到来
                                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                            Uri uri = Uri.fromFile(imgFile);
                                            intent.setData(uri);
                                            mContext.sendBroadcast(intent);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        //解决在子线程中调用Toast的异常情况处理
                                        Looper.prepare();
                                        Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onError() {
                                        //解决在子线程中调用Toast的异常情况处理
                                        Looper.prepare();
                                        Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                });
                            } else {
                                Toast.makeText(mContext, "请检查SD卡是否可用", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        if (list != null) {
            list.clear();
        }
        list = new ArrayList<>();
        for (int i = 0; i < imageViewInfo.path.size(); i++) {
            //传入图片地址
            final GalleryPhotoView galleryPhotoView = new GalleryPhotoView(mContext, imageViewInfo.path.get(i));
            galleryPhotoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePhotoAnimator();
                }
            });
            list.add(galleryPhotoView);
        }
        galleryPhotoAdapter = new GalleryPhotoAdapter(list);
        viewpager.setAdapter(galleryPhotoAdapter);
        galleryPhotoAdapter.getGalleryPhotoView(position).setVisibility(INVISIBLE);//隐藏照片
        viewpager.setCurrentItem(position);//选中第position个页面
        currentPagePosition = position;//设置当前页面下标
        tvNum.setText((position) + 1 + "/" + list.size());//设置选择位置
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvNum.setText((position + 1) + "/" + list.size());//设置选择位置
                currentPagePosition = position;//设置当前页面下标
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        View viewParent = (View) view.getParent();
        photosGalleryViewWidth = viewParent.getWidth();//获取控件宽度
        photosGalleryViewHeight = viewParent.getHeight();//获取控件高度

        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        screenHeight = outMetrics.heightPixels;//屏幕高度(不包含虚拟按键)
        screenContentHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();//界面内容高度
        topTitleActionBarHeight = screenHeight - screenContentHeight;//顶部高度

        RequestOptions options = new RequestOptions();
        options.skipMemoryCache(true);//默认跳过内存缓存
        options.diskCacheStrategy(DiskCacheStrategy.ALL);//默认全部使用磁盘缓存
        options.priority(Priority.HIGH);//用priority()指定请求优先级
//        options.error(R.mipmap.bg_error);//设置错误符,默认


        Glide.with(mContext)
                .asBitmap()
                .apply(options)
                .load(imageViewInfo.path.get(position).photoSource)
                .error(R.mipmap.bg_error)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        locationScreen = new int[2];
                        imageViewInfo.getView().get(currentPagePosition).getLocationOnScreen(locationScreen);//存储传入控件绝对坐标（不受布局影响）
                        LayoutParams layoutParams = (LayoutParams) scaleImageView.getLayoutParams();
                        int imageWidth = resource.getWidth();//图片原始宽度
                        int imageHeight = resource.getHeight();//图片原始高度
                        scaleImageView.setLayoutParams(layoutParams);
                        int imageViewWidth = imageViewInfo.getView().get(position).getWidth();//控件原始宽度
                        int imageViewHeight = imageViewInfo.getView().get(position).getHeight();//控件原始高度
                        layoutParams.width = imageViewWidth;
                        layoutParams.height = imageViewWidth * imageHeight / imageWidth;
                        layoutParams.leftMargin = locationScreen[0];
                        layoutParams.topMargin = locationScreen[1] - topTitleActionBarHeight;

                        float scaleRatio = 0;//放大倍数
                        scaleRatio = (float) photosGalleryViewWidth / imageViewWidth;//放大至全宽度
                        scaleImageView.setVisibility(VISIBLE);
                        scaleImageView.setImageBitmap(resource);
                        startShowAnimator(position, photosGalleryViewWidth, photosGalleryViewHeight, scaleRatio, layoutParams);
                        bitmapArrayList.add(resource);//收集bitmap
                    }
                });


    }

    private void startShowAnimator(final int position, final int photosGalleryViewWidth, final int photosGalleryViewHeight, final float scaleRatio, final LayoutParams layoutParams) {
        recycle();//检查之前有没有bitmap没回收
        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(animDuration);
        //透明度
        ObjectAnimator viewPagerAlpha = ObjectAnimator.ofFloat(viewpager, "alpha", 0, 1);
        viewpager.setVisibility(VISIBLE);
        animator.play(viewPagerAlpha);
        //透明度
        ObjectAnimator alpha = ObjectAnimator.ofFloat(scaleImageView, "alpha", 0, 1);
        //translationY Y轴平移 translationX  Y轴平移
        ObjectAnimator translationX = ObjectAnimator.ofFloat(scaleImageView, "translationX", 0, photosGalleryViewWidth / 2 - locationScreen[0] - layoutParams.width / 2);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(scaleImageView, "translationY", 0, photosGalleryViewHeight / 2 - locationScreen[1] + topTitleActionBarHeight - layoutParams.height / 2);
        //缩放
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(scaleImageView, "scaleX", 1, scaleRatio);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(scaleImageView, "scaleY", 1, scaleRatio);

        animator
                .play(alpha)
                .with(translationX)
                .with(translationY)
                .with(scaleX).with(scaleY);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onAnimationEnd(Animator animation) {
                tvNum.setVisibility(VISIBLE);
                tvSave.setVisibility(VISIBLE);
                galleryPhotoAdapter.getGalleryPhotoView(position).setVisibility(VISIBLE);//显示照片
                LayoutParams scaleImageViewLayoutParams = (LayoutParams) scaleImageView.getLayoutParams();
                scaleImageViewLayoutParams.width = (int) (scaleImageView.getLayoutParams().width * scaleRatio);
                scaleImageViewLayoutParams.height = (int) (scaleImageView.getLayoutParams().height * scaleRatio);
                scaleImageViewLayoutParams.topMargin = photosGalleryViewHeight / 2 - scaleImageViewLayoutParams.height / 2;
                scaleImageViewLayoutParams.leftMargin = photosGalleryViewWidth / 2 - scaleImageViewLayoutParams.width / 2;
                scaleImageView.setBackground(null);
                scaleImageView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return bitmapArrayList;
    }

    //bitmap回收
    public void recycle() {
        ArrayList<Bitmap> arrayList = getBitmapArrayList();
        if (arrayList != null && arrayList.size() != 0) {
            for (Bitmap bitmap : arrayList) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            arrayList.clear();
        }
    }

}
