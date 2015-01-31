package es.acperez.domocontrol.common.ui;

import es.acperez.domocontrol.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {
	private boolean stretchVertical = false;

	public SquareImageView(Context context) {
		super(context);
	}
	
	 public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	@Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!stretchVertical)
			widthMeasureSpec = heightMeasureSpec;
		else
			heightMeasureSpec = widthMeasureSpec;
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 }
	
	private void init(AttributeSet attrs) { 
	    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SquareImageView);
	    stretchVertical = a.getBoolean(R.styleable.SquareImageView_stretchVertical, false);
	    a.recycle();
	}
}
