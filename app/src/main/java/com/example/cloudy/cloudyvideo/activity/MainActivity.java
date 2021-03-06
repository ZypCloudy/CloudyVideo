package com.example.cloudy.cloudyvideo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.base.BasePager;
import com.example.cloudy.cloudyvideo.pager.MusicPager;
import com.example.cloudy.cloudyvideo.pager.MinePager;
import com.example.cloudy.cloudyvideo.pager.RecommendPager;
import com.example.cloudy.cloudyvideo.pager.DownloadPager;
import com.example.cloudy.cloudyvideo.utils.StatusBarUtils;
import com.example.cloudy.cloudyvideo.view.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import java.util.ArrayList;

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
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        StatusBarUtils.setStatusBarColor(this, Color.rgb(30, 144, 205));

        basePagers = new ArrayList<>();
        basePagers.add(new RecommendPager(this));//添加本地视频页面-0
        basePagers.add(new MusicPager(this));//添加本地音乐页面-1
        basePagers.add(new DownloadPager(this));//添加网络视频页面-2
        basePagers.add(new MinePager(this));//添加网络音频页面-3
        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);//默认选中首页


        //资源文件
        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.video_btn_next_gray);
        images.add(R.drawable.video_btn_next_normal);
        images.add(R.drawable.video_btn_next_pressed);
//        Banner banner = (Banner) findViewById(R.id.banner);
//        //设置banner样式
//        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
//        //设置图片加载器
//        banner.setImageLoader(new GlideImageLoader());
//        //设置图片集合
//        banner.setImages(images);
//        //设置banner动画效果
//        banner.setBannerAnimation(Transformer.DepthPage);
//
//        ArrayList<String> titles = new ArrayList<>();
//        titles.add("Hello！");
//        titles.add("World!");
//        titles.add("Haha!");
//
//        //设置标题集合（当banner样式有显示title时）
//        banner.setBannerTitles(titles);
//        //设置自动轮播，默认为true
//        banner.isAutoPlay(true);
//        //设置轮播时间
//        banner.setDelayTime(1500);
//        //设置指示器位置（当banner模式中有指示器时）
//        banner.setIndicatorGravity(BannerConfig.CENTER);
//        //banner设置方法全部调用完毕时最后调用
//        banner.start();

        Banner banner = (Banner) findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }
    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_audio://音频
                    position = 1;
                    break;
                case R.id.rb_net_video://网络视频
                    position = 2;
                    break;
                case R.id.rb_netaudio://网络音频
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
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        //4.提交事务
        ft.commit();
    }

    /**
     * 根据位置得到对应的页面
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager != null && !basePager.isInitData){
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
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
                rg_bottom_tag.check(R.id.rb_video);//首页
                return true;
            }else  if(!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次推出", Toast.LENGTH_SHORT).show();
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
