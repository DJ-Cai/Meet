package net.dongjian.meet.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseActivity;
import com.dongjian.framwork.view.TouchPictureV;

import net.dongjian.meet.R;

public class  TestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView(){
        TouchPictureV touchPictureV = findViewById(R.id.TouchPictureV);
        touchPictureV.setViewResultListener(new TouchPictureV.OnViewResultListener() {
            @Override
            public void onResult() {
                Toast.makeText(TestActivity.this, "验证通过", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
