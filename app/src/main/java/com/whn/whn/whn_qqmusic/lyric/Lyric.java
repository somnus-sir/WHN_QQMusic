package com.whn.whn.whn_qqmusic.lyric;

/**
 * Created by fullcircle on 2017/1/16.
 */

public class Lyric implements Comparable<Lyric>{
    /**
     * 一行歌词的文字
     */
    public String text;
    /**
     * 当前行歌词开始演唱的时刻
     */
    public int time;

    public Lyric(String text, int time) {
        this.text = text;
        this.time = time;
    }

    @Override
    public int compareTo(Lyric o) {
        return this.time-o.time;
    }
}
