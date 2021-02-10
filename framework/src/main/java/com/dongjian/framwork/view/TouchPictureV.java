package com.dongjian.framwork.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
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

    //空白方块大小
    private int CARD_SIZE = 200;
    //空白方块坐标
    private int LINE_W, LINE_H = 0;

    //移动方块横坐标 (纵坐标和空白块保持一致)
    private int moveX = 200;
    //移动方块可允许的误差值
    private int errorValue = 20;

    private OnViewResultListener viewResultListener;

    public void setViewResultListener(OnViewResultListener viewResultListener){
        this.viewResultListener = viewResultListener;
    }

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

        //3、绘制移动的方块
        drawMoveCard(canvas);
    }

    /**
     * 1、绘制背景
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
        //  3-2、画布上放入图片，图片位置、画笔
        bgCanvas.drawBitmap(mBitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);
        //4、将bgBitmap绘制到View上（和第三步类似，只是将bitmap的canvas换成view的canvas）
        canvas.drawBitmap(bgBitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);
    }

    /**
     * 2、绘制空白快---比较简单，没有移动，固定在一个地方没有改变
     * @param canvas
     */
    private void dragNullCard(Canvas canvas) {
        //1、先获取图片
        mNullBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_null_card);
        //2、计算空白块的位置
        CARD_SIZE = mNullBitmap.getWidth(); //空白块大小
        LINE_W = mWidth / 3 * 2;   // 宽：空白块在View的宽第二段的位置
        LINE_H = mHeight / 2 - (CARD_SIZE / 2);      //高：让空白块处于二分高度中心
        //3、绘制
        canvas.drawBitmap(mNullBitmap,LINE_W,LINE_H,mPaintNull);
    }

    /**
     * 3、绘制移动的方块
     * @param canvas
     */
    private void drawMoveCard(Canvas canvas) {
        //1、截取空白块位置坐标上的图像---在相同坐标上截取相同大小的图像
        mMoveBitmap = Bitmap.createBitmap(bgBitmap,LINE_W,LINE_H,CARD_SIZE,CARD_SIZE);
        //2、绘制在View上
        canvas.drawBitmap(mMoveBitmap,moveX,LINE_H,mPaintMove);
    }

    /**
     * 3-2、移动方块的操作
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_MOVE:
                //更新移动方块的位置，要做防止越界操作
                if(event.getX() > 0 && event.getX() < (mWidth - CARD_SIZE)){
                    moveX = (int) event.getX();
                    //移动的时候判断位置是否正确（在误差允许的范围内）
                    if(moveX > (LINE_W - errorValue) && moveX < (LINE_W + errorValue)){
                        if(viewResultListener != null){
                            viewResultListener.onResult();
                        }
                    }
                    invalidate();
                }
                break;
        }
        return true;
    }


    public interface OnViewResultListener{
        void onResult();
    }
}
