package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.cloudy.cloudyvideo.R;

public class MainActivity extends Activity{
    private FrameLayout fl_main_content;
    private RadioGroup rg_buttom_tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TextView textView = new TextView(this);
//        textView.setText("Hello");
//        textView.setTextSize(30);
//        textView.setGravity(Gravity.CENTER);
//        textView.setTextColor(Color.BLUE);
//        setContentView(textView);
        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_buttom_tag = (RadioGroup) findViewById(R.id.rg_buttom_tag);

        rg_buttom_tag.check(R.id.rb_recommend);
    }
}
