package com.example.rotator;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;

/**
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

    public AudioRotator(Context context, LayoutInflater inflater, ArrayList<String> audioList) {
        this.context = context;
        this.inflater = inflater;
        this.audioList = audioList;
    }

    public View initView() {
        View view = inflater.inflate(R.layout.audio_image, null);
        imageView = view.findViewById(R.id.audio_play_background);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion (MediaPlayer mediaPlayer){
                nextAudio();
            }
        });
        audioPlay();
        return view;
    }

    private void nextAudio() {
        if (audioIndex < audioList.size() - 1) {
            audioIndex = audioIndex + 1;
            audioPlay();
        } else {
            audioList.clear();
            audioIndex = 0;
            mediaPlayer.release();
            mediaPlayer = null;
            sendVideoBroadcast();
        }
    }
    private void audioPlay(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioList.get(audioIndex));
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (Exception e){}
    }
    private void sendVideoBroadcast(){
        Intent mIntent = new Intent();
        mIntent.setAction("android.intent.action.rotator");
        mIntent.putExtra("data","video");
        context.sendBroadcast(mIntent);
        Log.d(TAG,mIntent.getAction());
    }
}

