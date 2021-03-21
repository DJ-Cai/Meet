package net.dongjian.meet.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.dongjian.framwork.base.BaseActivity;
import com.dongjian.framwork.utils.LogUtils;

import net.dongjian.meet.R;


public class TestActivity extends BaseActivity implements View.OnClickListener {

    private Button button_1;
    private Button button_2;
    private Button button_3;
    private Button button_4;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zeng:
//                MyData myData = new MyData();
//                myData.setName("张三");
//                myData.setSex(0);
//                myData.save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        if (e == null) {
//                            LogUtils.e("新增成功");
//                        }else{
//                            LogUtils.e(e.toString());
//                        }
//                    }
//                });
                break;
            case R.id.shan:
//                SimulationData.testData();

                break;
            case R.id.gai:
                LogUtils.e("修改成功");

                break;
            case R.id.cha:
                LogUtils.e("插入成功");

                break;

        }
    }

    private void initView() {
        button_1 = findViewById(R.id.zeng);
        button_2 = findViewById(R.id.shan);
        button_3 = findViewById(R.id.gai);
        button_4 = findViewById(R.id.cha);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
    }
}
