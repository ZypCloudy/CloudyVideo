package com.example.cloudy.cloudyvideo.base;

import android.content.Context;
import android.view.View;

public abstract class BasePager {
    public  final Context context;
    public View rootView;
    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }
    /**
     * 视图
     * @return
     */
    public abstract View initView();
    /**
     * 数据
     */
    public void initData(){}
}
