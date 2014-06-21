package es.acperez.domocontrol.common;

import java.lang.ref.WeakReference;

import es.acperez.domocontrol.ContentFragment;
import es.acperez.domocontrol.EmptyManager;
import es.acperez.domocontrol.power.PowerData;
import es.acperez.domocontrol.power.SystemFragment;
import es.acperez.domocontrol.power.controller.PowerManager;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;

public class DomoSystem {
	public String name;
	private String fragmentClass;
	private SystemFragment mFragment;
	public int type;
	public SystemManager mManager;
	public SystemData mSystemData;
	
	final public static int TYPE_POWER = 0;
	final public static int TYPE_EMPTY = 1;
	
	final public static int STATUS_ONLINE = 0;
	final public static int STATUS_OFFLINE = 1;
	final public static int STATUS_LOADING = 2;
	
	public static final int ERROR_NONE = 0;
	public static final int ERROR_PASSWORD = 1;
	public static final int ERROR_NETWORK = 2;
	
	public interface DomoSystemStatusListener {
	    public void onSystemStatusChange(int systemType, int status);
	}
	
	public interface SystemManager {
		void sendRequest(Handler handler, int request);
	}
	
	public DomoSystem(String name, String fragmentClass, int type) throws Exception {
		switch (type) {
			case TYPE_POWER:
				this.mManager = new PowerManager();
				this.mSystemData = new PowerData();
				break;
			case TYPE_EMPTY:
				this.mManager = new EmptyManager();
				this.mSystemData = new EmptyData();
				break;
			default:
				throw new Exception("Invalid system type");
		}
		
		this.name = name;
		this.fragmentClass = fragmentClass;
		this.type = type;
	}
	
	public Fragment getFragment() {
		if (mFragment == null) {
			try {
				Class<?> claz = ContentFragment.class.getClassLoader().loadClass(fragmentClass);
				mFragment = (SystemFragment)claz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return mFragment;
	}
	
	public int getStatus() {
		return mSystemData.getStatus();
	}

	public void addSystemListener(DomoSystemStatusListener listener) {
		mSystemData.addSystemListener(listener);
	}
	
	public void removeSystemListener(DomoSystemStatusListener listener) {
		mSystemData.removeSystemListener(listener);
	}

	public void sendRequest(int request) {
		mSystemData.setStatus(DomoSystem.STATUS_LOADING);
		mManager.sendRequest(managerHandler, request);
	}
	
	private static class WeakRefHandler extends Handler {
		private WeakReference<DomoSystem> ref;

		public WeakRefHandler(DomoSystem domoSystem) {
			this.ref = new WeakReference<DomoSystem>(domoSystem);
		}

		@Override
		public void handleMessage(Message msg) {
			DomoSystem system = ref.get();
			system.mSystemData.setError(msg.what);
			int status = msg.what == DomoSystem.ERROR_NONE ? DomoSystem.STATUS_ONLINE : DomoSystem.STATUS_OFFLINE;
			system.mSystemData.setStatus(status);

			system.mFragment.updateStatus(status, msg.what);
		}
	};

	private WeakRefHandler managerHandler = new WeakRefHandler(this);
}
