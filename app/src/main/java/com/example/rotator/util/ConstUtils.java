package com.example.rotator.util;

import android.os.Environment;

/**
 * 常量类
 *
 * @author 吴科烽
 * @date 2019-08-20
 **/
public class ConstUtils {
    private ConstUtils() {
    }

    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";

    public static final String FILEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/testwkf";
    public static final int TIMEOUT = 3000;

    public static final String[] TYPE_IMAGE_ARRAY_8386 = new String[]{
            ".jpg", ".png", ".gif", ".jpeg", ".bmp"};
    public static final String[] TYPE_AUDIO_ARRAY_8386 = new String[]{
            ".aac", ".ape", ".flac", ".m4a", ".mp3", ".ogg", ".wav"};
    public static final String[] TYPE_VIDEO_ARRAY_8386 = new String[]{
            ".3g2", ".3gp", ".avi", ".flv", ".f4v", ".mkv", ".mov", ".mp4", ".VOB", ".mpg", ".MPEG", ".ts", ".m4v"};
}
