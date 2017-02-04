package com.whn.whn.whn_qqmusic.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.bean.MusicItem;
import com.whn.whn.whn_qqmusic.lyric.LyricView;
import com.whn.whn.whn_qqmusic.service.MusicPlayerService;
import com.whn.whn.whn_qqmusic.utils.Utils;

import java.io.File;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by fullcircle on 2017/1/15.
 */
public class MusicPlayerActivity extends AppCompatActivity {
    private static final int UPDATA_PLAYED_TIME = 1;
    private static final int UPDATA_LYRIC = 2;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.iv_playmode)
    ImageView ivPlaymode;
    @InjectView(R.id.iv_pre)
    ImageView ivPre;
    @InjectView(R.id.iv_play_pause)
    ImageView ivPlayPause;
    @InjectView(R.id.iv_next)
    ImageView ivNext;
    @InjectView(R.id.iv_list)
    ImageView ivList;
    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.rl_top)
    RelativeLayout rlTop;
    @InjectView(R.id.sb_progress)
    SeekBar sbProgress;
    @InjectView(R.id.lyric)
    LyricView mLyricView;
    @InjectView(R.id.tv_time_left)
    TextView tvTimeLeft;
    @InjectView(R.id.ll_musicbg_musicplayer)
    LinearLayout llMusicbgMusicplayer;
    private MyServiceConnection serviceConnection;
    private MusicPlayerService.MusicController music;
    private String totalTime;
    private MyReceiver receiver;
    private int[] pics = new int[]{R.mipmap.bg1, R.mipmap.bg2, R.mipmap.bg3, R.mipmap.bg4};


    /**
     * handler循环
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_PLAYED_TIME:
                    updataPlayedTime();
                    break;
                case UPDATA_LYRIC:
                    updatalyric();
                    break;
            }
        }
    };
    private int oldi;
    private int newi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_musicplayer);
        ButterKnife.inject(this);

        initService();
        initView();

        //广播接受者,控制,更新歌词,更新播放时间
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter("com.whn.startPlay");
        filter.addAction("com.whn.stopPlay");
        filter.addAction("com.whn.changeBG");
        registerReceiver(receiver, filter);
    }

    /**
     * 更新歌词
     */
    private void updatalyric() {
        mLyricView.updateLyrics(music.getCurrentPosition(), music.getDuration());
        handler.sendEmptyMessageDelayed(UPDATA_LYRIC, 100);
    }

    private void updataPlayedTime() {
        //Log.e(getClass().getSimpleName(),"updataPlayedTime");
        //获取当前播放的位置
        int currentPosition = music.getCurrentPosition();
        String time = Utils.formatPlayTime(currentPosition);
        //显示到textview上
        tvTimeLeft.setText(time);
        tvTime.setText(totalTime);
        //跟新进度条进度
        sbProgress.setProgress(currentPosition);
        //通过Hanlder通知隔一段时间再执行这个方法
        //半秒钟之后 再次执行这个方法
        handler.sendEmptyMessageDelayed(UPDATA_PLAYED_TIME, 500);
    }

    private void initService() {
        Intent intent = getIntent();
        intent.setClass(getApplicationContext(), MusicPlayerService.class);
        startService(intent);//会执行 onCreate onStartCommand(会多次执行)
        //混合方式开启服务
        serviceConnection = new MyServiceConnection();
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    /**
     * 初始化view
     */
    private void initView() {
        changeBackground();
        //progress监听
        sbProgress.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    /**
     * 改变背景
     */
    public void changeBackground(){
        Random random = new Random();
        newi = random.nextInt(4);
        while(newi==oldi){
            newi = random.nextInt(4);
        }
        oldi = newi;
        llMusicbgMusicplayer.setBackgroundResource(pics[newi]);
    }


    @OnClick({R.id.iv_playmode, R.id.iv_pre, R.id.iv_play_pause, R.id.iv_next, R.id.iv_list, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_playmode:
                MusicPlayerService.currentMode = ++MusicPlayerService.currentMode % 3;
                updatePlayModeIcon();
                getSharedPreferences("music_config", MODE_PRIVATE).edit().
                        putInt("playmode", MusicPlayerService.currentMode).commit();
                break;
            case R.id.iv_pre:
                music.preNext(MusicPlayerService.PLAY_PRE);
                break;
            case R.id.iv_play_pause:
                music.playPause();
                //更新播放的图标
                updatPlayPauseIcon();
                break;
            case R.id.iv_next:
                music.preNext(MusicPlayerService.PLAY_NEXT);
                break;
            case R.id.iv_list:
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 更新播放模式的图标
     */
    private void updatePlayModeIcon() {
        switch (MusicPlayerService.currentMode) {
            case MusicPlayerService.PLAY_MODE_LIST:
                ivPlaymode.setImageResource(R.drawable.selector_playmode_list);
                break;
            case MusicPlayerService.PLAY_MODE_SINGLE:
                ivPlaymode.setImageResource(R.drawable.selector_playmode_single);
                break;
            case MusicPlayerService.PLAY_MODE_SHUFFLE:
                ivPlaymode.setImageResource(R.drawable.selector_playmode_shuffle);
                break;
        }

    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //当进度发生改变的时候会走这个方法
            if (fromUser) {
                //如果是用户手动修改的进度在设置到mediaplayer上
                music.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //开始操作进度条 走这个方法
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //停止操作进度条走这个方法
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //bindService开启服务 如果onbind方法有返回值 就会执行这个方法
            //第二个参数IBinder service 就是 onbind返回值
            music = (MusicPlayerService.MusicController) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (music != null) {
            handler.sendEmptyMessage(UPDATA_PLAYED_TIME);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //bindservice开启的服务需要在activity销毁的时候 解除绑定
        unbindService(serviceConnection);
        //注销广播接收者
        unregisterReceiver(receiver);
    }


    /**
     * 广播接受者
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("com.whn.startPlay".equals(action)) {
                initPlayerUI();
                updataPlayedTime();
                updatePlayModeIcon();
                //加载歌词文件 解析   ---   必须将displayName,和歌词统一或者包含关系!
                String fileName = music.getCurrentMusic().displayName.split("\\.")[0];
                // Toast.makeText(context, fileName, Toast.LENGTH_SHORT).show();
                if (fileName.contains("三生三世")) {
                    fileName = "sanshengsanshi";
                }
                File file = new File(Environment.getExternalStorageDirectory(), fileName + ".txt");
                mLyricView.loadLyrics(file);
                //更新歌词
                updatalyric();
            } else if ("com.whn.stopPlay".equals(action)) {
                handler.removeMessages(UPDATA_PLAYED_TIME);
                //移除所有的消息
                handler.removeCallbacksAndMessages(null);
            }else if("com.whn.changeBG".equals(action)){
                changeBackground();//更换歌曲接受广播更换背景
            }
        }
    }

    /**
     * 初始化播放器界面
     */
    private void initPlayerUI() {
        MusicItem currentMusic = music.getCurrentMusic();
        //设置标题 艺术家
        tvTitle.setText(currentMusic.title);
        //更新播放的图标
        updatPlayPauseIcon();
        //更新播放的总时长
        totalTime = Utils.formatPlayTime(currentMusic.duration);
        tvTime.setText("00:00/" + totalTime);
        //初始化 seekbar的总时长
        sbProgress.setMax(currentMusic.duration);

    }

    /**
     * 更新播放,暂停图标
     */
    private void updatPlayPauseIcon() {
        if (music.isPlaying()) {
            ivPlayPause.setImageResource(R.drawable.selector_play);
        } else {
            ivPlayPause.setImageResource(R.drawable.selector_pause);
        }
    }
}
