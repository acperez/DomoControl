package es.acperez.domocontrol.systems.wemo;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.wemo.controller.Device;

public class WemoData {
	private Context mContext;
	private SharedPreferences mSettings;

	private String mUrl;
	private String mDeviceType;
	public String mName;
	public boolean mStatus;
	public Device mDevice;
	
	public WemoData(Context context) {
		mContext = context;
		mSettings = context.getSharedPreferences(DomoSystem.WEMO_SETTINGS_NAME, Context.MODE_PRIVATE);
	}

	public void importSettings() {
		mUrl = mSettings.getString(WemoSystem.DEVICE_URL, null);
		mDeviceType = mSettings.getString(WemoSystem.DEVICE_TYPE, null);
		mName = mSettings.getString(WemoSystem.DEVICE_NAME, mContext.getString(R.string.wemo_default_name));
		
		mDevice = null;
		
		if (mUrl == null || mDeviceType == null)
			return;
		
		try {
			mDevice = new Device(new URL(mUrl), mDeviceType);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		mStatus = mSettings.getBoolean(WemoSystem.DEVICE_STATUS, false);
	}
	
	public void exportSettings(Device device) {
		mDevice = device;
		mUrl = device.mBaseURL.toString();
		mDeviceType = device.mDeviceType;
		
		Editor editor = mSettings.edit();
		
		editor.putString(WemoSystem.DEVICE_URL, mUrl);
		editor.putString(WemoSystem.DEVICE_TYPE, mDeviceType);
		editor.putString(WemoSystem.DEVICE_NAME, mName);
		editor.putBoolean(WemoSystem.DEVICE_STATUS, mStatus);
		
		editor.commit();
	}
}
