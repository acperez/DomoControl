package es.acperez.domocontrol.modules.settings.wemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsFragment;
import es.acperez.domocontrol.services.wemo.WemoServiceData;

public class WemoFragment extends DomoSettingsFragment {
	private View mView;
	private WemoController mController;
	
	public WemoFragment(WemoController system) {
		super(system);
		mController = system;
	}
	
	@Override
    public View createView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.wemo_settings, container, false);
        ((Button) mView.findViewById(R.id.wemo_settings_apply)).setOnClickListener(mSettingsApplyListener);
        return mView;
    }
	
	private OnClickListener mSettingsApplyListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mController.setServiceSettings(null);
		}
	};

	@Override
	public void updateServiceSettings(Bundle settings) {

		String name = settings.getString(WemoServiceData.DEVICE_NAME);
		String host = settings.getString(WemoServiceData.DEVICE_HOST);
		
		if (host != null) {
			((LinearLayout) mView.findViewById(R.id.wemo_settings_found_devices)).setVisibility(View.VISIBLE);
			((TextView) mView.findViewById(R.id.wemo_settings_device_address)).setText(host);
			((TextView) mView.findViewById(R.id.wemo_settings_device_name)).setText(name);
		} else {
			((LinearLayout) mView.findViewById(R.id.wemo_settings_found_devices)).setVisibility(View.GONE);
		}
	}
}
