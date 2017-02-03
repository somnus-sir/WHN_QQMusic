package com.whn.whn.whn_qqmusic.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by fullcircle on 2017/1/15.
 */

public class MusicItem implements Serializable{
    public String data;
    public String title;
    public String artist;
    public long size;
    public int duration;
    public String displayName;


    /**
     * 传入游标返回一个音乐bean
     * @param cursor
     * @return
     */
    public static MusicItem getMusicFromCursor(Cursor cursor){
        if(cursor == null){
            return null;
        }
        MusicItem musicItem = new MusicItem();
        musicItem.data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        musicItem.title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        musicItem.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        musicItem.size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
        musicItem.duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

        if(musicItem.title.contains("-")){
            musicItem.artist = musicItem.title.split("-")[0].trim();
            musicItem.title = musicItem.title.split("-")[1].trim();
        }
        musicItem.displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

        return  musicItem;
    }

}
