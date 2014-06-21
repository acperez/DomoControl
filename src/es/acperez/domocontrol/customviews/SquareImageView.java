package es.acperez.domocontrol.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImageView extends ImageView {

	public SquareImageView(Context context) {
		super(context);
	}
	
	 public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		widthMeasureSpec = heightMeasureSpec;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	 }
}