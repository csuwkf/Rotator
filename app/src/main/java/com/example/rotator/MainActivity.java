package com.example.rotator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/testwkf";
    private LayoutInflater inflater;
    private LinearLayout ll_board_viewpager;
    //图片文件路径列表
    private ArrayList<String> imagePathList = new ArrayList<>();
    //音乐文件路径列表
    private ArrayList<String> audioPathList = new ArrayList<>();
    //视频文件路径列表
    private ArrayList<String> videoPathList = new ArrayList<>();
    private File mFile;
    private MediaPlayer mediaPlayer;

    private static final String[] TYPE_IMAGE_ARRAY_8386 = new String[]{
            ".jpg", ".png", ".gif", ".jpeg", ".bmp"};
    private static final String[] TYPE_AUDIO_ARRAY_8386 = new String[]{
            ".aac", ".ape", ".flac", ".m4a", ".mp3", ".ogg", ".wav"};
    private static final String[] TYPE_VIDEO_ARRAY_8386 = new String[]{
            ".3g2", ".3gp", ".avi", ".flv", ".f4v", ".mkv", ".mov", ".mp4", ".VOB", ".mpg", ".MPEG", ".ts", ".m4v"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ll_board_viewpager = findViewById(R.id.ll_board_viewpager);
        inflater = LayoutInflater.from(this);
        initAudioView();
    }
    
    private void initImageView() {
        ll_board_viewpager.removeAllViews();
        GetFilePath(filePath,TYPE_IMAGE_ARRAY_8386,imagePathList);
        ll_board_viewpager.addView(new ImageRotator(this,inflater,3000).initView(imagePathList)); //这里是添加图片轮播器
    }
    private void initVideoView() {
        ll_board_viewpager.removeAllViews();
        GetFilePath(filePath,TYPE_VIDEO_ARRAY_8386,videoPathList);
        ll_board_viewpager.addView(new VideoRotator(this,inflater,videoPathList).initView());
    }

    private void initAudioView() {
        ll_board_viewpager.removeAllViews();
        GetFilePath(filePath,TYPE_AUDIO_ARRAY_8386,audioPathList);
        ll_board_viewpager.addView(new AudioRotator(this,inflater,audioPathList).initView());
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            final Context context = getApplicationContext();
            int readPermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (readPermissionCheck == PackageManager.PERMISSION_GRANTED && writePermissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.v("juno", "Permission is granted");
                return true;
            } else {
                Log.v("juno", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("juno", "Permission is granted");
            return true;
        }
    }

    public void GetFilePath(String filepath,String[] type,ArrayList<String> pathList){
        mFile = new File(filepath);
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && isStoragePermissionGranted()){
            if(mFile.isDirectory()) {
                Log.d(TAG,filepath);
                for (File fileUrl : Objects.requireNonNull(mFile.listFiles())) {
                    String path = fileUrl.getAbsolutePath();
                    for(String imagePath : type){
                        if(path.toLowerCase().endsWith(imagePath)){
                            Log.d(TAG,path);
                            pathList.add(path);
                            break;
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
