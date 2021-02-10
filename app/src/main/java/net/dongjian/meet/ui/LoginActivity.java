package net.dongjian.meet.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dongjian.framwork.base.BaseUIActivity;

import net.dongjian.meet.R;

/**
 * 登录页
 */
public class LoginActivity extends BaseUIActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
