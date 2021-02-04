package net.dongjian.meet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
//        }else{
//            call();
//        }

    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode){
//            case 1 :
//                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    call();
//                }else{
//                    Toast.makeText(this,"你没许可噢",Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    private void call() {
//        LogUtils.i("Hello World");
//        LogUtils.e("Hello World Twice");
//    }
}