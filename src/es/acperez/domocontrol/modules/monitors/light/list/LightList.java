package es.acperez.domocontrol.modules.monitors.light.list;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.services.philips.hue.DomoLight;

public class LightList extends LinearLayout {
	
	private ArrayList<DomoSwitch> mLights;
	private ArrayList<DomoLight> mSelectedLights;
	private SparseArray<LightItem> mItems;
	
	private OnLightSelectedListener mLightListener;
	
	public interface OnLightSelectedListener {
		void onLightSelected();
	}

	public LightList(Context context) {
		super(context);
	}

	public LightList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LightList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setListener(OnLightSelectedListener listener) {
		mLightListener = listener;
	}
	
	public void init(ArrayList<DomoSwitch> lights) {
		removeAllViews();
		
		mLights = lights;
		mItems = new SparseArray<LightItem>();
		mSelectedLights = new ArrayList<DomoLight>();

		for (int i = 0; i < mLights.size(); i++) {
			DomoLight light = (DomoLight) mLights.get(i);
			LightView view = createView(light);
			mItems.put(light.id, new LightItem(light, view));
		}
	}
	
	private LightView createView(DomoLight light) {
		LightView view = new LightView(getContext(), light);
		view.setOnClickListener(mClickListener);
		addView(view);
		return view;
	}
	
	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			LightView lightView = (LightView) view;
			boolean edit = lightView.switchEdit();
			
			DomoLight light = mItems.get(lightView.mId).light;
			
			if (edit && !mSelectedLights.contains(light)) {
				mSelectedLights.add(light);
				mLightListener.onLightSelected();
				return;
			}
			
			if (!edit && mSelectedLights.contains(light)) {
				mSelectedLights.remove(light);
			}
			
			mLightListener.onLightSelected();
		}
	};
	
	public void notifyDataSetChanged() {
		for (DomoSwitch domoSwitch : mLights) {
			
			DomoLight light = (DomoLight) domoSwitch;
			LightItem item = mItems.get(light.id);

			if (item != null) {
				item.view.setName(light.name);
				item.view.setStatus(light.status);
				item.view.setThumb(light.thumb);
			} else {
				LightView view = createView(light);
				mItems.put(light.id, new LightItem(light, view));
			}
		}
	}
	
	public int getCount() {
		return mLights.size();
	}
	
	public DomoSwitch getItem(int position) {
		return mLights.get(position);
	}
	
	public int getLightId(int position) {
		return mLights.get(position).id;
	}
	
	public ArrayList<DomoLight> getSelectedLights() {
		return mSelectedLights;
	}
	
	public void updateColor(ArrayList<DomoLight> lights) {
		for (DomoLight light : lights) {
			LightView view = mItems.get(light.id).view;
			view.setThumb(light.thumb);
		}
	}

	public void updateLight(DomoSwitch domoSwitch) {
		LightView view = mItems.get(domoSwitch.id).view;
		view.setStatus(domoSwitch.status);
		view.setName(domoSwitch.name);
	}
	
	public class LightView extends RelativeLayout {

		private ImageView mImageThumb;
    	private TextView mName;
    	private CheckBox mEdit;
    	private int mId;
    	private int mColorOn;
    	private int mColorOff;
    	
        public LightView(Context context, DomoLight light) {
            super(context);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_list_item, this, true);
			
            mColorOn = context.getResources().getColor(R.color.light_on_text_color);
            mColorOff = context.getResources().getColor(R.color.light_off_text_color);
            
    		mName = (TextView) findViewById(R.id.light_list_name);
    		mName.setText(light.name);
    		if (light.status)
    			mName.setTextColor(mColorOn);
    		else
    			mName.setTextColor(mColorOff);
    		
    		mEdit = (CheckBox) findViewById(R.id.light_list_edit);
    		// If id is not modified, all check boxes change together
    		mEdit.setId(-1);
    		mId = light.id;
    		
			mImageThumb = (ImageView) findViewById(R.id.light_list_color);
			mImageThumb.setImageDrawable(light.thumb);
        }

        public void setName(String name) {
            mName.setText(name);
        }
        
        public void setStatus(boolean status) {
        	if (status)
    			mName.setTextColor(mColorOn);
            else
            	mName.setTextColor(mColorOff);
        }

        public void setThumb(Drawable thumb) {
            mImageThumb.setImageDrawable(thumb);
        }
        
        public boolean switchEdit() {
        	boolean checked = !mEdit.isChecked();
        	
        	mEdit.setChecked(checked);
        	return checked;
        }

		public int getLightId() {
			return mId;
		}
	}
}
