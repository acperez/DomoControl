package es.acperez.domocontrol.modules.base;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import es.acperez.domocontrol.modules.settings.DomoSettingsController;

public abstract class DomoController {
	public String mName;
	public int mType;
	public int mId;
	protected int mStatus;
	
	protected Fragment mFragment;
		
	final public static int TYPE_SETTINGS = 0x00000001;
	final public static int TYPE_MONITOR =  0x00000002;
	final public static int TYPE_ACTION =   0x00000004;
	
	final public static int STATUS_NONE = 0;
	final public static int STATUS_ONLINE = 1;
	final public static int STATUS_OFFLINE = 2;
	final public static int STATUS_LOADING = 3;
	final public static int STATUS_WARNING = 4;
	
	public static final int ERROR_NONE = 0;
	public static final int ERROR_AUTH = 1;
	public static final int ERROR_NETWORK = 2;
	public static final int ERROR_NOTIFY = 3;
	public static final int ERROR_PARAMS = 4;
	public static final int ERROR_FIND = 5;
	
	final public static String SYSTEM_LIST_NAME = "system_list_settings";
	final public static String EVENT_SETTINGS_NAME = "event_settings";
	final public static String POWER_SETTINGS_NAME = "power_settings";
	final public static String POWER_SERVICE_SETTINGS_NAME = "power_service_settings";
	final public static String LIGHT_SETTINGS_NAME = "light_settings";
	final public static String LIGHT_SERVICE_SETTINGS_NAME = "light_service_settings";
	final public static String WEMO_SETTINGS_NAME = "wemo_settings";
	
	public abstract void onStop();
	
	public interface DomoSystemStatusListener {
		public void onSystemStatusChange(DomoController system, int status);
	}
	
	public DomoController(int id, String name, int type) {
		mId = id;
		mName = name;
		mType = type;
		if (type == TYPE_SETTINGS)
			mStatus = DomoSettingsController.STATUS_LOADING;
		else
			mStatus = DomoSettingsController.STATUS_NONE;
	}
	
	public abstract Fragment createFragment();
	
	public Fragment getFragment() {
		if (mFragment == null)
			mFragment = createFragment();
		
		return mFragment;
	}
	
	public int getStatus() {
		return mStatus;
	}
	
	protected boolean launchService(Context context, Class<?> serviceClass, ServiceConnection connection) {
		Intent intent = new Intent(context, serviceClass);
        return context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}
}
