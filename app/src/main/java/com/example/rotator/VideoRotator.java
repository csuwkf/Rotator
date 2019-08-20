package com.example.rotator;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class VideoRotator implements SurfaceHolder.Callback{
    private static final String TAG = "VideoRotator";
    private MediaPlayer firstPlayer,     //负责播放进入视频播放界面后的第一段视频
            nextMediaPlayer, //负责一段视频播放结束后，播放下一段视频
            cachePlayer,     //负责setNextMediaPlayer的player缓存对象
            currentPlayer;   //负责当前播放视频段落的player对象
    //负责配合mediaPlayer显示视频图像播放的surfaceView
    private SurfaceView surface;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private LayoutInflater inflater;

    //存放所有视频端的url
    private ArrayList<String> videoList;
    //所有player对象的缓存
    private HashMap<String, MediaPlayer> playersCache = new HashMap<>();
    //当前播放到的视频段落数
    private int currentVideoIndex;

    public VideoRotator(Context context, LayoutInflater inflater,ArrayList<String> videoList) {
        this.context = context;
        this.inflater = inflater;
        this.videoList = videoList;
    }
    public View initView(){
        View view = inflater.inflate(R.layout.video_item,null);
        surface = view.findViewById(R.id.surfaceview_video);
        surfaceHolder = surface.getHolder();// SurfaceHolder是SurfaceView的控制接口
        surfaceHolder.addCallback(this); // 因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
        return  view;
    }
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        //然后初始化播放手段视频的player对象
        initFirstPlayer();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {}

    /*
     * 初始化播放首段视频的player
     */
    private void initFirstPlayer() {
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(surfaceHolder);
        firstPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVideoPlayCompleted(mp);
            }
        });

        //设置cachePlayer为该player对象
        cachePlayer = firstPlayer;
        initNextPlayer();

        //player对象初始化完成后，开启播放
        startPlayFirstVideo();
    }

    private void startPlayFirstVideo() {
        try {
            firstPlayer.setDataSource(videoList.get(currentVideoIndex));
            firstPlayer.prepare();
            firstPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initNextPlayer() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                for (int i = 1; i < videoList.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            onVideoPlayCompleted(mp);
                        }
                    });
                    try {
                        nextMediaPlayer.setDataSource(videoList.get(i));
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        // TODO 自动生成的 catch 块
                        e.printStackTrace();
                    }
                    //set next mediaplayer
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    //set new cachePlayer
                    cachePlayer = nextMediaPlayer;
                    playersCache.put(String.valueOf(i), nextMediaPlayer);
                }
            }
        }).start();
    }


    private void onVideoPlayCompleted(MediaPlayer mp) {
        mp.setDisplay(null);
        //get next player
        currentPlayer = playersCache.get(String.valueOf(++currentVideoIndex));
        if(currentVideoIndex == videoList.size()){
            onDestroy();
            sendImageBroadcast();
        }
        if (currentPlayer != null) {
            currentPlayer.setDisplay(surfaceHolder);
        }
    }

    protected void onDestroy() {
        if (firstPlayer != null) {
            if (firstPlayer.isPlaying()) {
                firstPlayer.stop();
            }
            firstPlayer.release();
        }
        if (nextMediaPlayer != null) {
            if (nextMediaPlayer.isPlaying()) {
                nextMediaPlayer.stop();
            }
            nextMediaPlayer.release();
        }

        if (currentPlayer != null) {
            if (currentPlayer.isPlaying()) {
                currentPlayer.stop();
            }
            currentPlayer.release();
        }
        currentPlayer = null;
    }

    private void sendImageBroadcast(){
        Intent mIntent = new Intent();
        mIntent.setAction("android.intent.action.rotator");
        mIntent.putExtra("data","image");
        Log.d(TAG,mIntent.getAction());
        context.sendBroadcast(mIntent);
    }

}