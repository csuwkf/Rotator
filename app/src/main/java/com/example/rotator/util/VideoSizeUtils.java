package com.example.rotator.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

/**
 * @author 吴科烽
 * @date 2019-08-22
 **/
public class VideoSizeUtils {
    private static final String TAG = "VideoSizeUtils";

    public static void changeVideoSize(MediaPlayer mediaPlayer, int width, int height, Context context, SurfaceView surfaceView) {
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        Log.e(TAG, "changeVideoSize: deviceHeight=" + deviceHeight + "deviceWidth=" + deviceWidth + "width=" + width + "height=" + height);
        float devicePercent = 0;
        //求屏幕比例
        if (context.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            devicePercent = (float) deviceWidth / (float) deviceHeight;
        } else {
            devicePercent = (float) deviceHeight / (float) deviceWidth;
        }
        if (width > height) {
            width = deviceWidth;
            height = (int) (deviceWidth * devicePercent);
        } else {
            if (context.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                width = deviceWidth;
                //接受在宽度的轻微拉伸来满足视频铺满屏幕的优化
                float videoPercent = (float) width / (float) height;
                float differenceValue = Math.abs(videoPercent - devicePercent);
                if (differenceValue < 0.15) {
                    height = deviceHeight;
                } else {
                    height = (int) (height / devicePercent);
                }
            } else { //横屏
                height = deviceHeight;
                width = (int) (deviceHeight * devicePercent);
            }

        }
        Log.e(TAG, "width=" + width + "height=" + height);
        RelativeLayout.LayoutParams mLayoutParams = new RelativeLayout.LayoutParams(width, height);
        mLayoutParams.setMargins(0, (deviceHeight - height) / 2, 0, (deviceHeight - height) / 2);
        surfaceView.setLayoutParams(mLayoutParams);
    }
}
