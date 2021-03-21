package com.dongjian.framwork.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import com.dongjian.framwork.utils.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 关于文件的帮助类
 * 1、跳转到相机
 * 2、跳转到相机
 * 3、裁剪头像大小
 */
public class FileHelper {
    //时间格式---用它来命名图片文件
    private SimpleDateFormat simpleDateFormat;
    //用于中转的文件
    private File tempFile = null;
    //用于拿到真实的URI
    private Uri imageUri;
    //裁剪文件
    private String cropPath;
    //裁剪结果
    public static final int CAMERA_CROP_RESULT = 1008;

    //相机的回调
    public static final int CAMEAR_REQUEST_CODE = 1004;
    public static final int ALBUM_REQUEST_CODE = 1005;

    private static volatile FileHelper mFileHelper = null;
    private FileHelper(){
        simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    }

    public static FileHelper getInstance(){
        if(mFileHelper == null){
            synchronized (FileHelper.class){
                if(mFileHelper == null){
                    mFileHelper = new FileHelper();
                }
            }
        }
        return mFileHelper;
    }

    public File getTempFile() {
        return tempFile;
    }
    public String getCropPath() {
        return cropPath;
    }

    /**
     * 1、跳转到相机
     * 用Intent
     */
    public void toCamera(Activity mActivity){
        //安卓自带的跳转--图像捕捉
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //用日期格式来命名文件
        String fileName = simpleDateFormat.format(new Date());
        //用中转文件在外部存储中存储
        tempFile = new File(Environment.getExternalStorageDirectory(),fileName + ".jpg");

        //拿到真实的URI: 兼容Anroid N --7.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            imageUri = Uri.fromFile(tempFile);
        }else{
            //如果版本超过7.0 需要FileProvider来处理
            imageUri = FileProvider.getUriForFile(mActivity,mActivity.getPackageName() + ".fileprovider" , tempFile);
            //还需要申请权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //和上一个activity（FirstUploadActivity）表明这里操作的是相机，放入文件，以及代表相机的CAMEAR_REQUEST_CODE
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        mActivity.startActivityForResult(intent,CAMEAR_REQUEST_CODE);
    }

    /**
     * 跳转到相册
     */
    public void toAlbum(Activity mActivity){
        //自带跳转-选择
        Intent intent = new Intent(Intent.ACTION_PICK);
        //显示所有的图片
        intent.setType("image/*");
        mActivity.startActivityForResult(intent,ALBUM_REQUEST_CODE);
    }


    /**
     * 根据URI去系统查询真实地址
     * @param mContext
     * @param uri
     */
    public String getRealPathFromURI(Context mContext , Uri uri){
        //查询条件proj
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader=new CursorLoader(mContext,uri,proj,null,null,null);
        //拿到游标，然后遍历得到下标
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        //游标回到开头
        cursor.moveToFirst();
        //重头遍历 返回真实地址
        return cursor.getString(index);
    }

    /**
     * 裁剪
     *
     * @param mActivity
     * @param file
     */
    public void startPhotoZoom(Activity mActivity, File file) {
        LogUtils.i("startPhotoZoom" + file.getPath());
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mActivity, "com.imooc.meet.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        if (uri == null) {
            return;
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", "true");
        //裁剪宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪图片的质量
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        //发送数据
        //intent.putExtra("return-data", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //单独存储裁剪文件，解决手机兼容性问题
        cropPath = Environment.getExternalStorageDirectory().getPath() + "/" + "meet.jpg";
        Uri mUriTempFile = Uri.parse("file://" + "/" + cropPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriTempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        mActivity.startActivityForResult(intent, CAMERA_CROP_RESULT);
    }
}
