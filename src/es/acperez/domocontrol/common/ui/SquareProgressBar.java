package es.acperez.domocontrol.common.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class SquareProgressBar extends ProgressBar {

	public SquareProgressBar(Context context) {
		super(context);
	}
	
	 public SquareProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = heightMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
