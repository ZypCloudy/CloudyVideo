package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.utils.Utils;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        utils = new Utils();
        super.onCreate(savedInstanceState);
        findViews();
        setListener();
        //得到播放地址
        uri = getIntent().getData();
        if(uri != null){
            videoView.setVideoURI(uri);
        }
        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
    }

    private void findViews() {
        setContentView(R.layout.system_video_player);
        videoView = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
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
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
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
        } else if ( v == btnVideoSiwchScreen ) {
            // Handle clicks for btnVideoSiwchScreen
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
                    //3.每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };
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
}