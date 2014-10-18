package es.acperez.domocontrol.systems.base;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class DomoSystem {
	public String name;
	public int type;
	
	protected SystemFragment mFragment;
	protected SystemManager mManager;
	
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
	final public static String LIGHT_SETTINGS_NAME = "light_settings";
		
	public DomoSystem(String name, int type) {
		this.name = name;
		this.type = type;
	}
	
	public SystemFragment getFragment() {
		if (mFragment == null) {
			mFragment = createFragment();
		}
		
		return mFragment;
	}

	public void sendRequest(int request, Bundle params, boolean showLoading) {
		mManager.sendRequest(managerHandler, request, params, showLoading);
	}
	
	private static class WeakRefHandler extends Handler {
		private WeakReference<DomoSystem> ref;

		public WeakRefHandler(DomoSystem domoSystem) {
			this.ref = new WeakReference<DomoSystem>(domoSystem);
		}

		@Override
		public void handleMessage(Message msg) {
			DomoSystem system = ref.get();
			system.mManager.setError(msg.what);
			
			int status = DomoSystem.STATUS_OFFLINE;
			if (msg.what == DomoSystem.ERROR_NONE) {
				status = DomoSystem.STATUS_ONLINE;
			} else if (msg.what == DomoSystem.ERROR_NOTIFY) {
				status = DomoSystem.STATUS_WARNING;
			}

			system.mManager.setStatus(status);
			system.requestResponse(msg);
		}
	};

	private WeakRefHandler managerHandler = new WeakRefHandler(this);

	public abstract void settingsUpdate();
	public abstract Bundle getSettings();
	public abstract void requestResponse(Message msg);
	protected abstract SystemFragment createFragment();

	public void updateStatus() {
		getFragment().updateStatus();
	}
	
	public int getStatus() {
		return mManager.getStatus();
	}
	
	public int getError() {
		return mManager.getError();
	}
}
