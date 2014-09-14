package es.acperez.domocontrol.common.customviews.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class SaturationBar extends ColorBar {

	public SaturationBar(Context context) {
		super(context);
	}

	public SaturationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SaturationBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void init() {
		setBackgroundColor(Color.HSVToColor(new float[]{mHue, 1, mBrightness}));
	}
	
	@Override
	protected void updateValue(int progress) {
		mSaturation = progress / 100.0f;
	}

	@Override
	public void setColor(float[] color) {
		mHue = color[0];
		mBrightness = color[2];
		
		setBackgroundColor(Color.HSVToColor(new float[]{mHue, 1, mBrightness}));
		return;
	}
}