package net.dongjian.meet;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.dongjian.framwork.base.BaseUIActivity;
import com.dongjian.framwork.manager.MediaPlayerManager;
import com.dongjian.framwork.utils.LogUtils;
import com.dongjian.framwork.utils.TimeUtils;

public class MainActivity extends BaseUIActivity {

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.i("Hello World");
        LogUtils.e("Hello World Twice");

        MediaPlayerManager mediaPlayerManager = new MediaPlayerManager();
        AssetFileDescriptor fileDescriptor = getResources().openRawResourceFd(R.raw.test);
        mediaPlayerManager.startPlay(fileDescriptor);

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