package com.whn.whn.whn_qqmusic.lyric;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.TextView;

import com.whn.whn.whn_qqmusic.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by fullcircle on 2017/1/16.
 */

public class LyricView extends TextView {


    private int viewHeight;
    private int viewWidth;
    /**
     * 正在演唱的歌词文字大小
     */
    private float bigFontSize;
    /**
     * 普通的歌词文字大小
     */
    private float normalFontSize;
    /**
     * 每一行的行高
     */
    private float lineHeight;
    private int heightLightColor;
    private ArrayList<Lyric> lyrics = null;
    /**
     * 正在演唱的歌词 在歌词集合中的索引
     */
    private int currentIndex = 5;
    private Paint paint;
    private Rect bounds;
    private float centerY;
    private int currentTime;
    private int duration;
    private float passedPercent;

    public LyricView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams();
    }

    private void initParams() {
        bigFontSize = getResources().getDimension(R.dimen.bigFontSize);
        normalFontSize = getResources().getDimension(R.dimen.nomalFontSize);
        lineHeight = getResources().getDimension(R.dimen.lineHeight);

        heightLightColor = getResources().getColor(R.color.colorMusicProgress);
//        lyrics = new ArrayList<>();
//        for(int i = 0;i<50;i++){
//            lyrics.add(new Lyric("我是歌词歌词"+i,2000*i));
//        }

        //初始化画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        //矩形对象 用来测量文字的边界
        bounds = new Rect();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LyricView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(lyrics!=null&&lyrics.size()>0){
            canvas.translate(0,-getTransY());
            drawAllText(canvas);
        }
//        String text = "我是一行歌词歌词";
//
//        paint.setColor(heightLightColor);
//        paint.setTextSize(bigFontSize);
//
//        paint.getTextBounds(text,0,text.length(),bounds);
//        //getTextBounds执行之后 文字的宽度和高度就保存到了Rect bounds对象中
//        int textWidth= bounds.width();
//        int textHeight = bounds.height();
//
//        float x = viewWidth/2-textWidth/2;
//        float centerY = viewHeight/2+textHeight/2;
//        canvas.drawText(text,x,centerY,paint);
////
//        text = "我是上一行歌词";
//        paint.setTextSize(normalFontSize);
//        paint.setColor(Color.WHITE);
//        paint.getTextBounds(text,0,text.length(),bounds);
//        textWidth= bounds.width();
//         textHeight = bounds.height();
//        x = viewWidth/2-textWidth/2;
//        //在中间行y坐标的基础上 - 一倍行高
//        float y = centerY-lineHeight;
//        canvas.drawText(text,x,y,paint);
//
//        text = "我是下一行歌词";
//        paint.getTextBounds(text,0,text.length(),bounds);
//        textWidth= bounds.width();
//        x = viewWidth/2-textWidth/2;
//        //在中间行y坐标的基础上 - 一倍行高
//         y = centerY+lineHeight;
//        canvas.drawText(text,x,y,paint);

    }

    private float getTransY() {
        if(currentIndex == lyrics.size()-1){
//            最后一句
            //当前歌词开始演唱的时刻
            int startTime = lyrics.get(currentIndex).time;
            //这一行应该演唱的总时长 用歌曲的总时长-最后一行歌词开始唱的时刻
            int totalTime  = duration-startTime;
            //这一行歌词已经演唱了多久
            int passedTime = currentTime-startTime;
            //当前时刻应该移动的距离
            float distance = lineHeight*passedTime/totalTime;
            return distance;

        }

        //当前歌词开始演唱的时刻
        int startTime = lyrics.get(currentIndex).time;
        //这一行应该演唱的总时长
        int totalTime  = lyrics.get(currentIndex+1).time-startTime;
        //这一行歌词已经演唱了多久
        int passedTime = currentTime-startTime;
        passedPercent = passedTime/(float)totalTime;
        //当前时刻应该移动的距离
        float distance = lineHeight*passedTime/totalTime;
        return distance;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取到view的宽度和高度
        viewHeight = h;
        viewWidth = w;

        String text = "我是一行歌词歌词";
        paint.setTextSize(bigFontSize);
        paint.getTextBounds(text,0,text.length(),bounds);
        //确定屏幕中间的Y坐标的位置
        centerY = viewHeight/2+bounds.height()/2;
    }

    /**
     *根据当前歌词的索引 以及正在演唱的歌词索引(currentPosition)来绘制一行歌词
     * @param index 当前歌词在集合中的索引
     * @param canvas
     */
    private void drawSingleLyric(int index,Canvas canvas){
        //通过索引获取到这一行歌词
      String text  = lyrics.get(index).text;
        //判断当前的索引和正在演唱的歌词索引是否相同
        if(index == currentIndex){
           //正在演唱的歌词
            paint.setTextSize(bigFontSize);
            paint.setColor(heightLightColor);
            paint.getTextBounds(text,0,text.length(),bounds);
            int textWidth = bounds.width();
            float x = viewWidth/2-textWidth/2;
            paint.setShader(new LinearGradient(x,centerY,x+textWidth,centerY,
                    new int[]{heightLightColor,Color.WHITE},
                    new float[]{passedPercent,passedPercent+0.01f},Shader.TileMode.CLAMP));
        }else{
            paint.setTextSize(normalFontSize);
            paint.setColor(Color.WHITE);
            paint.setShader(null);
        }
        //测量文字的边界
        paint.getTextBounds(text,0,text.length(),bounds);
        int textWidth = bounds.width();
        float x = viewWidth/2-textWidth/2;
        //y坐标 中间位置 + 当前行和正在唱的行的行号差距* 行高
        float y = centerY+(index-currentIndex)*lineHeight;
        canvas.drawText(text,x,y,paint);
    }

    /**
     * 遍历结合绘制集合中的所有歌词
     * @param canvas
     */
    private void drawAllText(Canvas canvas){
        for(int i = 0;i<lyrics.size();i++){
            drawSingleLyric(i,canvas);
        }
    }

    /**
     * 根据当前正在演唱的时刻 更新正在唱的歌词索引
     * @param currentTime
     */
    private void updateCurrentIndex(int currentTime){
        for(int i = 0;i<lyrics.size();i++){
            if(i == lyrics.size()-1){
                currentIndex = i;
                return;
            }
            //当前行的时刻小于正在播放的时刻 并且 下一行的时刻大于正在播放的时刻
            if(lyrics.get(i).time<currentTime&& lyrics.get(i+1).time>currentTime){
                //这行歌词正在演唱
                currentIndex = i;
                return;
            }
        }
    }

    /**
     * 更新歌词
     * @param currentTime
     * @param duration
     */
    public void updateLyrics(int currentTime,int duration){
        this.duration = duration;
        this.currentTime = currentTime;
        //更新正在唱的歌词索引
        updateCurrentIndex(currentTime);
        //重新绘制界面
        invalidate();
    }


    public void loadLyrics(File file){
        //解析歌词保存到 lyrics这个集合中
        lyrics =LyricsParser.parserFromFile(file);
    }
}
