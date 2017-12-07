package com.example.screenrotation;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.screenrotation.utils.ScreenRotateUtil;

public class MainActivity extends Activity {

    @Override
    protected void onResume() {
        super.onResume();
        ScreenRotateUtil.getInstance(this).start(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    /**
     * 横竖屏切换或者输入法等事件触发时调用
     * 需要在清单文件中配置权限
     * 需要在当前Activity配置configChanges属性
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (ScreenRotateUtil.getInstance(this).isLandscape()) {
            Toast.makeText(this, "当前为横屏", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "当前为竖屏", Toast.LENGTH_SHORT).show();
        }
    }

    public void scaleFull(View view) {
        ScreenRotateUtil.getInstance(this).toggleRotate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ScreenRotateUtil.getInstance(this).stop();
    }
}
