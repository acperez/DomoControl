package es.acperez.domocontrol.common.customviews.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.common.customviews.colorpicker.ColorBar.ColorPickerInitListener;

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
	    mHueBar.setInitListener(mHueInitListener);
		mHueBar.setOnSeekBarChangeListener(mHueListener);

	    ((ImageButton) getChildAt(3)).setOnClickListener(mSaturationIncListener);
	    ((ImageButton) getChildAt(4)).setOnClickListener(mSaturationDecListener);
	    mSaturationBar = (SaturationBar) getChildAt(5);
	    mSaturationBar.setOnSeekBarChangeListener(mSaturationListener);
	    
	    ((ImageButton) getChildAt(6)).setOnClickListener(mBrightnessIncListener);
	    ((ImageButton) getChildAt(7)).setOnClickListener(mBrightnessDecListener);
	    mBrightnessBar = (BrightnessBar) getChildAt(8);
	    mBrightnessBar.setOnSeekBarChangeListener(mValueListener);
	    
	    mListener = new OnColorChangeListener() {
			@Override
			public void onColorChange(float[] color) {}
		};
	}
	
	public void setOnColorChangeListener(OnColorChangeListener listener) {
		mListener = listener;
	}
	
	private ColorPickerInitListener mHueInitListener = new ColorPickerInitListener() {
	
		@Override
		public void onColorViewInit(float[] color) {
			setBackgroundColor(Color.HSVToColor(color));
			mSaturationBar.setColor(color);
			mBrightnessBar.setColor(color);
		}
	};
	
	private OnSeekBarChangeListener mHueListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			float[] color = ((ColorBar) seekBar).getColor();
			setBackgroundColor(Color.HSVToColor(color));
			mSaturationBar.setColor(color);
			mBrightnessBar.setColor(color);
			mBrightnessBar.setColor(color);
			
			mListener.onColorChange(color);
		}
	};
	
	private OnSeekBarChangeListener mSaturationListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			float[] color = ((ColorBar) seekBar).getColor();
			setBackgroundColor(Color.HSVToColor(color));
			mHueBar.setColor(color);
			mBrightnessBar.setColor(color);
			
			mListener.onColorChange(color);
		}
	};
	
	private OnSeekBarChangeListener mValueListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			float[] color = ((ColorBar) seekBar).getColor();
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
			mHueBar.updateProgress(mSaturationBar.getProgress() + 1);
		}
	};
	
	private OnClickListener mSaturationDecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHueBar.updateProgress(mSaturationBar.getProgress() - 1);
		}
	};
	
	private OnClickListener mBrightnessIncListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHueBar.updateProgress(mBrightnessBar.getProgress() + 1);
		}
	};
	
	private OnClickListener mBrightnessDecListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mHueBar.updateProgress(mBrightnessBar.getProgress() - 1);
		}
	};
}