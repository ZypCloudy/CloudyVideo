package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.activity.MainActivity;
import com.example.cloudy.cloudyvideo.utils.StatusBarUtils;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler hander =  new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtils.setStatusBarColor(this, Color.rgb(30, 144, 205));
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后执行主线程
                startMainActivity();
                Log.e(TAG,"哈哈"+Thread.currentThread().getName());
            }
        },2000);
    }
    private boolean isStartMain = false;
    /*
     跳转到主页面并关闭当前页面
    */
    private void startMainActivity() {
        //AndroidManifest.xml 文件里面不使用单例模式，就在这里判断
        if(!isStartMain){
            isStartMain = true;
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            //关闭当前页面
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"哈哈"+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        //修复刚打开立马退出，会再次进入主界面的Bug
        hander.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
