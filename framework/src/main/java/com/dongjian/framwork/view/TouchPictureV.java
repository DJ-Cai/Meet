package com.dongjian.framwork.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import net.dongjian.framework.R;

/**
 * 登录页---自定义View - - - 图片验证View
 * 1、View的背景
 * 2、View的空白块
 * 3、View的区域截图
 * 4、View的触摸事件
 * 5、View的接口：将结果通知外部，进行处理
 */
public class TouchPictureV extends View {
    //背景---即待滑动验证的图片
    private Bitmap bgBitmap;
    //背景画笔
    private Paint mPaintbg;

    //空白块
    private Bitmap mNullBitmap;
    //空白块画笔
    private  Paint mPaintNull;

    //移动方块
    private Bitmap mMoveBitmap;
    //移动画笔
    private Paint mPaintMove;

    //View的宽高---bitmap背景的时候，想和这里的数据一样
    private int mWidth;
    private int mHeight;

    public TouchPictureV(Context context) {
        super(context);
        init();
    }

    public TouchPictureV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPictureV(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintbg = new  Paint();
        mPaintNull = new Paint();
        mPaintMove = new Paint();
    }

    /**
     * 获取View的宽高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1、绘制背景
        drawBg(canvas);
        //2、绘制空白方块
        dragNullCard(canvas);
    }

    /**
     * 绘制空白快---比较简单，没有移动，固定在一个地方没有改变
     * @param canvas
     */
    private void dragNullCard(Canvas canvas) {
        //1、先获取图片
    }

    /**
     * 绘制背景
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        //1、获取图片
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        //2、创建一个空的Bitmap作为背景， （在这里 背景的宽高和View的宽高一样,即mWidth 和 mHeight）
        bgBitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        //3、将图片绘制到空的Bitmap，
        //  3-1、做个画布
        Canvas bgCanvas = new Canvas(bgBitmap);
        //  3-2、画布上放入图片，矩形大小、画笔
        bgCanvas.drawBitmap(mBitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);
        //4、将bgBitmap绘制到View上（和第三步类似，只是将bitmap的canvas换成view的canvas）
        canvas.drawBitmap(bgBitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);
    }


}
