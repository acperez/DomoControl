package es.acperez.domocontrol.systems.base;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public abstract class DomoSystem {
	public String mName;
	public int mType;
	
	private int mStatus;
	private int mError;
	private DomoSystemStatusListener mStatusListener;
	
	protected SystemFragment mFragment;
	
	final public static int TYPE_POWER = 0;
	final public static int TYPE_LIGHT = 1;
	
	final public static int STATUS_ONLINE = 0;
	final public static int STATUS_OFFLINE = 1;
	final public static int STATUS_LOADING = 2;
	final public static int STATUS_WARNING = 3;
	
	public static final int ERROR_NONE = 0;
	public static final int ERROR_PASSWORD = 1;
	public static final int ERROR_NETWORK = 2;
	public static final int ERROR_NOTIFY = 3;
	public static final int ERROR_PARAMS = 4;
	
	final public static String POWER_SETTINGS_NAME = "power_settings";
	final public static String POWER_SERVICE_SETTINGS_NAME = "power_service_settings";
	final public static String LIGHT_SETTINGS_NAME = "light_settings";
	final public static String LIGHT_SERVICE_SETTINGS_NAME = "light_service_settings";
	
	public interface DomoSystemStatusListener {
		public void onSystemStatusChange(DomoSystem system, int status);
	}
	
	public DomoSystem(String name, int type, DomoSystemStatusListener listener) {
		mName = name;
		mType = type;
		mStatus = DomoSystem.STATUS_LOADING;
		mError = DomoSystem.ERROR_NONE;
		mStatusListener = listener;
	}
	
	public SystemFragment getFragment() {
		if (mFragment == null) {
			mFragment = createFragment();
		}
		
		return mFragment;
	}
	
	protected void setStatus(int status) {
		if (mStatus != status) {
			mStatus = status;
			updateStatus();
			mStatusListener.onSystemStatusChange(this, status);
		}
	}

	public int getStatus() {
		return mStatus;
	}

//	protected void setError(int error) {
//		mError = error;
//	}
	
	public int getError() {
		return mError;
	}
	
	public abstract void requestResponse(Message msg);
	protected abstract SystemFragment createFragment();

	private void updateStatus() {
		getFragment().updateStatus();
	}
	
	private static class WeakRefHandler extends Handler {
		private WeakReference<DomoSystem> ref;

		public WeakRefHandler(DomoSystem domoSystem) {
			this.ref = new WeakReference<DomoSystem>(domoSystem);
		}

		@Override
		public void handleMessage(Message msg) {
			DomoSystem system = ref.get();
			
			system.mError = msg.what;
			
			int error = msg.what;
			int status = DomoSystem.STATUS_OFFLINE;

			if (error == DomoSystem.ERROR_NONE) {
				status = DomoSystem.STATUS_ONLINE;
			} else if (error == DomoSystem.ERROR_NOTIFY) {
				status = DomoSystem.STATUS_WARNING;
			}
			
			system.mError = error;
			system.setStatus(status);
			
			system.requestResponse(msg);
		}
	};

	protected WeakRefHandler mRequestListener = new WeakRefHandler(this);
}
