package com.whn.whn.whn_qqmusic.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Audio.Media;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.whn.whn.whn_qqmusic.R;
import com.whn.whn.whn_qqmusic.activity.MusicPlayerActivity;
import com.whn.whn.whn_qqmusic.adapter.MusicAdapter;
import com.whn.whn.whn_qqmusic.bean.MusicItem;
import com.whn.whn.whn_qqmusic.db.MyAsyncQueryHandler;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by whn on 2017/1/15.
 */

public class LocalFragment extends BaseFragment {
    @InjectView(R.id.lv_music)
    ListView lv_music;
    private MusicAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_vbang;
    }

    @Override
    protected void initView(View view) {
        adapter = new MusicAdapter(getContext(), null);
        lv_music.setAdapter(adapter);
        lv_music.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建集合用来保存所有的音乐
                ArrayList<MusicItem> musics = new ArrayList<MusicItem>();
                //通过适配器获取一个游标
                Cursor cursor = adapter.getCursor();
                //如果游标不为空
                if (cursor != null) {
                    //移动到所有数据的最前面
                    cursor.moveToPosition(-1);
                    //遍历所有数据
                    while (cursor.moveToNext()) {
                        //把游标转换成javabean 放到集合中
                        musics.add(MusicItem.getMusicFromCursor(cursor));
                    }
                    //通过intent把所有的音乐对应的集合 以及当前点击的位置传递给下一个界面
                    Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
                    intent.putExtra("musics", musics);
                    intent.putExtra("position", position);
                    intent.putExtra("fromNotification",false);
                    startActivity(intent);
                }
            }
        });
        //判断如果api版本>=23 需要动态获取权限
        if (Build.VERSION.SDK_INT >= 23) {
            //①checkSelfPermission 检查当前应用是否有特定权限 第一个参数 activity
            //第二个参数 需要检测的权限 Manifest.permission.XXXXX 需要注意 只有危险权限需要动态申请
            //危险权限指 跟用户隐私相关的权限 sd卡 短信 通话记录 打电话权限 位置权限 摄像头权限 录音机权限
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PermissionChecker.PERMISSION_DENIED) {//判读是否有SD卡读取权限
//               ② 如果没有权限动态申请权限
                //动态申请权限 第二个参数 需要申请的权限的数据 可以一次申请多个权限
                //第三个参数 请求码 需要注意不能小于0
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        initData();
    }

    public void initData() {
        //内容解析者 访问内容提供者
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = Media.EXTERNAL_CONTENT_URI;

//        Cursor cursor = contentResolver.query(uri, new String[]{Media._ID,Media.DATA,//文件路径
//                        Media.DURATION,//媒体文件时长
//                        Media.SIZE,//歌曲文件的大小
//                        Media.TITLE,//歌曲文件的标题
//                        Media.ARTIST}//艺术家
//                , null, null, null);
//        //打印数据
//        Utils.printCursor(cursor);
//        adapter.changeCursor(cursor);
        //开启异步查询
        MyAsyncQueryHandler queryHandler = new MyAsyncQueryHandler(contentResolver);
        //异步查询 第一个参数token 如果有多个不同查询 用来区分不同查询
        //第二个参数 cookie 可以传任意对象给 onqueryComplete方法 这里传入adapter在onqueryComplete方法中刷新界面
        queryHandler.startQuery(1, adapter, uri, new String[]{Media._ID, Media.DATA,//文件路径
                        Media.DURATION,//媒体文件时长
                        Media.SIZE,//歌曲文件的大小
                        Media.TITLE,//歌曲文件的标题
                        Media.ARTIST,//艺术家
                        Media.DISPLAY_NAME}//文件名字
                , null, null, null);
    }


}
