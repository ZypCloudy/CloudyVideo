package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
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
    private boolean isUseSystem = true;
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
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichPlayer;
    private LinearLayout ll_buffer;
    private TextView tv_buffer_netspeed;
    //上一次的播放进度
    private int precurrentPosition;
    private TextView tv_laoding_netspeed;
    private LinearLayout ll_loading;
    private Uri uri;
    private Utils utils;
    private TextView tvSystemTime;
    private boolean isshowMediaController = false;
    //是否网络地址
    private boolean isNetUri;
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
    /**
     * 调用声音
     */
    private AudioManager am;
    private float startY;
    /**
     * 当一按下的音量
     */
    private int mVol;
    /**
     * 屏幕的高
     */
    private float touchRang;
    /**
     * 当前的音量
     */
    private int currentVoice;
    /**
     * 0~15
     * 最大音量
     */
    private int maxVoice;
    /**
     * 是否是静音
     */
    private boolean isMute = false;
    //显示网速
    private static final int SHOW_SPEED = 3;

    private ArrayList<MediaItem> mediaItems;
    private int position;

    //1.设置手势
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = new Utils();
        getData();
        initData();
        findViews();
        setData();
        setListener();


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
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoView.setVideoPath(mediaItem.getData());

        }else if(uri !=null){
            tvName.setText(uri.toString());//设置视频的名称
            isNetUri = utils.isNetUri(uri.toString());
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

        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
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
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwichPlayer = (Button)findViewById( R.id.btn_swich_player );
        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        tv_laoding_netspeed = (TextView) findViewById(R.id.tv_laoding_netspeed);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        btnVoice.setOnClickListener( this );
        btnSwichPlayer.setOnClickListener( this );
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSiwchScreen.setOnClickListener( this );
        //最大音量和SeekBar关联
        seekbarVoice.setMax(maxVoice);
        //设置当前进度-当前音量
        seekbarVoice.setProgress(currentVoice);
        //开始更新网络速度
        handler.sendEmptyMessage(SHOW_SPEED);

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
        } else if ( v == btnVoice ) {
            isMute = !isMute;
            updataVoice(currentVoice,isMute);
        } else if ( v == btnSwichPlayer ) {
            // Handle clicks for btnSwichPlayer
            showSwichPlayerDialog();
        }

        handler.removeMessages(HIDE_MEDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
    }
    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("系统播放器提醒您");
        builder.setMessage("当您播放视频，有声音没有画面的时候，请切换万能播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
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
                isNetUri = utils.isNetUri(mediaItem.getData());
                if(!isNetUri){
                    ll_loading.setVisibility(View.GONE);
                }
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
                isNetUri = utils.isNetUri(mediaItem.getData());
                if(!isNetUri){
                    ll_loading.setVisibility(View.GONE);
                }
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
        //设置音量监听
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());

        //如果是网络视频，则设置网络卡
        if(isNetUri){
            //网络慢，视频卡：使用系统的监听卡，比如播放的文件不是一整个MP4，而是一段段的
            if (isUseSystem) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    videoView.setOnInfoListener(new MyOnInfoListener());
                }
            }
        }
    }
    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了，拖动卡
//                    Toast.makeText(SystemVideoPlayer.this, "卡了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;

                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡结束了，拖动卡结束了
//                    Toast.makeText(SystemVideoPlayer.this, "卡结束了", Toast.LENGTH_SHORT).show();
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_SPEED://显示网速
                    //1.得到网络速度
                    String netSpeed = utils.getNetSpeed(SystemVideoPlayer.this);

                    //显示网络速
                    tv_laoding_netspeed.setText("玩命加载中..."+ netSpeed);
                    tv_buffer_netspeed.setText("缓存中..." + netSpeed);

                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);

                    break;
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

                    //缓存进度
                    if(isNetUri){
                        int buffer = videoView.getBufferPercentage();
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer/100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }else {
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    //如果是网络视频则监听
                    if(isNetUri){
                        //监听卡-能获得当前播放进度的情况使用：一整个MP4文件
                        if (!isUseSystem && videoView.isPlaying()) {

                            if(videoView.isPlaying()){
                                int buffer = currentPosition - precurrentPosition;
                                if (buffer < 500) {
                                    //视频卡了
                                    ll_buffer.setVisibility(View.VISIBLE);
                                } else {
                                    //视频不卡了
                                    ll_buffer.setVisibility(View.GONE);
                                }
                            }else{
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }
                        precurrentPosition = currentPosition;
                    }

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

    class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                if(progress >0 ){
                    isMute = false;
                }else{
                    isMute = true;
                }
                updataVoice(progress,isMute);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIACONTROLLER);

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
        }
    }
    /**
     * 设置音量的大小
     * @param progress
     */
    private void updataVoice(int progress,boolean isMute) {
        if(isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);
            seekbarVoice.setProgress(0);
        }else{
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //3.把事件传递给手势识别器
        detector.onTouchEvent(event);
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                handler.removeMessages(HIDE_MEDIACONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动的记录相关值
                float endY = event.getY();
                float distanceY = startY - endY;
                //改变声音 = （滑动屏幕的距离： 总距离）*音量最大值
                float delta = (distanceY/touchRang)*maxVoice;
                //最终声音 = 原来的 + 改变声音；
                int voice = (int) Math.min(Math.max(mVol+delta,0),maxVoice);
                if(delta != 0){
                    isMute = false;
                    updataVoice(voice,isMute);
                }

//                startY = event.getY();//不要加
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 监听物理健，实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice --;
            updataVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++;
            updataVoice(currentVoice,false);
            handler.removeMessages(HIDE_MEDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

            //把加载页面消失掉
            ll_loading.setVisibility(View.GONE);

//            mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//                @Override
//                public void onSeekComplete(MediaPlayer mp) {
//                    Toast.makeText(SystemVideoPlayer.this, "拖动完成", Toast.LENGTH_SHORT).show();
//                }
//            });

        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//            Toast.makeText(SystemVideoPlayer.this, "播放出错了哦", Toast.LENGTH_SHORT).show();
            //1.播放的视频格式不支持--跳转到万能播放器继续播放
            startVitamioPlayer();
            //2.播放网络视频的时候，网络中断---1.如果网络确实断了，可以提示用于网络断了；2.网络断断续续的，重新播放
            //3.播放的时候本地文件中间有空白---下载做完成
            return true;
        }
    }
    /**
     * a,把数据按照原样传入VtaimoVideoPlayer播放器
     * b,关闭系统播放器
     */
    private void startVitamioPlayer() {

        if(videoView != null){
            videoView.stopPlayback();
        }

        Intent intent = new Intent(this,VitamioVideoPlayer.class);
        if(mediaItems != null && mediaItems.size() > 0){

            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);

        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);

        finish();//关闭页面
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