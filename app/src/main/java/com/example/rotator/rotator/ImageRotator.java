package com.example.rotator.rotator;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.rotator.R;
import com.example.rotator.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.rotator.ConstUtils.AUDIO;
import static com.example.rotator.ConstUtils.ROTATOR;

/**
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class ImageRotator implements ViewPager.OnPageChangeListener {
    private static final String TAG = "ImageRotator";
    private ViewPager viewPagerImage;
    private Context context;
    private LayoutInflater inflater;
    /**
     * 间隔时间
     */
    private int timeout;
    /**
     * 装载图片的view列表
     */
    List<View> viewList;
    /**
     * 圆点指示器
     */
    private ImageView[] dots;
    private int currentIndex;

    Timer timer;
    TimerTask timerTask;
    int count = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                int currentPage = (Integer) msg.obj;
                setCurrentDot(currentPage);
                viewPagerImage.setCurrentItem(currentPage);
            }
        }
    };

    public ImageRotator(Context context, LayoutInflater inflater, int timeout) {
        this.context = context;
        this.inflater = inflater;
        this.timeout = timeout;
    }

    public View initView(final List<String> datas) {
        View view = inflater.inflate(R.layout.viewpager_board, null);
        viewPagerImage = view.findViewById(R.id.vp_board);
        viewPagerImage.addOnPageChangeListener(this);
        viewList = new ArrayList<>();
        LinearLayout mLinearLayout = view.findViewById(R.id.ll_board_dot);
        for (int i = 0; i < datas.size(); i++) {
            viewList.add(inflater.inflate(R.layout.viewpager_item_picture, null));
            mLinearLayout.addView(inflater.inflate(R.layout.viewpager_board_dot, null));
        }
        initDots(mLinearLayout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(context, viewList, datas);
        viewPagerImage.setOffscreenPageLimit(3);
        viewPagerImage.setAdapter(adapter);
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int currentPage = count;
                count++;
                if (count > viewList.size()) {
                    timerTask.cancel();
                    sendAudioBroadcast();
                }
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = currentPage;
                handler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 0, timeout);
        return view;
    }

    /**
     * 初始化圆点
     *
     * @param linearLayout 布局
     */
    private void initDots(LinearLayout linearLayout) {
        dots = new ImageView[viewList.size()];
        //循环获取小圆点指示器
        for (int i = 0; i < viewList.size(); i++) {
            dots[i] = (ImageView) linearLayout.getChildAt(i);
            dots[i].setEnabled(false);
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(true);
    }

    private void setCurrentDot(int currentPage) {
        if (currentPage < 0 || currentPage > viewList.size() - 1 || currentIndex == currentPage) {
            return;
        }
        dots[currentPage].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = currentPage;
    }


    @Override
    public void onPageScrolled(int i, float v, int i1) {
    }

    @Override
    public void onPageSelected(int i) {
        count = i;
        setCurrentDot(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void sendAudioBroadcast() {
        Intent mIntent = new Intent();
        mIntent.setAction(ROTATOR);
        mIntent.putExtra("data", AUDIO);
        Log.d(TAG, mIntent.getAction());
        context.sendBroadcast(mIntent);
    }

}
