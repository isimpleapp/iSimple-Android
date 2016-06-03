package com.treelev.isimple.animation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

public class AnimationWithMargins extends TranslateAnimation {

	private View mAnimatedView;

	private int mMarginStart = 0;

	private int mMarginEnd = 0;

	public AnimationWithMargins(Context context, AttributeSet attrs, View animatedView) {
		super(context, attrs);
		this.mAnimatedView = animatedView;
	}

	public AnimationWithMargins(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta, View animatedView) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
		this.mAnimatedView = animatedView;
		mMarginStart = Math.round(fromYDelta);
		mMarginEnd = Math.round(toYDelta);
	}

	public AnimationWithMargins(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType,
                                float fromYValue, int toYType, float toYValue, View animatedView) {
		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
		this.mAnimatedView = animatedView;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);

		((RelativeLayout.LayoutParams) mAnimatedView.getLayoutParams()).bottomMargin = mMarginStart
				+ (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

		mAnimatedView.requestLayout();
	}

}
