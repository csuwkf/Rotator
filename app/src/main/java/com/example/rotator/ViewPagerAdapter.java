package com.example.rotator;

import android.content.Context;
import com.bumptech.glide.Glide;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * @author 吴科烽
 * @date 2019-08-19
 **/
public class ViewPagerAdapter extends PagerAdapter {
    private Context context; //上下文对象
    private List<String> datas;// 数据源
    private List<View> views;

    public ViewPagerAdapter(Context context, List<View> views, List<String> datas) {
        this.context=context;
        this.views=views;
        this.datas=datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        View view=views.get(position);
        try {
            String url=datas.get(position);
            ImageView item_image = view.findViewById(R.id.item_image);
            Glide.with(context).load(url).into(item_image);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  view;
    }
}

