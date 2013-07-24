package com.treelev.isimple.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class PriceSlider extends ImageView {

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
    private final float mSizeText = 14.0f;

    private double mAbsoluteMinValue = 0.0f;
    private double mAbsoluteMaxValue = 5000.0f;
    private double mMaxNormalized;
    private double mMinNormalized;

    private double mNormalizedValue = 0d;

    public PriceSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMaxNormalized = valueToNormalized(mAbsoluteMaxValue);
        setVisibility(GONE);
        initSizetText();
    }

    private void initSizetText(){
        float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
        final float scaledPx = mSizeText * densityMultiplier;
        mPaint.setTextSize(scaledPx);
    }


    public void setValue(double value){
        setVisibility(VISIBLE);
        if(value < 2000.0f){
            mAbsoluteMinValue = 0.0f;
        } else if( value >= 2000.0f && value < 2500.0f){
            mAbsoluteMinValue = 2000.0f;
        } else if(value >= 2500 && value < 5000 ){
            mAbsoluteMinValue = 2500.0f;
        } else if(value > 5000.0f){
            setVisibility(GONE);
        }
        mMinNormalized = valueToNormalized(mAbsoluteMinValue);
        mNormalizedValue = valueToNormalized(value);
        invalidate();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initValues();
        drawSlider(canvas);

    }

    private void drawSlider(Canvas canvas){
        double price = normalizedToValue(mNormalizedValue);
        if(price < 2000.0f){
            drawFirstRange(canvas);
        } else if( price >= 2000.0f && price < 2500.0f){
           drawSecondRange(canvas);
        } else if(price >= 2500 && price < 5000 ){
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
        canvas.drawCircle(mPadding, (float) getAxisLine(), mArcRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, "0");

        rect.right = normalizedToScreen(mNormalizedValue);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float coordCircle2000 = normalizedToScreen(valueToNormalized(2000.0f));
        canvas.drawCircle(coordCircle2000, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordCircle2000 - mPaddingText, "2 000");
        float coordCircle2500 = normalizedToScreen(valueToNormalized(2500.0f));
        canvas.drawCircle(coordCircle2500, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordCircle2500 - mPaddingText, "2 500");
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, "5 000");
    }

    private void drawSecondRange(Canvas canvas){
        RectF rect = new RectF(mPadding, (float)(getAxisLine() - mHalfHeightLine), getWidth() - mPadding, (float)(getAxisLine() + mHalfHeightLine) );
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BACKGROUND);
        mPaint.setAntiAlias(true);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_FOREGROUND);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, "2 000");

        rect.right = normalizedToScreen(mNormalizedValue);
        canvas.drawRect(rect, mPaint);

        mPaint.setColor(COLOR_INNER_CIRCLE);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mInnerCupRadius, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float coordCircle2500 = normalizedToScreen(valueToNormalized(2500.0f));
        canvas.drawCircle(coordCircle2500, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordCircle2500 - mPaddingText, "2 500");
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, "5 000");
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

        canvas.drawCircle(mPadding, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, mPadding / 1.5f, "2 500");

        mPaint.setColor(COLOR_INNER_CIRCLE);
        canvas.drawCircle(mPadding, (float) getAxisLine(), mInnerCupRadius, mPaint);

        mPaint.setColor(COLOR_BACKGROUND);
        float coordMax = normalizedToScreen(mMaxNormalized);
        canvas.drawCircle(coordMax, (float) getAxisLine(), mCupRadius, mPaint);
        drawText(canvas, coordMax - mPaddingText, "5 000");
    }

    private double getAxisLine(){
        return  getHeight() * 0.25f; // line y-axis 20% from the top to the bottom
    }

    private void drawText(Canvas canvas, float coord, String text){
        canvas.drawText(text, coord, (float)getAxisText(), mPaint);
    }


    private double getAxisText(){
        return  getHeight() * 0.75f; // line y-axis 5% from the top to the bottom
    }

    private void initValues(){
        mPadding = getWidth() / 20; // 4%
        mHalfHeightLine = 0.075f * getHeight(); // height line 15% for all height view
        mArcRadius = mHalfHeightLine;
        mCupRadius = 2.0f * mArcRadius;
        mInnerCupRadius = 0.6f * mCupRadius;
        mPaddingText = mCupRadius * 2.5f;

    }

    private double valueToNormalized(double value) {
        if (0 == mAbsoluteMaxValue - mAbsoluteMinValue) {
            return 0d;
        }
        return (value - mAbsoluteMinValue) / (mAbsoluteMaxValue - mAbsoluteMinValue);
    }

    private double normalizedToValue(double normalized) {
        return  mAbsoluteMinValue + normalized * (mAbsoluteMaxValue - mAbsoluteMinValue);
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
