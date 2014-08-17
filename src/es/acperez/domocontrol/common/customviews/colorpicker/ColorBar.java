package es.acperez.domocontrol.common.customviews.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public abstract class ColorBar extends SeekBar {
	ColorPickerInitListener mInitListener;
	protected float mHue = 0;
	protected float mSaturation = 1;
	protected float mBrightness = 1;
		
	protected abstract void init();
	protected abstract void updateValue(int value);
	public abstract void setColor(float[] color);
	
	public interface ColorPickerInitListener {
		void onColorViewInit(float[] color);
	}
	
	public ColorBar(Context context) {
		super(context);
	}

	public ColorBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		_init(attrs);
	}
	
	public ColorBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_init(attrs);
	}
	
	private void _init(AttributeSet attrs) {
        init();
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
    	c.rotate(-90);
        c.translate(-getHeight(), -1);
        super.onDraw(c);
    }

	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_MOVE && event.getAction() != MotionEvent.ACTION_DOWN)
        	return true;
        
        int max = getMax();
        int i = max - (int) (max * event.getY() / getHeight());
        
    	updateValue(max - i);
        setProgress(i);
        
        onSizeChanged(getWidth(), getHeight(), 0, 0);

        return true;
    }
	
	public void updateProgress(int progress) {
    	updateValue(getMax() - progress);
        setProgress(progress);
        
        onSizeChanged(getWidth(), getHeight(), 0, 0);
	}
	
	public void setInitListener(ColorPickerInitListener initListener) {
		mInitListener = initListener;
	}
	
	public float[] getColor() {
		float[] hsv = {mHue, mSaturation, mBrightness};
		return hsv;
	}
}