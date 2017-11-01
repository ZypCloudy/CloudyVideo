package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.activity.MainActivity;

public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Handler hander =  new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hander.postDelayed(new Runnable() {
            @Override
            public void run() {
                //两秒后执行主线程
                startMainActivity();
                Log.e(TAG,"哈哈"+Thread.currentThread().getName());
            }
        },2000);
    }
    /*
     跳转到主页面并关闭当前页面
    */
    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG,"哈哈"+event.getAction());
        startMainActivity();
        return super.onTouchEvent(event);
    }
    //修复刚打开立马退出，会再次进入主界面的Bug
    @Override
    protected void onDestroy() {
        hander.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
