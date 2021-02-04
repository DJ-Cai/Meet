package com.dongjian.framwork.manager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongjian.framwork.utils.LogUtils;

import java.io.IOException;

/**
 * 控制媒体播放
 */
public class MediaPlayerManager {

    //定义媒体的三种状态
    public static final int MEDIA_STATUS_PLAY = 0;
    public static final int MEDIA_STATUS_PAUSE = 1;
    public static final int MEDIA_STATUS_STOP = 2;

    //媒体当前状态--默认停止
    public static int MEDIA_STATUS = MEDIA_STATUS_STOP;

    private MediaPlayer mMediaPlayer;
    private OnMusicProgressListener musicProgressListener;

    public MediaPlayerManager(){
        mMediaPlayer = new MediaPlayer();
    }

    /**
     * @return 播放器目前是否处于播放状态
     */
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    /**
     * 是否循环播放
     * @param isLooping
     */
    public void setLooping(boolean isLooping){
        mMediaPlayer.setLooping(isLooping);
    }

    /**
     * 1、控制播放器开始播放
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startPlay(AssetFileDescriptor path){
        try {
            //1、重置一下播放器
            mMediaPlayer.reset();
            //2、设置资源   -- 加了个版本判断
            mMediaPlayer.setDataSource(path);
            //3、以同步的方式装载好流媒体文件
            mMediaPlayer.prepare();
            //4、开始播放
            mMediaPlayer.start();
            //5、设置媒体当前状态
            MEDIA_STATUS = MEDIA_STATUS_PLAY;
        } catch (IOException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 2、控制播放器暂停播放
     */
    public void pausePlay(){
        //1、判断播放器是否处于播放状态，处于才暂停
        if(isPlaying()){
            mMediaPlayer.pause();
            MEDIA_STATUS = MEDIA_STATUS_PAUSE;
        }
    }

    /**
     * 3、控制播放器继续播放
     */
    public void continuePlay(){
        mMediaPlayer.start();
        MEDIA_STATUS = MEDIA_STATUS_PLAY;
    }

    /**
     * 4、控制播放器停止播放，这里注意回收
     */
    public void stopPlay(){
        mMediaPlayer.stop();
        MEDIA_STATUS = MEDIA_STATUS_STOP;
    }

    /**
     * 5、获取当前播放器播放的位置
     */
    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 6、获取当前播放器播放的音乐总时长
     */
    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    /**
     * 7、从指定时间开始播放
     */
    public void seekTo(int ms){
        mMediaPlayer.seekTo(ms);
    }

    /**
     * 8、监听播放结束
     */
    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
        mMediaPlayer.setOnCompletionListener(listener);
    }

    /**
     * 9、监听播放出错
     */
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener){
        mMediaPlayer.setOnErrorListener(listener);
    }

    /**
     * 10、监听播放进度
     */
    public void setOnProgressListener(OnMusicProgressListener listener){
        musicProgressListener = listener;
    }

    public interface OnMusicProgressListener{
        void OnProgress();
    }


}

