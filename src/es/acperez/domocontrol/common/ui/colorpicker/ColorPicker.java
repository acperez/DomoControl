package es.acperez.domocontrol.common.ui.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.ui.colorpicker.ColorBar.ColorBarListener;

public class ColorPicker extends RelativeLayout {

	private HueBar mHueBar;
	private SaturationBar mSaturationBar;
	private BrightnessBar mBrightnessBar;
	
	private OnColorChangeListener mListener;

	public interface OnColorChangeListener {
		void onColorChange(float[] color);
	}
	
	public ColorPicker(Context context) {
		super(context);
		init(context);
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    inflater.inflate(R.layout.color_picker, this, true);
	    
	    setBackgroundColor(Color.RED);
	    
	    ((ImageButton) getChildAt(0)).setOnClickListener(mHueIncListener);
	    ((ImageButton) getChildAt(1)).setOnClickListener(mHueDecListener);
	    mHueBar = (HueBar) getChildAt(2);
	    mHueBar.setColorBarListener(mHueListener);

	    ((ImageButton) getChildAt(3)).setOnClickListener(mSaturationIncListener);
	    ((ImageButton) getChildAt(4)).setOnClickListener(mSaturationDecListener);
	    mSaturationBar = (SaturationBar) getChildAt(5);
	    mSaturationBar.setColorBarListener(mSaturationListener);
	    
	    ((ImageButton) getChildAt(6)).setOnClickListener(mBrightnessIncListener);
	    ((ImageButton) getChildAt(7)).setOnClickListener(mBrightnessDecListener);
	    mBrightnessBar = (BrightnessBar) getChildAt(8);
	    mBrightnessBar.setColorBarListener(mValueListener);
	    
	    mListener = new OnColorChangeListener() {
			@Override
			public void onColorChange(float[] color) {}
		};
	}
	
	public void setOnColorChangeListener(OnColorChangeListener listener) {
		mListener = listener;
	}
	
	private ColorBarListener mHueListener = new ColorBarListener() {
		@Override
		public void onColorBarChange(float[] color) {
			setBackgroundColor(Color.HSVToColor(color));
			mSaturationBar.setColor(color);
			mBrightnessBar.setColor(color);
			mBrightnessBar.setColor(color);
			
			mListener.onColorChange(color);
		}
	};
	
	private ColorBarListener mSaturationListener = new ColorBarListener() {
		@Override
		public void onColorBarChange(float[] color) {
			setBackgroundColor(Color.HSVToColor(color));
			mHueBar.setColor(color);
			mBrightnessBar.setColor(color);
			
			mListener.onColorChange(color);
		}
	};
	
	private ColorBarListener mValueListener = new ColorBarListener() {
		@Override
		public void onColorBarChange(float[] color) {
			setBackgroundColor(Color.HSVToColor(color));
			mHueBar.setColor(color);
			mSaturationBar.setColor(color);
			
			mListener.onColorChange(color);
		}
	};
	
	private OnClickListener mHueIncListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHueBar.updateProgress(mHueBar.getProgress() + 1);
		}
	};
	
	private OnClickListener mHueDecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHueBar.updateProgress(mHueBar.getProgress() - 1);
		}
	};
	
	private OnClickListener mSaturationIncListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSaturationBar.updateProgress(mSaturationBar.getProgress() + 1);
		}
	};
	
	private OnClickListener mSaturationDecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mSaturationBar.updateProgress(mSaturationBar.getProgress() - 1);
		}
	};
	
	private OnClickListener mBrightnessIncListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBrightnessBar.updateProgress(mBrightnessBar.getProgress() + 1);
		}
	};
	
	private OnClickListener mBrightnessDecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mBrightnessBar.updateProgress(mBrightnessBar.getProgress() - 1);
		}
	};
}