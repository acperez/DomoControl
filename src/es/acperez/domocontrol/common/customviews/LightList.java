package es.acperez.domocontrol.common.customviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.light.controller.LightUtils;

public class LightList {
	Context mContext;
	LinearLayout mLightList;
	OnLightSelectedListener mLightListener;
	ArrayList<LightView> mViews;
	HashMap<String, Integer> mIds;
	
	public interface OnLightSelectedListener {
		void onLightSelected(boolean edit, String lightId);
	}

	public LightList(Context context, LinearLayout root, OnLightSelectedListener listener) {
		mContext = context;
		mLightList = root;
		mLightListener = listener;
		mViews = new ArrayList<LightList.LightView>();
		mIds = new HashMap<String, Integer>();
	}

	public int getCount() {
		return mViews.size();
	}
	
	public String getLightId(int position) {
		return mViews.get(position).getLightId();
	}
	
	public void init(List<PHLight> lights) {
		mLightList.removeAllViews();
		
		mViews.clear();
		mIds.clear();
		
		for (int i = 0; i < lights.size(); i++) {
			createView(lights.get(i));
		}
	}
	
	private void createView(PHLight light) {
		LightView view = new LightView(mContext, light);
		mViews.add(view);
		mIds.put(view.mId, mViews.size() - 1);
		view.setOnClickListener(mClickListener);
		mLightList.addView(view);
	}
	
	OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			LightView view = (LightView) v;
			mLightListener.onLightSelected(view.switchEdit(), view.getLightId());
		}
	};
	
	public void update(Map<String, PHLight> lights, ArrayList<String> ids) {
		for (String id : ids) {
			LightView view = mViews.get(mIds.get(id));
			PHLight light = lights.get(id);
			
			view.setName(light.getName(), light.getLastKnownLightState().isOn());
			view.setThumb(LightUtils.createThumb(light));
		}
	}
	
	public void updateAll(List<PHLight> lights) {
		for (PHLight light : lights) {
			String id = light.getIdentifier();
			
			if (mIds.containsKey(id)) {
				LightView view = mViews.get(mIds.get(id));
				view.setName(light.getName(), light.getLastKnownLightState().isOn());
				view.setThumb(LightUtils.createThumb(light));
			} else {
				createView(light);
			}
		}
	}
	
	public class LightView extends RelativeLayout {
    	private ImageView mImageThumb;
    	private TextView mName;
    	private CheckBox mEdit;
    	private String mId;
    	private int mColorOn;
    	private int mColorOff;
    	
        public LightView(Context context, PHLight light) {
            super(context);

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_list_item, this, true);
			
            mColorOn = mContext.getResources().getColor(R.color.light_on_text_color);
            mColorOff = mContext.getResources().getColor(R.color.light_off_text_color);
            
    		mName = (TextView) findViewById(R.id.light_list_name);
    		mName.setText(light.getName());
    		if (light.getLastKnownLightState().isOn())
    			mName.setTextColor(mColorOn);
    		else
    			mName.setTextColor(mColorOff);
            
    		mEdit = (CheckBox) findViewById(R.id.light_list_edit);
    		
    		mId =  light.getIdentifier();
    		
			mImageThumb = (ImageView) findViewById(R.id.light_list_color);
			mImageThumb.setImageDrawable(LightUtils.createThumb(light));
        }

        public void setName(String name, boolean status) {
            mName.setText(name);
            
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

		public String getLightId() {
			return mId;
		}
	}
}
