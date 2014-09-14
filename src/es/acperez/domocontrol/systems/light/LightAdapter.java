package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;

import com.philips.lighting.model.PHLight;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.light.controller.LightUtils;

public class LightAdapter extends BaseAdapter {
	private Context mContext;
	private List<PHLight> mLights;
	
	public LightAdapter(Context context) {
		mContext = context;
		mLights = new ArrayList<PHLight>();
	}
	
	@Override
	public int getCount() {
		return mLights.size();
	}

	@Override
	public Object getItem(int position) {
		return mLights.get(position).getIdentifier();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		 
		LightView view;
		PHLight light = mLights.get(position);
		
		if (convertView == null) {	
			view = new LightView(mContext, light);
		} else {
			view = (LightView) convertView;
			view.setName(light.getName(), light.getLastKnownLightState().isOn());
			view.setThumb(LightUtils.createThumb(light));
		}
		
		return view;
	}

	public void setData(List<PHLight> lights) {
		mLights = lights;
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
			mImageThumb.post(new Runnable() {

		        @Override
		        public void run() {
		        	RelativeLayout.LayoutParams mParams;
		            mParams = (RelativeLayout.LayoutParams) mImageThumb.getLayoutParams();
		            mParams.width = mImageThumb.getHeight();
		            mImageThumb.setLayoutParams(mParams);
		            mImageThumb.postInvalidate();
		        }
		    });
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