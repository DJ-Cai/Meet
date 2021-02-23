package com.dongjian.framwork.manager;

import com.dongjian.framwork.cloud.CloudManager;
import com.dongjian.framwork.utils.SHA1;

import org.apache.http.client.HttpClient;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpManager {



    private static volatile HttpManager httpManager = null;
    private OkHttpClient mOkHttpClient;
    private HttpManager(){
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpManager getInstance(){
        if(httpManager == null){
            synchronized (HttpManager.class){
                if(httpManager == null){
                    httpManager = new HttpManager();
                }
            }
        }
        return httpManager;
    }


    /**
     * 请求融云token
     * @param map
     */
    public String postCloudToken(HashMap<String,String> map){
        //参数:时间戳，随机数
        String TimeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String Nonce = String.valueOf(Math.floor(Math.random() * 10000));
        String Signature = SHA1.sha1( CloudManager.SECRET + Nonce + TimeStamp);
        //将map中的参数填充到FormBody里
        FormBody.Builder builder = new FormBody.Builder();
        for(String key : map.keySet()){
            builder.add(key,map.get(key));
        }
        //用FormBody来构建requestBody
        RequestBody requestBody = builder.build();
        //添加签名规则
        Request request = new Request.Builder()
                .url(CloudManager.URL)
                .addHeader("Timestamp",TimeStamp)
                .addHeader("App-Key",CloudManager.KEY)
                .addHeader("Nonce",Nonce)
                .addHeader("Signature",Signature)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        try {
            //okHttp去发起请求
            return mOkHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
