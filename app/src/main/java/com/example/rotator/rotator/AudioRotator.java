package com.example.rotator.rotator;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.rotator.R;
import com.example.rotator.RotatorType;

import java.util.ArrayList;

import static com.example.rotator.util.ConstUtils.VIDEO;

/**
 * 音乐轮播功能
 *
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class AudioRotator {
    private static final String TAG = "AudioRotator";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<String> audioList;
    private Context context;
    private LayoutInflater inflater;
    private int audioIndex = 0;
    private ImageView imageView;
    private RotatorType callBackType;

    public AudioRotator(Context context, LayoutInflater inflater, ArrayList<String> audioList, RotatorType callBackType) {
        this.context = context;
        this.inflater = inflater;
        this.audioList = audioList;
        this.callBackType = callBackType;
    }

    public View initView() {
        View view = inflater.inflate(R.layout.audio_image, null);
        imageView = view.findViewById(R.id.audio_play_background);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextAudio();
            }
        });
        audioPlay();

        return view;
    }

    /**
     * 下一首播放
     */
    private void nextAudio() {
        if (audioIndex < audioList.size() - 1) {
            audioIndex = audioIndex + 1;
            audioPlay();
        } else {
            onDestroy();
            callBackType.setType(VIDEO);
        }
    }

    /**
     * 音乐播放
     */
    private void audioPlay() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioList.get(audioIndex));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDestroy() {
        audioIndex = 0;
        mediaPlayer.release();
        mediaPlayer = null;
    }
}

