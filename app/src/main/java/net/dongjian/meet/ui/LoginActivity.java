package net.dongjian.meet.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.bmob.BmobManager;
import com.dongjian.framwork.bmob.IMUser;
import com.dongjian.framwork.entity.Constants;
import com.dongjian.framwork.utils.SpUtils;

import net.dongjian.meet.MainActivity;
import net.dongjian.meet.R;

import org.w3c.dom.Text;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 登录页
 * 1、点击发送按钮，会弹出图片验证码，进行身份验证
 * 2、发送验证码，同时按钮变成不可点击，按钮开始倒计时，倒计时结束后 按钮重置(重置这一步有点问题)
 * 3、通过手机号码和验证码进行登录
 * 4、登录成功后获取本地对象
 */
public class LoginActivity extends BaseUIActivity implements View.OnClickListener {


    private EditText et_phone;
    private EditText et_code;
    private Button btn_send_code;
    private Button btn_login;

    private static final int H_TIME = 1001;
    //发送验证码后的“倒计时60s”
    private static int TIME = 3;
    //通过Handler不断刷新
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case H_TIME:
                    btn_send_code.setText(TIME + "s");
                    TIME--;
                    if (TIME >= 0) {
                        btn_send_code.setBackgroundResource(R.drawable.login_btn_bg_2);
                        mHandler.sendEmptyMessageDelayed(H_TIME, 1000);
                    } else {
                        //这里好像无法再次执行点击事件了
                        btn_send_code.setEnabled(true);
                        btn_send_code.setText(getString(R.string.text_login_send));
                        btn_send_code.setBackgroundResource(R.drawable.login_btn_bg);
                    }
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    /**
     * 初始化各类视图及点击事件
     */
    private void initView() {
        et_phone = findViewById(R.id.et_phone);
        et_code = findViewById(R.id.et_code);
        btn_send_code = findViewById(R.id.btn_send_code);
        btn_login = findViewById(R.id.btn_login);

        btn_send_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        //优化：读取之前留下的电话号码
        String phone = SpUtils.getInstance().getString(Constants.SP_PHONE,"");
        if(!TextUtils.isEmpty(phone)){
            et_phone.setText(phone);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击发送验证码的按钮：发送短信验证码
            case R.id.btn_send_code:
                sendSMS();
                break;
            //点击登录按钮
            case R.id.btn_login:
                login();
                break;

        }
    }


    private void login() {
        //1、获取并判断手机号码和验证码
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, getString(R.string.text_login_code_null), Toast.LENGTH_SHORT).show();
            return;
        }

        BmobManager.getmInstance().signOrLoginByMobilePhone(phone, code, new LogInListener<IMUser>() {
            @Override
            //登录成功或失败以后的回调
            public void done(IMUser imUser, BmobException e) {
                if (e == null) {
                    //登录成功
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    //（体验优化：保存此次登录用的手机号码，下次不用重新输入手机号码）
                    SpUtils.getInstance().putString(Constants.SP_PHONE,phone);
                    finish();
                } else {
                    //登录失败则弹出错误原因
                    Toast.makeText(LoginActivity.this, "ERROR:" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 发送短信验证码
     */
    private void sendSMS() {
        //1、获取手机号码
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, getString(R.string.text_login_phone_null), Toast.LENGTH_SHORT).show();
            return;
        }
        //2、请求短信验证码  且设置按钮和Toast告知用户
        BmobManager.getmInstance().requestSMS(phone, new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                //没有异常
                if (e == null) {
                    //点击发送验证后，此按钮变为不可点击，然后开始倒计时，倒计时结束后按钮重置
                    btn_send_code.setEnabled(false);
                    mHandler.sendEmptyMessage(H_TIME);
                    Toast.makeText(LoginActivity.this, R.string.text_login_sms_successful, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.text_login_sms_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
