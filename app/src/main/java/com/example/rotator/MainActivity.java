package com.example.rotator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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

import org.apache.commons.lang3.ObjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.rotator.util.ConstUtils.AUDIO;
import static com.example.rotator.util.ConstUtils.FILEPATH;
import static com.example.rotator.util.ConstUtils.IMAGE;
import static com.example.rotator.util.ConstUtils.TIMEOUT;
import static com.example.rotator.util.ConstUtils.TYPE_AUDIO_ARRAY_8386;
import static com.example.rotator.util.ConstUtils.TYPE_IMAGE_ARRAY_8386;
import static com.example.rotator.util.ConstUtils.TYPE_VIDEO_ARRAY_8386;
import static com.example.rotator.util.ConstUtils.VIDEO;

/**
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class MainActivity extends AppCompatActivity implements RotatorType{
    private static final String TAG = "MainActivity";
    private LayoutInflater inflater;
    private LinearLayout linearLayoutBoardViewpager;
    /**
     * 图片、音乐、视频文件路径列表
     */
    private ArrayList<String> imagePathList = new ArrayList<>();
    private ArrayList<String> audioPathList = new ArrayList<>();
    private ArrayList<String> videoPathList = new ArrayList<>();
    private ImageRotator imageRotator;
    private VideoRotator videoRotator;
    private AudioRotator audioRotator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        linearLayoutBoardViewpager = findViewById(R.id.ll_board_viewpager);
        inflater = LayoutInflater.from(this);
        getFilePath(FILEPATH);
        if(imagePathList.size() == 0 && audioPathList.size() == 0 && videoPathList.size() == 0){
            showWarningDialog();
            return;
        }
        initImageView();

    }

    private void initImageView() {
        if(imagePathList.size() == 0){
            initAudioView();
            return;
        }
        linearLayoutBoardViewpager.removeAllViews();
        imageRotator = new ImageRotator(this, inflater, TIMEOUT, this);
        linearLayoutBoardViewpager.addView(imageRotator.initView(imagePathList));
    }

    private void initAudioView() {
        if(audioPathList.size() == 0){
            initVideoView();
            return;
        }
        linearLayoutBoardViewpager.removeAllViews();
        audioRotator = new AudioRotator(this, inflater, audioPathList,this);
        linearLayoutBoardViewpager.addView(audioRotator.initView());
    }

    private void initVideoView() {
        if(videoPathList.size() == 0){
            initImageView();
            return;
        }
        linearLayoutBoardViewpager.removeAllViews();
        videoRotator = new VideoRotator(this, inflater, videoPathList,this);
        linearLayoutBoardViewpager.addView(videoRotator.initView());
    }

    /**
     * 读写权限检查判断
     *
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
     */
    public void getFilePath(String filepath) {
        File mFile = new File(filepath);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && isStoragePermissionGranted()) {
            if (mFile.isDirectory()) {
                Log.d(TAG, filepath);
                for (File fileUrl : Objects.requireNonNull(mFile.listFiles())) {
                    String path = fileUrl.getAbsolutePath();
                    for (String imagePath : TYPE_IMAGE_ARRAY_8386) {
                        if (path.toLowerCase().endsWith(imagePath)) {
                            Log.d(TAG, path);
                            imagePathList.add(path);
                            break;
                        }
                    }
                    for (String imagePath : TYPE_AUDIO_ARRAY_8386) {
                        if (path.toLowerCase().endsWith(imagePath)) {
                            Log.d(TAG, path);
                            audioPathList.add(path);
                            break;
                        }
                    }
                    for (String imagePath : TYPE_VIDEO_ARRAY_8386) {
                        if (path.toLowerCase().endsWith(imagePath)) {
                            Log.d(TAG, path);
                            videoPathList.add(path);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 无指定文件提示
     */
    public void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("警告");
        builder.setMessage("未找到指定类型文件");
        builder.setIcon(R.drawable.ic_launcher_icon);
        builder.setCancelable(false);
        builder.setPositiveButton("知道了！", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MainActivity.this.finish();
            }
        });
        builder.create().show();
    }

    @Override
    public void setType(String type){
        Log.d(TAG,type);
        switch (type) {
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
