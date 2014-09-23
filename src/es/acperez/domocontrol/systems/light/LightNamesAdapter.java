package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.R;

public class LightNamesAdapter extends BaseAdapter {
	private Context mContext;
	private List<PHLight> mLights;
 
	public LightNamesAdapter(Context context, List<PHLight> lights) {
		mContext = context;
		if (lights == null)
			mLights = new ArrayList<PHLight>();
		else
			mLights = lights;
	}
 
	@Override
	public int getCount() {
		return mLights.size();
	}
 
	@Override
	public Object getItem(int position) {
		return mLights.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setData(List<PHLight> lights) {
		mLights = lights;
		notifyDataSetChanged();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LightView view;
		PHLight light = mLights.get(position);
		
		if (convertView == null) {	
			view = new LightView(mContext, light);
		} else {
			view = (LightView) convertView;
			view.setName(light.getName());
		}
		
		return view;
	}
	
	public class LightView extends LinearLayout {
    	private EditText mName;
    	
        public LightView(Context context, PHLight light) {
            super(context);

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.light_name_list_item, this, true);
			
			mName = (EditText)findViewById(R.id.light_name_list_name);
			setName(light.getName());
        }

        private void setName(String name) {
            mName.setHint(name);
        }
    }
}