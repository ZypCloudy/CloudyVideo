package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.*;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.domain.MediaItem;
import com.example.cloudy.cloudyvideo.utils.LogUtil;
import com.example.cloudy.cloudyvideo.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private VideoView videoView;
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private Uri uri;
    private Utils utils;
    private TextView tvSystemTime;
    /**
     * 监听电量的广播
     */
    private MyReceiver myReceiver;

    private ArrayList<MediaItem> mediaItems;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
        getData();
        initData();
        findViews();
        setListener();
        setData();
        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
    }

    private void setData() {
        if(mediaItems != null &&mediaItems .size() >0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置视频的名称
            videoView.setVideoPath(mediaItem.getData());

        }else if(uri !=null){
            tvName.setText(uri.toString());//设置视频的名称
            videoView.setVideoURI(uri);
        }else{
            Toast.makeText(SystemVideoPlayer.this, "没有传递数据", Toast.LENGTH_SHORT).show();
        }
        //设置按钮状态
        setButtonState();
    }

    private void getData(){
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position",0);
    }
    private void initData() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化的时候发这个广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver, intentFilter);
    }
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);//0~100;
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if(level <= 0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if(level <=10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if(level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if(level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if(level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if(level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if(level <= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    private void findViews() {
        setContentView(R.layout.system_video_player);
        videoView = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );

        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );
    }

    @Override
    public void onClick(View v) {
        if ( v == btnExit ) {
            // Handle clicks for btnExit
            finish();
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            // Handle clicks for btnVideoStartPause
            if(videoView.isPlaying()){
                videoView.pause();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
            }else {
                videoView.start();
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            }
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
        }
    }
    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if(mediaItems != null && mediaItems.size()>0){
            //播放上一个视频
            position--;
            if(position >= 0){

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if(mediaItems != null && mediaItems.size()>0){
            //播放下一个
            position++;
            if(position < mediaItems.size()){

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                videoView.setVideoPath(mediaItem.getData());
                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
            setButtonState();
        }
    }
    private void setButtonState() {
        if(mediaItems != null && mediaItems.size() >0){
            if(mediaItems.size()==1){
                setEnable(false);
            }else if(mediaItems.size()==2) {
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);

                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);

                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);

                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);

                }
            }else{
                if(position==0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else if(position==mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else{
                    setEnable(true);
                }
            }
        }else if(uri != null){
            //两个按钮设置灰色
            setEnable(false);
        }
    }
    private void setEnable(boolean isEnable) {
        if(isEnable){
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        }else{
            //两个按钮设置灰色
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }
    private void setListener() {
        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoView.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());

        //设置SeeKbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PROGRESS:
                    //1.得到当前的视频播放进度
                    int currentPosition = videoView.getCurrentPosition();
                    //2.SeekBar.setProgress(当前进度);
                    seekbarVideo.setProgress(currentPosition);
                    //更新文本播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentPosition));
                    //设置系统时间
                    tvSystemTime.setText(getSysteTime());
                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    /**
     * 得到系统时间
     * @return
     */
    private String getSysteTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();//开始播放
            //1.视频的总时长，关联总长度
            int duration =  videoView.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(SystemVideoPlayer.this, "播放错误", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
        }
    }
    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当手指滑动的时候，会引起SeekBar进度变化，会回调这个方法
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的true,不是用户引起的false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                videoView.seekTo(progress);
            }
        }
        /**
         * 当手指触碰的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        /**
         * 当手指离开的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    protected void onDestroy() {
        //释放资源的时候，先释放子类，在释放父类
        if(myReceiver != null){
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        LogUtil.e("onDestroy--");
        super.onDestroy();//释放父类
    }

}