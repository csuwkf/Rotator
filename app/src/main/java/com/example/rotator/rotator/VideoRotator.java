package com.example.rotator.rotator;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.example.rotator.R;
import com.example.rotator.RotatorType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.rotator.util.ConstUtils.IMAGE;
import static com.example.rotator.util.VideoSizeUtils.changeVideoSize;

/**
 * 视频轮播功能
 *
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class VideoRotator implements SurfaceHolder.Callback {
    private static final String TAG = "VideoRotator";
    /**
     * cachePlayer:负责setNextMediaPlayer的player缓存对象
     * currentPlayer:负责当前播放视频段落的player对象
     */
    private MediaPlayer firstPlayer, nextMediaPlayer, cachePlayer, currentPlayer;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> videoList;
    private RotatorType callBackType;

    /**
     * 所有player对象的缓存
     */
    private HashMap<String, MediaPlayer> playersCache = new HashMap<>();
    /**
     * 当前播放到的视频段数
     */
    private int currentIndex = 0;

    public VideoRotator(Context context, LayoutInflater inflater, ArrayList<String> videoList, RotatorType callBackType) {
        this.context = context;
        this.inflater = inflater;
        this.videoList = videoList;
        this.callBackType = callBackType;
    }

    public View initView() {
        View view = inflater.inflate(R.layout.video_item, null);
        surfaceView = view.findViewById(R.id.surfaceview_video);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        return view;
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        initFirstPlayer();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }


    /**
     * 初始化播放首段视频的player
     */
    private void initFirstPlayer() {
        firstPlayer = new MediaPlayer();
        firstPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        firstPlayer.setDisplay(surfaceHolder);
        firstPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onVideoPlayCompleted(mediaPlayer);
            }
        });
        firstPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                changeVideoSize(mediaPlayer, mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight(), context, surfaceView);
            }
        });
        cachePlayer = firstPlayer;
        initNextPlayer();
        //初始化完成后，开启播放
        startPlayFirstVideo();
    }

    /**
     * 播放第一段视频
     */
    private void startPlayFirstVideo() {
        try {
            firstPlayer.setDataSource(videoList.get(currentIndex));
            firstPlayer.prepare();
            firstPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 缓冲下一段视频
     */
    private void initNextPlayer() {
        ThreadPoolExecutor singleThreadExecutor = new ThreadPoolExecutor(
                1, 1, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        singleThreadExecutor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                for (int i = 1; i < videoList.size(); i++) {
                    nextMediaPlayer = new MediaPlayer();
                    nextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    nextMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            onVideoPlayCompleted(mediaPlayer);
                        }
                    });
                    nextMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
                            changeVideoSize(mediaPlayer, mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight(), context, surfaceView);
                        }
                    });
                    try {
                        nextMediaPlayer.setDataSource(videoList.get(i));
                        nextMediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cachePlayer.setNextMediaPlayer(nextMediaPlayer);
                    cachePlayer = nextMediaPlayer;
                    playersCache.put(String.valueOf(i), nextMediaPlayer);
                }
            }
        });
        singleThreadExecutor.shutdown();
    }

    private void onVideoPlayCompleted(MediaPlayer mediaPlayer) {
        mediaPlayer.setDisplay(null);
        currentPlayer = playersCache.get(String.valueOf(++currentIndex));
        if (currentIndex == videoList.size()) {
            onDestroy();
            callBackType.setType(IMAGE);
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
        currentIndex = 0;
    }

}