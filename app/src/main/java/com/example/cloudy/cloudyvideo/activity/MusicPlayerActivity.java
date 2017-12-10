package com.example.cloudy.cloudyvideo.activity;

import android.app.Activity;
import android.content.*;
import android.graphics.drawable.AnimationDrawable;
import android.os.*;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.*;

import com.example.cloudy.cloudyvideo.R;
import com.example.cloudy.cloudyvideo.service.IMusicPlayerService;
import com.example.cloudy.cloudyvideo.service.MusicPlayerService;
import com.example.cloudy.cloudyvideo.utils.LyricUtils;
import com.example.cloudy.cloudyvideo.utils.Utils;
import com.example.cloudy.cloudyvideo.view.ShowLyricView;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayerActivity extends Activity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnLyrc;
    private ShowLyricView showLyric;

    /**
     * 进度更新
     */
    private static final int PROGRESS = 1;
    /**
     * 显示歌词
     */
    private static final int SHOW_LYRIC = 2;
    private MyReceiver receiver;
    private IMusicPlayerService service;//服务的代理类，通过它可以调用服务的方法
    private int position;
    /**
     * true:从状态栏进入的，不需要重新播放
     * false:从播放列表进入的
     */
    private boolean notification;
    private Utils utils;
    private ArrayList<Object> mediaItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        bindAndStartService();
    }

    private void initData() {
        utils = new Utils();
        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 得到数据
     */
    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private ServiceConnection con = new ServiceConnection() {
        /**
         * 当连接成功的时候回调这个方法
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);
            if (service != null) {
                try {
                    if (!notification) {//从列表
                        service.openAudio(position);
                    } else {
                        //从状态栏
                        showViewData();
                        checkPlaymode();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 当断开连接的时候回调这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (service != null) {
                    service.stop();
                    service = null;
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        //1.得到当前进度
                        int currentPosition = service.getCurrentPosition();
                        //2.设置SeekBar.setProgress(进度)
                        seekbarAudio.setProgress(currentPosition);
                        //3.时间进度跟新
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));
                        //4.每秒更新一次
                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_LYRIC://显示歌词
                    //1.得到当前的进度
                    try {
                        int currentPosition = service.getCurrentPosition();
                        //2.把进度传入ShowLyric控件，并且计算该高亮哪一句
                        showLyric.setshowNextLyric(currentPosition);
                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private void showViewData() {
        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            //设置进度条的最大值
            seekbarAudio.setMax(service.getDuration());

            //发消息
            handler.sendEmptyMessage(PROGRESS);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("android.cloudy.action.OPRNMUSIC");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);//不至于实例化多个服务
    }

    private void findViews() {
        setContentView(R.layout.music_player);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivIcon.getBackground();
        animationDrawable.start();
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnLyrc = (Button) findViewById(R.id.btn_lyrc);
        showLyric = (ShowLyricView) findViewById(R.id.showLyric);

        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnLyrc.setOnClickListener(this);

        //设置视频的拖动
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                //拖动进度
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            setPlaymode();
        } else if (v == btnAudioPre) {
            if (service != null) {
                try {
                    service.pre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioStartPause) {
            if (service != null) {
                try {
                    if (service.isPlaying()) {
                        //暂停
                        service.pause();
                        //按钮-播放
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                    } else {
                        //播放
                        service.start();
                        //按钮-暂停
                        btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnAudioNext) {
            if (service != null) {
                try {
                    service.next();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == btnLyrc) {

        }
    }

    private void setPlaymode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NORMAL;
            }
            //保持
            service.setPlayMode(playmode);
            //设置图片
            showPlaymode();

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(MusicPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
                Toast.makeText(MusicPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
                Toast.makeText(MusicPlayerActivity.this, "全部循环", Toast.LENGTH_SHORT).show();
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
                Toast.makeText(MusicPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 校验状态
     */
    private void checkPlaymode() {
        try {
            int playmode = service.getPlayMode();

            if (playmode == MusicPlayerService.REPEAT_NORMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
            } else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }

            //校验播放和暂停的按钮
            if (service.isPlaying()) {
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
            } else {
                btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //发消息开始歌词同步
            showLyric();
            showViewData();
            checkPlaymode();
        }
    }

    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();
        try {
            String path = service.getAudioPath();//得到歌曲的绝对路径
            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/audio/beijingbeijing.lrc
            path = path.substring(0, path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if (!file.exists()) {
                file = new File(path + ".txt");
            }
            lyricUtils.readLyricFile(file);//解析歌词

            showLyric.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (lyricUtils.isExistsLyric()) {
            handler.sendEmptyMessage(SHOW_LYRIC);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        //取消注册广播
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        //解绑服务
        if (con != null) {
            unbindService(con);
            con = null;
        }
        super.onDestroy();
    }
}
