package com.dongjian.framwork.manager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

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
    public int MEDIA_STATUS = MEDIA_STATUS_STOP;

    private MediaPlayer mMediaPlayer;
    private OnMusicProgressListener musicProgressListener;

    private static final int H_PROGRESS = 1000;

    /**
     * 用Handler来获取歌曲的进度：开始播放的时候开始循环计时，然后抛出进度结果
     */
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //判断传入消息的类型，如果是H_PROGRESS就开始计算歌曲进度
            switch (msg.what){
                case H_PROGRESS:
                    //1、此接口不为空，证明需要监听歌曲进度；为空则不监听
                    if(musicProgressListener != null){
                        //2、获取当前时长,算出在歌曲里的百分比  然后抛给监听接口
                        int currentPosition = getCurrentPosition();
                        int pos = (int)( ((float) currentPosition) / ((float)getDuration()) * 100);
                        musicProgressListener.OnProgress(currentPosition,pos);
                        //3、延迟一秒继续做循环操作 -- 每隔一秒会往外抛出当前歌曲进度
                        mHandler.sendEmptyMessageDelayed(H_PROGRESS,1000);

                    }
                    break;
            }
            return false;
        }
    });

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
    public void startPlay(AssetFileDescriptor path){
        try {
            //1、重置一下播放器
            mMediaPlayer.reset();
            //2、设置资源
            //mMediaPlayer.setDataSource(path);  这样只能允许N版本后的进行播放
            mMediaPlayer.setDataSource(path.getFileDescriptor(),path.getStartOffset(),path.getLength());
            //3、以同步的方式装载好流媒体文件
            mMediaPlayer.prepare();
            //4、开始播放
            mMediaPlayer.start();
            //5、设置媒体当前状态
            MEDIA_STATUS = MEDIA_STATUS_PLAY;
            //6、给Handler发送消息,以便开始计算歌曲进度
            mHandler.sendEmptyMessage(H_PROGRESS);
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
            //移除Handler的消息
            mHandler.removeMessages(H_PROGRESS);
        }
    }

    /**
     * 3、控制播放器继续播放
     */
    public void continuePlay(){
        mMediaPlayer.start();
        MEDIA_STATUS = MEDIA_STATUS_PLAY;
        //继续发送消息给Handler
        mHandler.sendEmptyMessage(H_PROGRESS);
    }

    /**
     * 4、控制播放器停止播放，这里注意回收
     */
    public void stopPlay(){
        mMediaPlayer.stop();
        MEDIA_STATUS = MEDIA_STATUS_STOP;
        //移除消息
        mHandler.removeMessages(H_PROGRESS);
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
        /**
         *
         *获取当前歌曲进度
         * @param progress 当前歌曲播放的时长
         * @param pos 当前歌曲播放的百分比
         */
        void OnProgress(int progress,int pos);
    }


}

