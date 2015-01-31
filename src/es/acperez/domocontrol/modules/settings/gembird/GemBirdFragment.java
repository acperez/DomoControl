package es.acperez.domocontrol.modules.settings.gembird;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsFragment;
import es.acperez.domocontrol.services.gembird.PowerServiceData;

public class GemBirdFragment extends DomoSettingsFragment {
	private View mView;
	
	public GemBirdFragment(GemBirdController system) {
		super(system);
	}
	
	@Override
	protected View createView(LayoutInflater inflater, ViewGroup container) {
		mView = inflater.inflate(R.layout.gembird_settings, container, false);	
		((Button) mView.findViewById(R.id.gembird_settings_connect)).setOnClickListener(mSettingsConnectListener);
        return mView;
	}
		
	private OnClickListener mSettingsConnectListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {			
			String server = ((EditText) mView.findViewById(R.id.gembird_settings_address)).getText().toString();
			int port = Integer.valueOf(((EditText) mView.findViewById(R.id.gembird_settings_address_port)).getText().toString());
			String password = ((EditText) mView.findViewById(R.id.gembird_settings_password)).getText().toString();
			
			Bundle settings = new Bundle();
			settings.putString(PowerServiceData.SERVER, server);
			settings.putInt(PowerServiceData.SERVER_PORT, port);
			settings.putString(PowerServiceData.PASSWORD, password);
			
			mController.setServiceSettings(settings);
		}
	};
	
	@Override
	protected void updateServiceSettings(Bundle settings) {
		String server = settings.getString(PowerServiceData.SERVER);
		int port = settings.getInt(PowerServiceData.SERVER_PORT);
		String password = settings.getString(PowerServiceData.PASSWORD);
		
		if (server != null && server.length() > 0)
			((EditText) mView.findViewById(R.id.gembird_settings_address)).setText(server);
		
		if (port != 0)
			((EditText) mView.findViewById(R.id.gembird_settings_address_port)).setText(String.valueOf(port));
		
		if (password.length() > 0)
			((EditText) mView.findViewById(R.id.gembird_settings_password)).setText(password);
	}
}