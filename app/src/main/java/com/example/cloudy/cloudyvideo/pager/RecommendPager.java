package com.example.cloudy.cloudyvideo.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.cloudy.cloudyvideo.base.BasePager;
import com.example.cloudy.cloudyvideo.utils.LogUtil;

public class RecommendPager extends BasePager {

    private TextView textView;
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_loading;

    public RecommendPager(Context context) {
        super(context);
    }

    /**
     * 初始化当前页面的控件，由父类调用
     * @return
     */
    @Override
    public View initView() {
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLUE);
        textView.setGravity(Gravity.CENTER);
//        View view = View.inflate(context, R.layout.video_pager,null);
//        listview = (ListView) view.findViewById(R.id.listview);
//        tv_nomedia = (TextView) view.findViewById(R.id.tv_nomedia);
//        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
//        return view;
        return textView;
    }


    @Override
    public void initData() {
        super.initData();
        LogUtil.e("推荐的数据被初始化");
        //联网
        //视频内容
        textView.setText("推荐");
    }
}
