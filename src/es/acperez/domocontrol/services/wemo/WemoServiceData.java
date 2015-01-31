package es.acperez.domocontrol.services.wemo;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.ServiceData;

public class WemoServiceData extends ServiceData {
	final public static String DEVICE_STATUS = "wemo.socket.status";
	final public static String DEVICE_URL = "wemo.socket.locationURL";
	final public static String DEVICE_TYPE = "wemo.socket.deviceType";
	final public static String DEVICE_NAME = "wemo.socket.deviceName";
	final public static String DEVICE_HOST = "wemo.socket.host";
	
	private Context mContext;
	private SharedPreferences mSettings;

	private String mUrl;
	private String mDeviceType;
	public String mName;
	public boolean mStatus;
	public Device mDevice;
	
	public WemoServiceData(Context context) {
		mContext = context;
		mSettings = context.getSharedPreferences(DomoController.WEMO_SETTINGS_NAME, Context.MODE_PRIVATE);
	}

	public void importSettings() {
		mUrl = mSettings.getString(DEVICE_URL, null);
		mDeviceType = mSettings.getString(DEVICE_TYPE, null);
		mName = mSettings.getString(DEVICE_NAME, mContext.getString(R.string.wemo_default_name));
		
		mDevice = null;
		
		if (mUrl == null || mDeviceType == null)
			return;
		
		try {
			mDevice = new Device(new URL(mUrl), mDeviceType);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		mStatus = mSettings.getBoolean(DEVICE_STATUS, false);
	}
	
	public void setDevice(Device device) {
		mDevice = device;
		mUrl = device.mBaseURL.toString();
		mDeviceType = device.mDeviceType;
	}

	@Override
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putString(DEVICE_URL, mUrl);
		editor.putString(DEVICE_TYPE, mDeviceType);
		editor.putString(DEVICE_NAME, mName);
		editor.putBoolean(DEVICE_STATUS, mStatus);
		
		editor.commit();
	}
	
	public Bundle getSettings() {
		Bundle data = new Bundle();
		
		if (mDevice == null)
			return data;
		
		data.putString(DEVICE_HOST, mDevice.mBaseURL.getHost());
		data.putString(DEVICE_NAME, mName);
		
		return data;
	}
}
