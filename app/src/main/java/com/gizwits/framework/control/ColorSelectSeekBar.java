package com.gizwits.framework.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by water.zhou on 7/28/2015.
 */
public class ColorSelectSeekBar extends SeekBar
{
    public float currentX = 40;
    public float currentY = 50;
    public int color_imageView;

    private Paint mFillPaint;
    private int mWidth;
    private int mHeight;
    private final int[] mColors;
    Rect rect_color;

    Paint p = new Paint();

    public ColorSelectSeekBar(Context context , AttributeSet set)
    {
        super(context ,set);
        mColors = new int[] {0xffff0000,0xffffff00,0xff00ff00,0xff00ffff,0xff0000ff,0xffff00ff,0xffff0000};
        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(rect_color, mFillPaint);
        super.onDraw(canvas);

    }
    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        mWidth = this.getWidth();
        mHeight = this.getHeight();

        Shader s = new LinearGradient(0, 0, mWidth, mHeight, mColors, null, Shader.TileMode.REPEAT);
        mFillPaint.setShader(s);

        rect_color = new Rect(0,0,mWidth, mHeight);
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
