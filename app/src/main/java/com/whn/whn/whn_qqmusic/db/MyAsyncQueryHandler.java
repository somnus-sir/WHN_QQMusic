package com.whn.whn.whn_qqmusic.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;

import com.whn.whn.whn_qqmusic.adapter.MusicAdapter;


/**
 * Created by fullcircle on 2017/1/15.
 */

public class MyAsyncQueryHandler extends AsyncQueryHandler {
    public MyAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        MusicAdapter adapter = (MusicAdapter) cookie;
        //替换旧的游标 如果旧的游标不为空会关闭
        adapter.changeCursor(cursor);
        //替换旧的游标 把旧的游标作为返回值返回 不会关闭
        adapter.swapCursor(cursor);
    }
}
