package com.example.cloudy.cloudyvideo.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.example.cloudy.cloudyvideo.base.BasePager;
import com.example.cloudy.cloudyvideo.utils.LogUtil;

public class MinePager extends BasePager{
    private TextView textView;
    public MinePager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextColor(Color.BLUE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(25);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("我的数据加载");
        textView.setText("我的");
    }
}
