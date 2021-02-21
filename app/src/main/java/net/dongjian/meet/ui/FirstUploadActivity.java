package net.dongjian.meet.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseBackActivity;
import com.dongjian.framwork.manager.DialogManager;

import net.dongjian.meet.R;

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
        mActivity.startActivity(intent);
    }

    private CircleImageView iv_photo;
    private EditText et_nickname;
    private Button btn_upload;

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
    }

    private void initPhotoView() {
        DialogManager.getInstance().initView(this,R.layout.dialo)
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_photo:
                //显示提示框
                break;
            case R.id.btn_upload:
                break;
        }

    }
}
