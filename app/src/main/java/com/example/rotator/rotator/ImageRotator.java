package com.example.rotator.rotator;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.example.rotator.R;
import com.example.rotator.RotatorType;
import com.example.rotator.ViewPagerAdapter;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.rotator.util.ConstUtils.AUDIO;


/**
 * 图片轮播功能
 *
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class ImageRotator implements ViewPager.OnPageChangeListener {
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
    List<View> viewList = new ArrayList<>();
    /**
     * 圆点指示器
     */
    private ImageView[] dots;
    private int currentIndex;

    private RotatorType callBackType;

    int count = 0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 0) {
                int currentPage = (Integer) message.obj;
                setCurrentDot(currentPage);
                viewPagerImage.setCurrentItem(currentPage);
            } else if (message.what == 1) {
                callBackType.setType(AUDIO);
            }
            return false;
        }
    });

    public ImageRotator(Context context, LayoutInflater inflater, int timeout, RotatorType callBackType) {
        this.context = context;
        this.inflater = inflater;
        this.timeout = timeout;
        this.callBackType = callBackType;
    }

    public View initView(final List<String> datas) {
        View view = inflater.inflate(R.layout.viewpager_board, null);
        LinearLayout mLinearLayout = view.findViewById(R.id.ll_board_dot);
        for (int i = 0; i < datas.size(); i++) {
            viewList.add(inflater.inflate(R.layout.viewpager_item_picture, null));
            mLinearLayout.addView(inflater.inflate(R.layout.viewpager_board_dot, null));
        }
        initDots(mLinearLayout);
        viewPagerImage = view.findViewById(R.id.vp_board);
        viewPagerImage.addOnPageChangeListener(this);
        ViewPagerAdapter adapter = new ViewPagerAdapter(context, viewList, datas);
        viewPagerImage.setOffscreenPageLimit(3);
        viewPagerImage.setAdapter(adapter);
        ScheduledExecutorService mService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

        mService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int currentPage = count;
                count++;
                Message msg = Message.obtain();
                if (count > viewList.size()) {
                    mService.shutdown();
                    msg.what = 1;
                } else {
                    msg.what = 0;
                    msg.obj = currentPage;
                }
                handler.sendMessage(msg);
            }
        }, 0, timeout, TimeUnit.MILLISECONDS);
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

    /**
     * 当前状态圆点设置
     *
     * @param currentPage 当前页面
     */
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

}
