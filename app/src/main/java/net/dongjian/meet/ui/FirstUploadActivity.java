package net.dongjian.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseBackActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.helper.FileHelper;
import com.dongjian.framwork.manager.DialogManager;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.view.DialogView;
import com.dongjian.framwork.view.LoadingView;

import net.dongjian.meet.R;

import java.io.File;

import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * 第一次登录后，创建头像和用户名的地方
 */
public class FirstUploadActivity extends BaseBackActivity implements View.OnClickListener {

    /**
     * 跳转到这个Activitiy来，
     * 之所以在这个类这里写，是为了方便看这个Activity的功能
     * @param mActivity
     * @param requestCode
     */
    public static void startActivity(Activity mActivity , int requestCode){
        Intent intent = new Intent(mActivity,FirstUploadActivity.class);
        mActivity.startActivityForResult(intent,requestCode);
    }

    //圆形头像
    private CircleImageView iv_photo;
    private EditText et_nickname;
    private Button btn_upload;

    //弹出提示框--让用户选择三种方式上传头像的提示框
    DialogView mPhotoSelectView;

    //监听用户选择上传头像的三种方式的控件
    private TextView tv_camera;
    private TextView tv_ablum;
    private TextView tv_cancel;

    //记录用户上传上来的头像文件
    private File uploadFile;
    //做个loadingView，在上传的时候优化一下用户体验
    private LoadingView mLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_upload);

        initView();

    }

    private void initView() {
        initPhotoView();

        iv_photo = (CircleImageView) findViewById(R.id.iv_photo);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        btn_upload = (Button) findViewById(R.id.btn_upload);

        iv_photo.setOnClickListener(this);
        btn_upload.setOnClickListener(this);

        //默认不可点击，需要满足设置好了头像和昵称以后才可以点击
        btn_upload.setEnabled(false);

        //所以需要监听输入框即et_nickname
        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入框内容长度大于0，则证明输入了内容
                if(s.length() > 0){
                    //文件部分不为空，则证明创建了头像
                    if(uploadFile != null){
                        btn_upload.setEnabled(true);
                    }else{
                        btn_upload.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 初始化用户选择上传头像的方式的选择框
     */
    private void initPhotoView() {

        mLoadingView = new LoadingView(this);
        mLoadingView.setLoadingText("正在上传头像...");

        mPhotoSelectView = DialogManager.getInstance().initView(this,R.layout.dialog_select_photo, Gravity.BOTTOM);

        tv_camera = (TextView) mPhotoSelectView.findViewById(R.id.tv_camera);
        tv_camera.setOnClickListener(this);
        tv_ablum = (TextView) mPhotoSelectView.findViewById(R.id.tv_ablum);
        tv_ablum.setOnClickListener(this);
        tv_cancel = (TextView) mPhotoSelectView.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_photo:
                //点击了图片后应该显示提示框然后让用户选择自己传入的头像的方式
                DialogManager.getInstance().show(mPhotoSelectView);
                break;
            case R.id.btn_upload:
                uploadPotoAndNickname();
                break;

                //下面三个无论点击哪个，都需要将mPhotoSelectView这个提示框给隐藏起来
            case R.id.tv_camera:
                DialogManager.getInstance().hide(mPhotoSelectView);
                //跳转到相机
                FileHelper.getInstance().toCamera(this);
                break;
            case R.id.tv_ablum:
                DialogManager.getInstance().hide(mPhotoSelectView);
                //跳转到相册
                FileHelper.getInstance().toAlbum(this);
                break;
            case R.id.tv_cancel:
                DialogManager.getInstance().hide(mPhotoSelectView);
                break;
        }
    }


    /**
     * 对于各个点击事件的回调响应
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        //成功回调
        if(resultCode == Activity.RESULT_OK){
            //用户选择通过相机上传照片
            if(requestCode == FileHelper.CAMEAR_REQUEST_CODE){
                uploadFile = FileHelper.getInstance().getTempFile();
                //压缩
                FileHelper.getInstance().startPhotoZoom(this, uploadFile);
            }else if(requestCode == FileHelper.ALBUM_REQUEST_CODE){
                //从相册选取
                //拿到选择的图片的地址，等下在转化为真实地址
                Uri uri = data.getData();
                if(uri != null){
                    //转化为真实地址
                    String path = FileHelper.getInstance().getRealPathFromURI(this,uri);
                    if(!TextUtils.isEmpty(path)){
                        uploadFile = new File(path);
                        //压缩上传的头像图片
                        FileHelper.getInstance().startPhotoZoom(this, uploadFile);
                    }
                }
            } else if (requestCode == FileHelper.CAMERA_CROP_RESULT) {
                //压缩后，让uploadFile指向压缩文件
                LogUtils.i("CAMERA_CROP_RESULT");
                uploadFile = new File(FileHelper.getInstance().getCropPath());
                LogUtils.i("uploadPhotoFile:" + uploadFile.getPath());
            }

            //设置头像
            if(uploadFile != null){
                Bitmap mBitmap = BitmapFactory.decodeFile(uploadFile.getPath());
                iv_photo.setImageBitmap(mBitmap);

                //判断当前的输入框是否填入昵称，填入之后才可以执行按钮的点击操作
                String nickName = et_nickname.getText().toString().trim();
                btn_upload.setEnabled(!TextUtils.isEmpty(nickName));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void uploadPotoAndNickname() {
        //获取昵称(头像文件是全局变量，可以直接使用)
        String nickname = et_nickname.getText().toString().trim();
        //这里就是上传，为了优化用户体验，所以加个LoadingView
        mLoadingView.show();

        BmobManager.getmInstance().uploadFirstPhotoAndNickname(uploadFile, nickname, new BmobManager.OnUploadPhotoListener() {
            @Override
            public void OnUpdateDone() {
                mLoadingView.hide();
                //返回成功，让上面的MainActivity操作去
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void OnUpdateFail(BmobException e) {
                mLoadingView.hide();
                Toast.makeText(FirstUploadActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
