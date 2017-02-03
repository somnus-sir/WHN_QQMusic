package com.whn.whn.whn_qqmusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by fullcircle on 2017/1/15.
 */

public class Utils {


    private static Toast toast;

    public static void showToast(Context context, String msg){
        if(toast == null){
        toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }else{
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 把游标中所有的数据进行打印
     * @param cursor
     */
    public static void printCursor(Cursor cursor){
        if(cursor == null){
            return;
        }else{
            //首先移动到第一行之前
            cursor.moveToPosition(-1);
            while(cursor.moveToNext()){
                //游标每移动一行 遍历这一行数据的所有列
                for(int i = 0;i<cursor.getColumnCount();i++){
                    Log.e("utils",cursor.getColumnName(i)+cursor.getString(i));
                }

            }
        }
    }

    /*
    格式化时间hh:mm:ss    不够一小时 返回 mm:ss
     */
    public static String formatPlayTime(int time){
        // hh:mm:ss    不够一小时 返回 mm:ss
        int hour = 60*60*1000;
        int minute = 60*1000;
        int second = 1000;

        int h = time/hour;
        int m = time%hour/minute;
        int s = time%minute/second;

        if(h>0){
          return String.format("%02d:%02d:%02d",h,m,s);
        }else{
            return String.format("%02d:%02d",m,s);
        }
    }
}
