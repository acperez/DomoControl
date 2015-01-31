package es.acperez.domocontrol.common.ui.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class BrightnessBar extends ColorBar {

	public BrightnessBar(Context context) {
		super(context);
	}

	public BrightnessBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public BrightnessBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void init() {
		setBackgroundColor(Color.HSVToColor(new float[]{mHue, mSaturation, 1}));
	}

	@Override
	protected void updateValue(int progress) {
		mBrightness = progress / 100.0f;
	}

	@Override
	public void setColor(float[] color) {
		mHue = color[0];
		mSaturation = color[1];
		
		setBackgroundColor(Color.HSVToColor(new float[]{mHue, mSaturation, 1}));
	}
}