package com.whn.whn.whn_qqmusic.adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.whn.whn.whn_qqmusic.R;


/**
 * Created by whn on 2017/1/15.
 */

public class MusicAdapter extends CursorAdapter {
    // 最后一个参数是boolean类型的 构造 不推荐使用
    //如果最后一个参数传入true 当Cursor的内容发生改变的时候会自动调用 requery方法 刷新界面
//    public MusicAdapter(Context context, Cursor c, boolean autoRequery) {
//        super(context, c, autoRequery);
//    }

    public MusicAdapter(Context context, Cursor c) {
        //第三个参数FLAG_REGISTER_CONTENT_OBSERVER 会通过内容观察者处理游标变化的操作
        super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //创建新的布局通过newView
        View view = LayoutInflater.from(context).inflate(R.layout.item_music, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //给条目设置数据 通过这个bindView
        ViewHolder holder = (ViewHolder) view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        holder.tv_title.setText(title);
        holder.tv_artist.setText(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        String size = Formatter.formatFileSize(context, cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
        if(title.contains("-")){
            holder.tv_artist.setText(title.split("-")[0]);
            holder.tv_title.setText(title.split("-")[1]);
        }
        holder.tv_size.setText(size);
    }

    class ViewHolder {
        TextView tv_title;
        TextView tv_artist;
        TextView tv_size;

        public ViewHolder(View view) {
            tv_artist = (TextView) view.findViewById(R.id.tv_artist);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
        }
    }
}
