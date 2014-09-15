package es.acperez.domocontrol.common.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareUnsizedImageView extends ImageView {
	private int mSize = Integer.MAX_VALUE;

	public SquareUnsizedImageView(Context context) {
		super(context);
	}
	
	 public SquareUnsizedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareUnsizedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int s = getMeasuredHeight();
		if (s != 0 && s < mSize)
			mSize = s;

        setMeasuredDimension(mSize, getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
	 }
}
