package com.fivetrue.gimpo.ac05.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.fivetrue.gimpo.ac05.R;
import com.fivetrue.gimpo.ac05.utils.Log;


/**
 * Created by Fivetrue on 2016-02-09.
 */

public class DrawerButton extends View {

    private final static String TAG = DrawerButton.class.getSimpleName();
    private final static int INVALID_VALUE = -1;
    private final static int MOVING_VALUE = 150;
    private final static int CIRCLE_HALF_DEGREE = 180;

    private float mLineStroke = 1;
    private float mDensity = INVALID_VALUE;

    private boolean isHomeAsUp = false;
    private boolean isRevert = false;
    private float mRotationOffset = INVALID_VALUE;

    private Paint mPaint = null;
    private Path mPath = null;

    private float mPadding = 0;

    private int mLineCount = 0;

    private int mLineColor = Color.WHITE;

    public DrawerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initAttributeSet(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        if(mRotationOffset == INVALID_VALUE){
            mRotationOffset = 0;
        }
        mRotationOffset = isHomeAsUp ? 1 - mRotationOffset : mRotationOffset;

//        if(isHomeAsUp){
//            onDrawBack(canvas);
//        }else{
            onDrawHome(canvas);
//        }
    }

    private void initAttributeSet(Context context, AttributeSet attrs){
        mDensity = context.getResources().getDisplayMetrics().density;
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawerButton);
            mPadding = a.getDimension(R.styleable.DrawerButton_android_padding, 0);
            mLineColor = a.getColor(R.styleable.DrawerButton_lineColor, Color.WHITE);
            mLineCount = a.getInt(R.styleable.DrawerButton_lineCount, 3);
            mLineStroke = a.getDimension(R.styleable.DrawerButton_lineWidth, 1 * mDensity);
            a.recycle();
        }
    }

    private void initPaint(){

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mLineColor);
        mPaint.setStrokeWidth(mLineStroke);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    private void onDrawHome(Canvas canvas){
        if(mPath == null){
            mPath = new Path();
        }
        mPath.reset();
        float lineGap = (getHeight() - mPadding) / mLineCount;
        for(int i = 0 ; i < mLineCount ; i ++){
            float gap = (lineGap * i);
            float distanceY = (getHeight() / 2) - (mPadding + gap);
            float distanceX = 0;
            if(i == 0 || i == mLineCount - 1){
                distanceX =  getHeight() * 0.35f;
            }
            float movingY = distanceY * mRotationOffset;
            float movingX = distanceX * mRotationOffset;
            float startX = mPadding + movingX;
            float startY = mPadding + gap;
            float endX = getWidth() - mPadding;
            float endY = mPadding + gap + movingY;
            mPath.moveTo(startX, startY);
            mPath.lineTo(endX, endY);
        }
        canvas.drawPath(mPath, mPaint);
        float rotateValue = mRotationOffset * CIRCLE_HALF_DEGREE;
        rotateValue = rotateValue * (isRevert ? -1 : 1);
        setRotation(rotateValue);
    }

    private void onDrawBack(Canvas canvas){

        if(mPath == null){
            mPath = new Path();
        }
        mPath.reset();
        float lineGap = (getHeight() - mPadding) / mLineCount;
        for(int i = 0 ; i < mLineCount ; i ++){
            float gap = (lineGap * i);
            float distanceY = (getHeight() / 2) - (mPadding + gap);
            float distanceX = 0;
            if(i == 0 || i == mLineCount - 1){
                distanceX =  getHeight() * 0.35f;
            }
            float movingY = distanceY * mRotationOffset;
            float movingX = distanceX * mRotationOffset;
            float startX = mPadding + movingX;
            float startY = getHeight() / 2;
            float endX = getWidth() - mPadding - distanceX;
            float endY = mPadding + gap + movingY;
            mPath.moveTo(startX, startY);
            mPath.lineTo(endX, endY);
        }
        canvas.drawPath(mPath, mPaint);
        float rotateValue = mRotationOffset * CIRCLE_HALF_DEGREE;
        rotateValue = rotateValue * (isRevert ? -1 : 1);
        setRotation(rotateValue);
    }

    public void setHomeAsUp(boolean isHomeAsUp){
        this.isHomeAsUp = isHomeAsUp;
        mRotationOffset = 0;
        invalidate();
    }

    public boolean isHomeAsUp(){
        return isHomeAsUp;
    }

    public void setRotationOffset(float value){
        mRotationOffset = value;
        Log.i(TAG, "setRotationOffset: value = " + value);
        invalidate();
    }

    public void setRevert(boolean isRevert) {
        this.isRevert = isRevert;
    }

    public void setLineColorRes(int color){
        mLineColor = color;
        initPaint();
    }


}

