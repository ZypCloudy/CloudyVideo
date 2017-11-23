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
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import android.widget.*;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.domain.MediaItem;
import com.example.cloudy.cloudyvideo.utils.LogUtil;
import com.example.cloudy.cloudyvideo.utils.Utils;
import com.example.cloudy.cloudyvideo.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayer extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int HIDE_MEDIACONTROLLER = 2;
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
    private RelativeLayout media_controller;
    private Uri uri;
    private Utils utils;
    private TextView tvSystemTime;
    /**
     * 全屏
     */
    private static final int FULL_SCREEN = 1;
    /**
     * 默认屏幕
     */
    private static final int DEFAULT_SCREEN = 2;
    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;

    /**
     * 屏幕的宽
     */
    private int screenWidth = 0;

    /**
     * 屏幕的高
     */
    private int screenHeight = 0;

    /**
     * 真实视频的宽
     */
    private int videoWidth;
    /**
     * 真实视频的高
     */
    private int videoHeight;
    /**
     * 监听电量的广播
     */
    private MyReceiver myReceiver;

    private ArrayList<MediaItem> mediaItems;
    private int position;

    //1.设置手势
    private GestureDetector detector;

    private boolean isshowMediaController = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
        getData();
        initData();
        findViews();
        setListener();
        setData();

        //设置手势
        gesture();
        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
    }

    private void gesture() {
        //2.实例化手势识别器，并且重写双击，点击，长按
        detector  = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被双击了", Toast.LENGTH_SHORT).show();
                startAndPause();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPlayer.this, "我被单击了", Toast.LENGTH_SHORT).show();
                if(isshowMediaController){
                    //隐藏
                    hideMediaController();
                    //把隐藏消息移除
                    handler.removeMessages(HIDE_MEDIACONTROLLER);

                }else{
                    //显示
                    showMediaController();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    private void startAndPause() {
        if(videoView.isPlaying()){
            videoView.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else {
            videoView.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }
    /**
     * 显示控制面板
     */
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }
    /**
     * 隐藏控制面板
     */
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
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

        //得到屏幕的宽和高
        //过时的方式
//        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //得到屏幕的宽和高最新方式
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
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
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);

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
            startAndPause();
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
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
                case HIDE_MEDIACONTROLLER://隐藏控制面板
                    hideMediaController();
                    break;
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
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();
            videoView.start();//开始播放
            //1.视频的总时长，关联总长度
            int duration =  videoView.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));

            hideMediaController();
            //2.发消息
            handler.sendEmptyMessage(PROGRESS);

//            videoView.setVideoSize(mp.getVideoWidth(),mp.getVideoHeight());
            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);
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
//            Toast.makeText(SystemVideoPlayer.this, "播放完成了="+uri, Toast.LENGTH_SHORT).show();
            //播放完成自动播放下一个
            playNextVideo();
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
            handler.removeMessages(HIDE_MEDIACONTROLLER);
        }

        /**
         * 当手指离开的时候回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void setFullScreenAndDefault() {
        if(isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else{
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN://全屏
                //1.设置视频画面的大小-屏幕有多大就是多大
                videoView.setVideoSize(screenWidth,screenHeight);
                //2.设置按钮的状态-默认
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //1.设置视频画面的大小
                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoView.setVideoSize(width,height);
                //2.设置按钮的状态--全屏
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }
}