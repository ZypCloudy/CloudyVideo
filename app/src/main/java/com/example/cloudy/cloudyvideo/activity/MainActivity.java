//package com.example.cloudy.cloudyvideo.activity;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.RadioGroup;
//import com.example.cloudy.cloudyvideo.R;
//import com.example.cloudy.cloudyvideo.base.BasePager;
//import com.example.cloudy.cloudyvideo.pager.ChannelPager;
//import com.example.cloudy.cloudyvideo.pager.MinePager;
//import com.example.cloudy.cloudyvideo.pager.RecommendPager;
//import com.example.cloudy.cloudyvideo.pager.SearchPager;
//import java.util.ArrayList;
//
//public class MainActivity extends FragmentActivity{
////    private FrameLayout fl_main_content;
//    private RadioGroup rg_bottom_tag;
//    private ArrayList<BasePager> basePagers;
//    private int position;//底部选中的位置
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
//        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_buttom_tag);
//
//        basePagers = new ArrayList<>();
//        basePagers.add(new RecommendPager(this));
//        basePagers.add(new ChannelPager(this));
//        basePagers.add(new SearchPager(this));
//        basePagers.add(new MinePager(this));
//        rg_bottom_tag.check(R.id.rb_recommend);//默认选中首页
//        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
//
//    }
//    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
//        @Override
//        public void onCheckedChanged(RadioGroup group, int checkedId) {
//            switch (checkedId){
//                default:
//                    position = 0;
//                    break;
//                case R.id.rb_channel:
//                    position = 1;
//                    break;
//                case R.id.rb_search:
//                    position = 2;
//                    break;
//                case R.id.rb_mine:
//                    position = 3;
//                    break;
//            }
//            setFragment();
//        }
//    }
//    /**
//     * 把页面添加到Fragment中
//     */
//    private void setFragment() {
//        //1.得到FragmentManger
//        FragmentManager manager = getSupportFragmentManager();
//        //2.开启事务
//        FragmentTransaction ft = manager.beginTransaction();
//        //3.替换
//        ft.replace(R.id.fl_main_content,new Fragment(){
//            @Nullable
//            @Override
//            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//                BasePager basePager = getBasePager();
//                if(basePager != null){
//                    //各个页面的视图
//                    return basePager.rootView;
//                }
//                return null;
//            }
//        });
//        //4.提交事务
//        ft.commit();
//    }
//    /**
//     * 根据位置得到对应的页面
//     * @return
//     */
//    private BasePager getBasePager() {
//        BasePager basePager = basePagers.get(position);
//        if(basePager != null){
//            basePager.initData();//联网请求或者绑定数据
//        }
//        return basePager;
//    }
//}





package com.example.cloudy.cloudyvideo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.base.BasePager;
import com.example.cloudy.cloudyvideo.pager.ChannelPager;
import com.example.cloudy.cloudyvideo.pager.MinePager;
import com.example.cloudy.cloudyvideo.pager.RecommendPager;
import com.example.cloudy.cloudyvideo.pager.SearchPager;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/7/16 10:26
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：主页面
 */
public class MainActivity extends FragmentActivity {


    private RadioGroup  rg_bottom_tag;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;

    /**
     * 选中的位置
     */
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_buttom_tag);

        basePagers = new ArrayList<>();
        basePagers.add(new RecommendPager(this));//添加本地视频页面-0
        basePagers.add(new ChannelPager(this));//添加本地音乐页面-1
        basePagers.add(new SearchPager(this));//添加网络视频页面-2
        basePagers.add(new MinePager(this));//添加网络音频页面-3


        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_recommend);//默认选中首页

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_channel://音频
                    position = 1;
                    break;
                case R.id.rb_search://网络视频
                    position = 2;
                    break;
                case R.id.rb_mine://网络音频
                    position = 3;
                    break;
            }

            setFragment();


        }
    }

    /**
     * 把页面添加到Fragment中
     */
    private void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.fl_main_content,new Fragment(){
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                if(basePager != null){
                    //各个页面的视图
                    return basePager.rootView;
                }
                return null;
            }
        });
        //4.提交事务
        ft.commit();

    }

    /**
     * 根据位置得到对应的页面
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager != null){
            basePager.initData();//联网请求或者绑定数据
        }
        return basePager;
    }


    /**
     * 是否已经退出
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(position != 0){//不是第一页面
                position = 0;
                rg_bottom_tag.check(R.id.rb_recommend);//首页
                return true;
            }else  if(!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次推出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit  = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
