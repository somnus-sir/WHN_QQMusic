package com.whn.whn.whn_qqmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.whn.whn.whn_qqmusic.activity.MusicPlayerActivity;
import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.bean.MusicItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by whn on 2017/1/15.
 */

public class MusicPlayerService extends Service {

    private static final int OPEN_ACTIVITY = 3;
    private static final int CANCEL_NOTIFICATION = 4;
    private static final int PLAY_PAUSE = 5;
    private MediaPlayer mMediaplayer;
    private int currentPosition = -1;
    private ArrayList<MusicItem> musics;
    public static final int PLAY_MODE_LIST = 0;
    public static final int PLAY_MODE_SINGLE = 1;
    public static final int PLAY_MODE_SHUFFLE = 2;
    public static final int PLAY_PRE = 38;
    public static final int PLAY_NEXT = 836;
    /**
     * 记录当前的播放模式
     */
    public static int currentMode = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicController();
    }

    public class MusicController extends Binder {
        /**
         * 根据当前的播放状态控制音乐的播放和暂停
         */
        public void playPause() {
            MusicPlayerService.this.playPause();
        }

        /**
         * 获取当前正在播放的音乐的Javabean
         *
         * @return
         */
        public MusicItem getCurrentMusic() {
            return musics.get(currentPosition);
        }

        /**
         * 获取当前的播放状态
         *
         * @return true 说明正在播放
         */
        public boolean isPlaying() {
            return mMediaplayer.isPlaying();
        }

        /**
         * 获取当前播放了多久
         */
        public int getCurrentPosition() {
            return mMediaplayer.getCurrentPosition();
        }

        public void seekTo(int position) {
            mMediaplayer.seekTo(position);
        }

        public void preNext(int mode) {
            MusicPlayerService.this.preNext(mode);
        }

        public int getDuration() {
            return mMediaplayer.getDuration();
        }
    }

    private void stopUpdateUI() {
        sendBroadcast(new Intent("com.whn.stopPlay"));
    }

    /**
     * 根据当前的播放状态控制音乐的播放和暂停
     */
    public void playPause() {
        if (mMediaplayer.isPlaying()) {
            //如果处于播放状态就暂停
            mMediaplayer.pause();
            stopUpdateUI();
        } else {
            //如果处于暂停状态 就开始播放
            mMediaplayer.start();
            notifyPlayerUPdateUI();
        }
    }


    /**
     * 切换歌曲---就是根据当前的播放模式来修改 currentposition
     */
    public void preNext(int mode) {

        switch (currentMode) {
            case PLAY_MODE_LIST:
                sendBroadcast(new Intent("com.whn.changeBG"));//更改背景
                if (mode == PLAY_PRE) {
                    //如果是第一首 移动到列表的最后一首继续播
                    currentPosition = currentPosition == 0 ? musics.size() - 1 : --currentPosition;
                } else if (mode == PLAY_NEXT) {
                    currentPosition = (++currentPosition) % musics.size();
                }
                break;

            case PLAY_MODE_SHUFFLE:
                sendBroadcast(new Intent("com.whn.changeBG"));//更改背景
                Random random = new Random();
                int temp = random.nextInt(musics.size());
                while (temp == currentPosition) {
                    temp = random.nextInt(musics.size());
                }
                currentPosition = temp;
                break;

            case PLAY_MODE_SINGLE:
                break;
        }
        //重新播放音乐
        startplay();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //从sp中获取缓存的playmode
        currentMode = getSharedPreferences("music_config", MODE_PRIVATE).getInt("playmode", 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //区分这个意图是从通知中来还是从上一个activity中来
        boolean fromNotification = intent.getBooleanExtra("fromNotification", false);
        if (fromNotification) {
            //说明这个意图是通知发出来的
            int operation = intent.getIntExtra("operation", 0);
            switch (operation) {
                case PLAY_NEXT:
                    preNext(PLAY_NEXT);
                    break;
                case PLAY_PRE:
                    preNext(PLAY_PRE);
                    break;
                case OPEN_ACTIVITY:
                    notifyPlayerUPdateUI();
                    break;
                case CANCEL_NOTIFICATION:
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    //消除通知
                    manager.cancel(1);
                    break;
                case PLAY_PAUSE:
                    playPause();
                    //更新通知
                    sendCustomNotification();
                    break;
            }

        } else {
            //数据从activity中传递 获取到所有音乐的信息
            musics = (ArrayList<MusicItem>) intent.getSerializableExtra("musics");
            //获取到了当前点击的条目位置
            int temp = intent.getIntExtra("position", 0);

            if (temp == currentPosition) {
                //如果点击的条目 跟当前的音乐是同一首 不做音乐播放处理
                // 通知更新界面
                notifyPlayerUPdateUI();
            } else {
                //如果点击的条目 跟当前的音乐不是一首 再重置处理
                currentPosition = temp;
                //开始准备mediaPlayer
                startplay();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 开始,或者重新播放音乐
     */
    private void startplay() {
        Log.e(getClass().getSimpleName(), "curretn====" + currentPosition);
        if (mMediaplayer == null) {
            //说明服务第一次创建 需要创建新的mediaplayer对象
            mMediaplayer = new MediaPlayer();
            mMediaplayer.setOnPreparedListener(new MyOnPreparedListener());
            //添加播放结束的监听
            mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //音乐播放结束 自动播放下一首
                    preNext(PLAY_NEXT);
                }
            });
            mMediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
        } else {
            //说明是切换歌曲的操作
            //先停止更新ui
            stopUpdateUI();
            //重置mediaplayer
            mMediaplayer.reset();
        }
        try {
            //设置要播放的音乐路径
            mMediaplayer.setDataSource(musics.get(currentPosition).data);
            //异步准备 准备好了之后会走setOnPreparedListener  MyOnPreparedListener 的 onPrepared
            mMediaplayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //准备好之后音乐就开始播放了
            mMediaplayer.start();
            //通知activity更新界面
            notifyPlayerUPdateUI();
            sendCustomNotification();
        }
    }

    private void notifyPlayerUPdateUI() {
        //发送广播 通知activity 音乐已经开始播放了 可以更新UI
        sendBroadcast(new Intent("com.whn.startPlay"));
    }

    private void sendNormalNotification() {
//        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.music_default_bg);
        builder.setContentTitle(musics.get(currentPosition).title);
        builder.setContentText(musics.get(currentPosition).artist);
        builder.setContentInfo(musics.get(currentPosition).displayName);
        //说明是正在进行的通知 不能让用户通过手动操作消除通知
        // builder.setOngoing(true); //音乐播放器 ongoing
        //点击之后触发一个pendingIntent 然后通知会自动消失 一般促销信息/新闻的推送 一般都是autoCancel
        // builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //通过notify方法来发送通知到通知栏 第一个参数id用来区分不同的通知
        manager.notify(1, notification);
        //id的作用在消除通知的时候可以使用这个id找到对应的通知
        //manager.cancel(1); cancel方法可以消除通知栏中的通知
    }

    /**
     * 自定义通知栏
     */
    private void sendCustomNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.music_default_bg);
        builder.setContent(getRemoteViews());
        if (Build.VERSION.SDK_INT >= 16) {//android4.1之后才支持的
            builder.setCustomBigContentView(getBigRemoteViews());
        }
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //通过notify方法来发送通知到通知栏 第一个参数id用来区分不同的通知
        manager.notify(1, notification);

    }

    /**
     * 获取大View的方法
     */
    private RemoteViews getBigRemoteViews() {
        //构造第一个参数 包名 第二个参数 布局的资源id
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_big);
        //给remoteViews设置textview的内容
        remoteViews.setTextViewText(R.id.tv_notification_title, musics.get(currentPosition).title);
        remoteViews.setTextViewText(R.id.tv_notification_artist, musics.get(currentPosition).artist);

        if (mMediaplayer.isPlaying()) {
            //给remoteViews设置imageview的资源
            remoteViews.setImageViewResource(R.id.iv_notification_playPause, R.drawable.selector_play);
        } else {
            remoteViews.setImageViewResource(R.id.iv_notification_playPause, R.drawable.selector_pause);
        }


        //给remoteviews设置点击事件
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_pre, getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, getNextPendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.rl_notification, getActivityPendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_playPause, getPlayPausePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_cancel, getCancelPendingIntent());
        return remoteViews;
    }

    private PendingIntent getCancelPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("operation", CANCEL_NOTIFICATION);
        //getService 相当于执行startSerivce 在service的onstarCommand中可以收到这个意图
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent getPlayPausePendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("operation", PLAY_PAUSE);
        //getService 相当于执行startSerivce 在service的onstarCommand中可以收到这个意图
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    /**
     * 获取小View并设置点击事件
     * @return
     */
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_nomal);
        remoteViews.setTextViewText(R.id.tv_notification_title, musics.get(currentPosition).title);
        remoteViews.setTextViewText(R.id.tv_notification_artist, musics.get(currentPosition).artist);

        remoteViews.setOnClickPendingIntent(R.id.iv_notification_pre, getPrePendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next, getNextPendingIntent());
        remoteViews.setOnClickPendingIntent(R.id.rl_notification, getActivityPendingIntent());
        return remoteViews;
    }

    private PendingIntent getActivityPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("operation", OPEN_ACTIVITY);
        //getService 相当于执行startSerivce 在service的onstarCommand中可以收到这个意图
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        PendingIntent.getBroadcast();
        return pendingIntent;
    }

    /**
     * 后一首,点击事件
     */
    private PendingIntent getNextPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("operation", PLAY_NEXT);
        //getService 相当于执行startSerivce 在service的onstarCommand中可以收到这个意图
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * 前一首,点击事件
     */
    private PendingIntent getPrePendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MusicPlayerService.class);
        intent.putExtra("fromNotification", true);
        intent.putExtra("operation", PLAY_PRE);
        //getService 相当于执行startSerivce 在service的onstarCommand中可以收到这个意图
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
