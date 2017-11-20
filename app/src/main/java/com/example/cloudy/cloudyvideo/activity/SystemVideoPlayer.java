package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.example.cloudy.cloudyvideo.R;

public class SystemVideoPlayer extends Activity{
    private VideoView videoView;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_video_player);
        videoView = (VideoView) findViewById(R.id.videoview);
        setListener();
        //得到播放地址
        uri = getIntent().getData();
        if(uri != null){
            videoView.setVideoURI(uri);
        }
        //设置控制面板
        videoView.setMediaController(new MediaController(this));
    }
    private void setListener() {
        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错了的监听
        videoView.setOnErrorListener(new MyOnErrorListener());

        //播放完成了的监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());
    }
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        //当底层解码准备好的时候
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();//开始播放
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