package net.dongjian.meet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.github.chrisbanes.photoview.PhotoView;

import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.helper.GlideHelper;

import net.dongjian.meet.R;

import java.io.File;

/**
 * FileName: ImagePreviewActivity
 * Profile: 图片预览--就是点击图片以后放大显示原图的view
 */
public class ImagePreviewActivity extends BaseUIActivity implements View.OnClickListener {

    /**
     * 跳转
     * @param mContext
     * @param isUrl 类型：表明是URL 还是 file文件
     * @param url
     */
    public static void startActivity(Context mContext, boolean isUrl, String url) {
        Intent intent = new Intent(mContext, ImagePreviewActivity.class);
        intent.putExtra(Constants.INTENT_IMAGE_TYPE, isUrl);
        intent.putExtra(Constants.INTENT_IMAGE_URL, url);
        mContext.startActivity(intent);
    }

//    private PhotoView photo_view;
    private ImageView iv_back;
    private TextView tv_download;

    //图片地址
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        initView();
    }

    private void initView() {
//        photo_view = (PhotoView) findViewById(R.id.photo_view);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_download = (TextView)findViewById(R.id.tv_download);

        iv_back.setOnClickListener(this);
        tv_download.setOnClickListener(this);

        Intent intent = getIntent();
        boolean isUrl = intent.getBooleanExtra(Constants.INTENT_IMAGE_TYPE,false);
        url = intent.getStringExtra(Constants.INTENT_IMAGE_URL);

        //图片地址才下载，File代表本次已经存在
        tv_download.setVisibility(isUrl?View.VISIBLE:View.GONE);

//        if(isUrl){
//            GlideHelper.loadUrl(this,url,photo_view);
//        }else{
//            GlideHelper.loadFile(this,new File(url),photo_view);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_download:
                Toast.makeText(this, getString(R.string.text_iv_pre_downloading), Toast.LENGTH_SHORT).show();
                GlideHelper.loadUrlToBitmap(this, url, new GlideHelper.OnGlideBitmapResultListener() {
                    @Override
                    public void onResourceReady(Bitmap resource) {
                        if(resource != null){
                           // FileHelper.getInstance().saveBitmapToAlbum(ImagePreviewActivity.this,resource);
                        }else{
                            Toast.makeText(ImagePreviewActivity.this, getString(R.string.text_iv_pre_save_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
}
