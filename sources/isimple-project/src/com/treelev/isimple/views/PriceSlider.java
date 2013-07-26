package com.treelev.isimple.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


public class PriceSlider extends ImageView {

    public enum Region{Moscow("Москва"), MoscowRegion("Подмосковье"), StPetersburg("Санкт-Петербург");

        private String value;

        Region(String value) {
            this.value = value;
        }

        public static Region fromString(String strValue) {
            if (strValue != null) {
                for (Region b : Region.values()) {
                    if (strValue.equalsIgnoreCase(b.value)) {
                        return b;
                    }
                }
            }
            return null;
        }

    }

    private static final int COLOR_FOREGROUND = Color.parseColor("#ea168c");
    private static final int COLOR_BACKGROUND = Color.GRAY;
    private static final int COLOR_INNER_CIRCLE = Color.WHITE;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mArcRadius;
    private float mCupRadius;
    private float mInnerCupRadius;
    private float mHalfHeightLine;
    private float mPadding;
    private float mPaddingText;
    private final float mSizeText = 12.0f;
    private Region mRegion;

    private double mAbsoluteMinValue = 0.0f;
    private double mAbsMaxValue;
    private double mMaxNormalized;
    private double mMinNormalized;
    private double mFirstBorderNormalized;
    private double mSecondBorderNormalized;
    private double mAbsFirstBorder;
    private double mAbsSecondBorder;

    private double mNormalizedValue = 0d;
    private double mAbsValue;

    public PriceSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSizeText();
        mRegion = Region.Moscow;
        initMoscow();
    }

    private void initSizeText(){
        float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
        final float scaledPx = mSizeText * densityMultiplier;
        mPaint.setTextSize(scaledPx);
    }

    public void setRegion(Region region) {
        mRegion = region;
        switch (mRegion){
            case Moscow:
                initMoscow();
                break;
            case MoscowRegion:
                initMoscowRegion();
                break;
            case StPetersburg:
                initStPetersburg();
                break;
            default:
                Log.v(this.toString(), "Error unknown region" );
        }
        setVisibleSlider();
        invalidate();
    }

    private void initMoscow(){
        mAbsMaxValue = 5000d;
        mAbsFirstBorder = 2000d;
        mAbsSecondBorder = 2500d;
        setValueMoscow();
    }

    private void initMoscowRegion(){
        mAbsMaxValue = 10000d;
        mAbsFirstBorder = 2000d;
        mAbsSecondBorder = 5000d;
        setValueMoscowRegion();
    }

    private void initStPetersburg(){
        mAbsMaxValue = 10000d;
        mAbsFirstBorder = 2500d;
        mAbsSecondBorder = 5000d;
        setValueStPetersburg();
    }

    private void initBorders(){
        mMaxNormalized = valueToNormalized(mAbsMaxValue);
        mFirstBorderNormalized = valueToNormalized(mAbsFirstBorder);
        mSecondBorderNormalized = valueToNormalized(mAbsSecondBorder);
    }

    public void setValue(double value) {
        mAbsValue = value;
        switch (mRegion){
            case Moscow:
                setValueMoscow();
                break;
            case MoscowRegion:
                setValueMoscowRegion();
                break;
            case StPetersburg:
                setValueStPetersburg();
                break;
            default:
                Log.v(this.toString(), "Error unknown region" );
        }
        setVisibleSlider();
        invalidate();
    }

    private void setValueMoscow(){
        if(mAbsValue < 2000.0f){
            mAbsoluteMinValue = 0.0f;
        } else if( mAbsValue >= 2000.0f && mAbsValue < 2500.0f){
            mAbsoluteMinValue = 2000.0f;
        } else if(mAbsValue >= 2500 && mAbsValue < 5000 ){
            mAbsoluteMinValue = 2500.0f;
        }
        initMinBorderValue();
        initBorders();
    }

    private void setValueMoscowRegion(){
        if(mAbsValue < 2000.0f){
            mAbsoluteMinValue = 0.0f;
        } else if( mAbsValue >= 2000.0f && mAbsValue < 5000.0f){
            mAbsoluteMinValue = 2000.0f;
        } else if(mAbsValue >= 5000.0f && mAbsValue < 10000.0f ){
            mAbsoluteMinValue = 5000.0f;
        }
        initMinBorderValue();
        initBorders();
    }

    private void setValueStPetersburg(){
        if(mAbsValue < 2500.0f){
            mAbsoluteMinValue = 0.0f;
        } else if( mAbsValue >= 2500.0f && mAbsValue < 5000.0f){
            mAbsoluteMinValue = 2500.0f;
        } else if(mAbsValue >= 5000.0f && mAbsValue < 10000.0f ){
            mAbsoluteMinValue = 5000.0f;
        }
        initMinBorderValue();
        initBorders();
    }

    private void initMinBorderValue(){
        mMinNormalized = valueToNormalized(mAbsoluteMinValue);
        mNormalizedValue = valueToNormalized(mAbsValue);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initValues();
        switch (mRegion){
            case Moscow:
                drawSliderMoscow(canvas);
                break;
            case MoscowRegion:
                drawSliderMoscowRegion(canvas);
                break;
            case StPetersburg:
                drawSliderStPetersburg(canvas);
                break;
        }
    }

    private void setVisibleSlider(){
        if( mAbsValue < mAbsMaxValue){
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }
//Moscow Slider
    private void drawSliderMoscow(Canvas canvas){
        double price = normalizedToValue(mNormalizedValue);
        if(price < 2000.0f){
            drawFirstRange(canvas);
        } else if( price >= 2000.0f && price < 2500.0f){
           drawSecondRange(canvas);
        } else if(price >= 2500 && price < 5000 ){
            drawThirdRange(canvas);
        }
    }

 //MoscowRegion Slider
    private void drawSliderMoscowRegion(Canvas canvas){
        double price = normalizedToValue(mNormalizedValue);
        if(price < 2000.0f){
            drawFirstRange(canvas);
        } else if( price >= 2000.0f && price < 5000.0f){
            drawSecondRange(canvas);
        } else if(price >= 5000.0f && price < 10000.0f ){
            drawThirdRange(canvas);
        }
    }


//St.Petersburg Slider
    private void drawSliderStPetersburg(Canvas canvas){
        double price = normalizedToValue(mNormalizedValue);
        if(price < 2500.0f){
            drawFirstRange(canvas);
        } else if( price >= 2500.0f && price < 5000.0f){
            drawSecondRange(canvas);
        } else if(price >= 5000.0f && price < 10000.0f ){
           drawThirdRange(canvas);
        }
    }

    private void drawFirstRange(Canvas canvas){
        RectF rect = new RectF(mPadding, (float)(getAxisLine() - mHalfHeightLine), getWidth() - mPadding, (float)(getAxisLine() + mHalfHeightLine) );
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BACKGROUND);
        mPaint.setAntiAlias(true);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_FOREGROUND);
        mPaint.setFakeBoldText(true);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mArcRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, mAbsoluteMinValue);
        mPaint.setFakeBoldText(false);

        rect.right = normalizedToScreen(mNormalizedValue);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float firstBorderCoord = normalizedToScreen(mFirstBorderNormalized);
        canvas.drawCircle(firstBorderCoord, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, firstBorderCoord - mPaddingText, mAbsFirstBorder);
        float secondBorderCoord = normalizedToScreen(mSecondBorderNormalized);
        canvas.drawCircle(secondBorderCoord, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, secondBorderCoord - mPaddingText, mAbsSecondBorder);
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, mAbsMaxValue);
    }

    private void drawSecondRange(Canvas canvas){
        RectF rect = new RectF(mPadding, (float)(getAxisLine() - mHalfHeightLine), getWidth() - mPadding, (float)(getAxisLine() + mHalfHeightLine) );
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BACKGROUND);
        mPaint.setAntiAlias(true);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_FOREGROUND);
        mPaint.setFakeBoldText(true);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, mAbsoluteMinValue);
        mPaint.setFakeBoldText(false);

        rect.right = normalizedToScreen(mNormalizedValue);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_INNER_CIRCLE);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mInnerCupRadius, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float secondBorderCoord = normalizedToScreen(mSecondBorderNormalized);
        canvas.drawCircle(secondBorderCoord, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, secondBorderCoord - mPaddingText, mAbsSecondBorder);
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, mAbsMaxValue);
    }

    private void drawThirdRange(Canvas canvas){
        RectF rect = new RectF(mPadding, (float)(getAxisLine() - mHalfHeightLine), getWidth() - mPadding, (float)(getAxisLine() + mHalfHeightLine) );
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BACKGROUND);
        mPaint.setAntiAlias(true);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_FOREGROUND);
        rect.right = normalizedToScreen(mNormalizedValue);
        canvas.drawRect(rect, mPaint);

        mPaint.setFakeBoldText(true);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, mAbsoluteMinValue);
        mPaint.setFakeBoldText(false);

        mPaint.setColor(COLOR_INNER_CIRCLE);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mInnerCupRadius, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, mAbsMaxValue);
    }

    private void drawText(Canvas canvas, float coord, double value){
        String strValue = String.valueOf(new Double(value).intValue());
        canvas.drawText(strValue, coord, (float)getAxisText(), mPaint);
    }

    private double getAxisLine(){
        return  getHeight() * 0.25f; // line y-axis 20% from the top to the bottom
    }

    private double getAxisText(){
        return  getHeight() * 0.75f; // line y-axis 5% from the top to the bottom
    }

    private void initValues(){
        mPadding = getWidth() / 20; // 4%
        mHalfHeightLine = 0.075f * getHeight(); // height line 15% for all height view
        mArcRadius = 0.98f * mHalfHeightLine;
        mCupRadius = 2.0f * mHalfHeightLine;
        mInnerCupRadius = 0.6f * mCupRadius;
        mPaddingText = mCupRadius * 2.5f;

    }

    private double valueToNormalized(double value) {
        if (0 == mAbsMaxValue - mAbsoluteMinValue) {
            return 0d;
        }
        return (value - mAbsoluteMinValue) / (mAbsMaxValue - mAbsoluteMinValue);
    }

    private double normalizedToValue(double normalized) {
        return  mAbsoluteMinValue + normalized * (mAbsMaxValue - mAbsoluteMinValue);
    }

    private float normalizedToScreen(double normalizedCoord) {
        return (float) (mPadding + normalizedCoord * (getWidth() - 2 * mPadding));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("VALUE", mNormalizedValue);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        mNormalizedValue = bundle.getDouble("VALUE");
    }
}
