package es.acperez.domocontrol.systems.light;

import java.util.Arrays;

import es.acperez.domocontrol.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class ColorPicker extends SeekBar {
	private static final int TYPE_HUE = 0;
	private static final int TYPE_SATURATION = 1;
	private static final int TYPE_VALUE = 2;
	
	Bitmap mBitmap = null;
	ColorPickerInitListener mInitListener;
	private float mHue = 0;
	private float mSaturation = 1;
	private float mValue = 1;
	
	private int mPickerType = -1;
	
	Paint mPaint;
	
	public interface ColorPickerInitListener {
		void onColorViewInit(int color);
	}
	
	public ColorPicker(Context context) {
		super(context);
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		getAttrs(attrs);
	}
	
	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getAttrs(attrs);
	}
	
	private void getAttrs(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setColor(Color.argb(255, 0, 0, 0));
        mPaint.setStrokeWidth(1);        
        mPaint.setStyle(Paint.Style.STROKE);
        
	    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPicker);
	    String type = a.getString(R.styleable.ColorPicker_type);
	    
	    a.recycle();
	    
	    if (type == null) {
	    	mPickerType = TYPE_HUE;
	    	return;
	    }
	    
	    if (type.equals("saturation")) {
	    	mPickerType = TYPE_SATURATION;
	    	return;
	    }
	    
	    if (type.equals("value")) {
	    	mPickerType = TYPE_VALUE;
	    	return;
	    }
	    
	    mPickerType = TYPE_HUE;
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mBitmap == null)
    		mBitmap = init(getWidth(), getHeight());
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
		c.drawBitmap(mBitmap, 0, 0, null);
		
		c.drawRect(0, 0, getWidth() - 1, getHeight() - 1, mPaint);
		
        c.rotate(-90);
        c.translate(-getHeight(), -1);
        
        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            	int i=0;
            	i=getMax() - (int) (getMax() * event.getY() / getHeight());
            	updateValue(i);
                setProgress(i);

                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private Bitmap init(int width, int height) {
    	if (mPickerType == TYPE_SATURATION)
   			return generateSaturation(width, height);

    	if (mPickerType == TYPE_VALUE)
   			return generateValue(width, height);
    	
		return generateHue(width, height);
    }
    
	private Bitmap generateHue(int width, int height) {
		Bitmap bmp = null;
		
		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);;
		
		int colors = 360;
		float interval = (float)colors / height;
		
		float a[] = {mHue, mSaturation, mValue};
		int[] line = new int[width];
		int color;
		
		for (int y = 0; y < height; y++) {
			a[0] += interval;
			color = Color.HSVToColor(a);
			
			Arrays.fill(line, color);
			bmp.setPixels(line, 0, width, 0, y, width, 1);
		}
		
		if (mInitListener != null) {
			mInitListener.onColorViewInit(getColor());
		}
		
		return bmp;
	}
	
	private Bitmap generateSaturation(int width, int height) {
		Bitmap bmp = null;
		
		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);;
		
		float interval = 1.0f / height;
		
		float a[] = {mHue, 0, mValue};
		int[] line = new int[width];
		int color;
		
		for (int y = 0; y < height; y++) {
			a[1] += interval;
			color = Color.HSVToColor(a);
						
			Arrays.fill(line, color);
			bmp.setPixels(line, 0, width, 0, y, width, 1);
		}
		
		return bmp;
	}

	private Bitmap generateValue(int width, int height) {
		Bitmap bmp = null;
		
		bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);;
		
		float interval = 1.0f / height;
		
		float a[] = {mHue, mSaturation, 0};
		int[] line = new int[width];
		int color;
		
		for (int y = 0; y < height; y++) {
			a[2] += interval;
			color = Color.HSVToColor(a);
						
			Arrays.fill(line, color);
			bmp.setPixels(line, 0, width, 0, y, width, 1);
		}
		
		return bmp;
	}
	
	public void setInitListener(ColorPickerInitListener initListener) {
		mInitListener = initListener;
	}
	
	private void updateValue(int progress) {
		progress = 100 - progress;
		
		if (mPickerType == TYPE_HUE) {
			mHue = 360.0f * progress / 100.0f;
			return;
		}
		
		if (mPickerType == TYPE_SATURATION) {
			mSaturation = 1.0f * progress / 100.0f;
			return;
		}
		
		if (mPickerType == TYPE_VALUE) {
			mValue = 1.0f * progress / 100.0f;
			return;
		}
	}
	
	public void setColor(int color) {
		float[] hsv = new float[3];
		Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
		
		if (mPickerType == TYPE_HUE) {
			mSaturation = hsv[1];
			return;
		}
		
		if (mPickerType == TYPE_SATURATION) {
			mHue = hsv[0];
			mValue = hsv[2];
			if (mBitmap != null) {
				mBitmap.recycle();
				mBitmap = init(getWidth(), getHeight());
			}
			return;
		}
		
		if (mPickerType == TYPE_VALUE) {
			mHue = hsv[0];
			mSaturation = hsv[1];
			if (mBitmap != null) {
				mBitmap.recycle();
				mBitmap = init(getWidth(), getHeight());
			}
			return;
		}
	}
	
	public int getColor() {
		float[] hsv = {mHue, mSaturation, mValue};
		return Color.HSVToColor(hsv);
	}
}