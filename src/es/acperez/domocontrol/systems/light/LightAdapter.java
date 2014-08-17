package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;

import com.philips.lighting.model.PHLight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
		View view;

		if (convertView == null) {
			view = new View(mContext);
			view = inflater.inflate(R.layout.light_list_item, null);
		} else
			view = (View) convertView;
 
		PHLight light = mLights.get(position);
		
		TextView textView = (TextView) view.findViewById(R.id.light_list_name);
		textView.setText(light.getName());
		if (light.getLastKnownLightState().isOn())
			textView.setTextColor(mContext.getResources().getColor(R.color.light_on_text_color));
		else
			textView.setTextColor(mContext.getResources().getColor(R.color.light_off_text_color));

		final ImageView imageThumb = (ImageView) view.findViewById(R.id.light_list_color);
		imageThumb.setImageDrawable(LightUtils.createThumb(light));
		imageThumb.post(new Runnable() {

	        @Override
	        public void run() {
	        	RelativeLayout.LayoutParams mParams;
	            mParams = (RelativeLayout.LayoutParams) imageThumb.getLayoutParams();
	            mParams.width = imageThumb.getHeight();
	            imageThumb.setLayoutParams(mParams);
	            imageThumb.postInvalidate();
	        }
	    });
		
		return view;
	}

	public void setData(List<PHLight> lights) {
		mLights = lights;
	}
}