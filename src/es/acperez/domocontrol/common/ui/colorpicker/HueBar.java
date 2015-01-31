package es.acperez.domocontrol.common.ui.colorpicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;

public class HueBar extends ColorBar {
	
	public HueBar(Context context) {
		super(context);
	}

	public HueBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HueBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void init() {
		ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
		    @Override
		    public Shader resize(int width, int height) {
		    	int[] colors = new int[360];
		    	for (int i = 0; i < 360; i++) {
		    		colors[i] = Color.HSVToColor(new float[]{i, 1, 1});
		    	}
		    	
		        LinearGradient linearGradient = new LinearGradient(0, 0, width, height, colors, null, Shader.TileMode.REPEAT);
		        return linearGradient;
		    }
		};
		
		PaintDrawable paint = new PaintDrawable();
		paint.setShape(new RectShape());
		paint.setShaderFactory(shaderFactory);
		
		setBackground(paint);
	}
	
	@Override
	protected void updateValue(int progress) {
		mHue = progress;
	}

	@Override
	public void setColor(float[] color) {
			mSaturation = color[1];
			mBrightness = color[2];
	}
}