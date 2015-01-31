package es.acperez.domocontrol.modules.settings.philips.hue;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsFragment;
import es.acperez.domocontrol.services.philips.hue.LightServiceData;

public class PhilipsHueFragment extends DomoSettingsFragment {
	private View mView;
	
	public PhilipsHueFragment(PhilipsHueController system) {
		super(system);
	}
	
	@Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.philips_hue_settings, container, false);
        
		((Button) mView.findViewById(R.id.philips_hue_settings_connect_with_address)).setOnClickListener(mSettingsConnectListener);
		((Button) mView.findViewById(R.id.philips_hue_settings_find)).setOnClickListener(mSettingsFindListener);
        
        return mView;
    }
	
	@Override
	protected View createWarningView(LayoutInflater inflater, ViewGroup container) {
		ImageView imageView = new ImageView(getActivity());
		imageView.setImageResource(R.drawable.link_auth);
		imageView.setContentDescription(getString(R.string.light_link_description));
		return imageView;
	}
	
	@Override
	protected String getWarningMessage() {
		return getString(R.string.light_press_link);
	}
	
	private OnClickListener mSettingsConnectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String server = ((EditText) mView.findViewById(R.id.philips_hue_settings_address)).getText().toString();
			
			Bundle settings = new Bundle();
			settings.putString(LightServiceData.SERVER, server);
			
			mController.setServiceSettings(settings);
		}
	};
	
	private OnClickListener mSettingsFindListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mController.setServiceSettings(new Bundle());
		}
	};
	
	@Override
	public void updateServiceSettings(Bundle settings) {
		String server = settings.getString(LightServiceData.SERVER);
		
		if (server != null && server.length() > 0)
			((EditText) mView.findViewById(R.id.philips_hue_settings_address)).setText(server);	
	}
}