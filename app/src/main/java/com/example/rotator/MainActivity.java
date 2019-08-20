package com.example.rotator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.rotator.rotator.AudioRotator;
import com.example.rotator.rotator.ImageRotator;
import com.example.rotator.rotator.VideoRotator;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.rotator.ConstUtils.AUDIO;
import static com.example.rotator.ConstUtils.FILEPATH;
import static com.example.rotator.ConstUtils.IMAGE;
import static com.example.rotator.ConstUtils.ROTATOR;
import static com.example.rotator.ConstUtils.TIMEOUT;
import static com.example.rotator.ConstUtils.TYPE_AUDIO_ARRAY_8386;
import static com.example.rotator.ConstUtils.TYPE_VIDEO_ARRAY_8386;
import static com.example.rotator.ConstUtils.VIDEO;
/**
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private LayoutInflater inflater;
    private LinearLayout linearLayoutBoardViewpager;
    /**
     * 图片、音乐、视频文件路径列表
     */
    private ArrayList<String> imagePathList = new ArrayList<>();
    private ArrayList<String> audioPathList = new ArrayList<>();
    private ArrayList<String> videoPathList = new ArrayList<>();
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        linearLayoutBoardViewpager = findViewById(R.id.ll_board_viewpager);
        inflater = LayoutInflater.from(this);
        receiver = new RotatorBroadcastReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ROTATOR);
        registerReceiver(receiver,mIntentFilter);
        initVideoView();
    }
    
    private void initImageView() {
        linearLayoutBoardViewpager.removeAllViews();
        getFilePath(FILEPATH,ConstUtils.TYPE_IMAGE_ARRAY_8386,imagePathList);
        linearLayoutBoardViewpager.addView(new ImageRotator(this,inflater,TIMEOUT).initView(imagePathList));
    }
    private void initVideoView() {
        linearLayoutBoardViewpager.removeAllViews();
        getFilePath(FILEPATH,TYPE_VIDEO_ARRAY_8386,videoPathList);
        linearLayoutBoardViewpager.addView(new VideoRotator(this,inflater,videoPathList).initView());
    }

    private void initAudioView() {
        linearLayoutBoardViewpager.removeAllViews();
        getFilePath(FILEPATH,TYPE_AUDIO_ARRAY_8386,audioPathList);
        linearLayoutBoardViewpager.addView(new AudioRotator(this,inflater,audioPathList).initView());
    }

    /**
     * 读写权限检查判断
     * @return 是否拥有权限
     */
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

    /**
     * 文件夹下指定文件读取
     * @param filepath 文件夹路径
     * @param type 指定文件类型
     * @param pathList 指定文件集合
     */
    public void getFilePath(String filepath,String[] type,ArrayList<String> pathList){
        File mFile = new File(filepath);
        pathList.clear();
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && isStoragePermissionGranted()) {
            if (mFile.isDirectory()) {
                Log.d(TAG, filepath);
                for (File fileUrl : Objects.requireNonNull(mFile.listFiles())) {
                    String path = fileUrl.getAbsolutePath();
                    for (String imagePath : type) {
                        if (path.toLowerCase().endsWith(imagePath)) {
                            Log.d(TAG, path);
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
        unregisterReceiver(receiver);
    }


    public class RotatorBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            if(action.equals(ROTATOR)) {
                String str = intent.getStringExtra("data");
                Log.d(TAG,str);
                switch (str) {
                    case IMAGE:
                        initImageView();
                        break;
                    case AUDIO:
                        initAudioView();
                        break;
                    case VIDEO:
                        initVideoView();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
