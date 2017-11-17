package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.view.VideoView;

public class SystemVideoPlayer extends Activity{
    private VideoView videoview;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.system_video_player);
//        videoview = (VideoView) findViewById(R.id.video_view);
        findViews();
        setListener();
        //得到播放地址
        uri = getIntent().getData();
        if(uri != null){
            videoview.setVideoURI(uri);
        }
        //设置控制面板
//        videoview.setMediaController(new MediaController(this));
    }
    private void findViews() {
        setContentView(R.layout.system_video_player);
//        llTop = (LinearLayout)findViewById( R.id.ll_top );
//        tvName = (TextView)findViewById( R.id.tv_name );
//        ivBattery = (ImageView)findViewById( R.id.iv_battery );
//        tvSystemTime = (TextView)findViewById( R.id.tv_system_time );
//        btnVoice = (Button)findViewById( R.id.btn_voice );
//        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
//        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
//        llBottom = (LinearLayout)findViewById( R.id.ll_bottom );
//        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
//        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
//        tvDuration = (TextView)findViewById( R.id.tv_duration );
//        btnExit = (Button)findViewById( R.id.btn_exit );
//        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
//        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
//        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
//        btnVideoSiwchScreen = (Button)findViewById( R.id.btn_video_siwch_screen );
        videoview = (VideoView) findViewById(R.id.video_view);

//        btnVoice.setOnClickListener( this );
//        btnSwichPlayer.setOnClickListener( this );
//        btnExit.setOnClickListener( this );
//        btnVideoPre.setOnClickListener( this );
//        btnVideoStartPause.setOnClickListener( this );
//        btnVideoNext.setOnClickListener( this );
//        btnVideoSiwchScreen.setOnClickListener( this );
    }
    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener() );

        //设置SeeKbar状态变化的监听
//        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoview.start();//开始播放
            //1.视频的总时长，关联总长度
//            int duration =  videoView.getDuration();
//            seekbarVideo.setMax(duration);
//            tvDuration.setText(utils.stringForTime(duration));
//            //2.发消息
//            handler.sendEmptyMessage(PROGRESS);
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

}
