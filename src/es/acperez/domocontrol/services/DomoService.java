package es.acperez.domocontrol.services;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.base.DomoController;

public abstract class DomoService extends Service {
	
	public static final int SERVICE_CONNECTION = 0;
	public static final int SERVICE_GET_SWITCHES = 1;
	public static final int SERVICE_GET_STATUS = 2;
	public static final int SERVICE_SET_STATUS = 3;
	public static final int SERVICE_SWITCH_EDIT_NAME = 4;
	
	public static final int SERVICE_TYPE_GEMBIRD = 0;
	public static final int SERVICE_TYPE_PHILIPS_HUE = 1;
	public static final int SERVICE_TYPE_WEMO = 2;
	
	protected int mServiceId;
	protected ArrayList<ServiceListener> mServiceListener;
	protected ServiceStatusListener mStatusListener;
	private final IBinder mBinder = new DomoServiceBinder();
	private boolean mConnected = false;

	public interface ServiceListener {
		public void onServiceStarted(int serviceId);
		public void onGetSwitches(int serviceId, ArrayList<DomoSwitch> switches);
		public void onRequestError(int serviceId, int result);
		public void onSwitchAction(int serviceId, int id, boolean status);
		public void onSwitchNameEdited(int serviceId, int id, String name);
	}
	
	public interface ServiceStatusListener {
		public void onServiceStarted(int serviceId);
		public void onRequestError(int serviceId, int result);
	}

	public abstract Bundle getSettings();
	protected abstract void saveSettings(Bundle settings);
	protected abstract void doConnect(Handler handler);
	protected abstract void doGetSwitches(Handler handler);
	protected abstract ArrayList<DomoSwitch> doGetSwitchesResponse(ServiceMessage message);
	protected abstract void doGetStatus(Handler handler, int plugId);
	protected abstract void doSetStatus(Handler handler, int plugId, boolean status);
	protected abstract boolean doStatusResponse(ServiceMessage message);
	protected abstract void doEditSwitchName(Handler handler, int id, String name);
	protected abstract String doEditSwitchNameResponse(ServiceMessage message);
	
	public class DomoServiceBinder extends Binder {
		public DomoService getService() {
            return DomoService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mServiceId = getServiceType();
		mServiceListener = new ArrayList<ServiceListener>();
	}
	
	public void start(int serviceId, ServiceStatusListener listener) {
		mServiceId = serviceId;
		mStatusListener = listener;
		doConnect(mRemoteHandler);
	}
	
	public void setSettings(Bundle settings) {
		saveSettings(settings);
		doConnect(mRemoteHandler);
	}

	public void getSwitches() {
		doGetSwitches(mRemoteHandler);
	}
	
	public void getStatus(int plugId) {
		doGetStatus(mRemoteHandler, plugId);
	}
	
	public void setStatus(int plugId, boolean status) {
		doSetStatus(mRemoteHandler, plugId, status);
	}
	
	public void editSwitchName(int plugId, String name) {
		doEditSwitchName(mRemoteHandler, plugId, name);
	}
	
	protected static class WeakRefHandler extends Handler {
		private WeakReference<DomoService> ref;

		public WeakRefHandler(DomoService domoService) {
			this.ref = new WeakReference<DomoService>(domoService);
		}

		@Override
		public void handleMessage(Message msg) {
			DomoService service = ref.get();
			
			ServiceMessage message = (ServiceMessage)msg.obj;
			
			if (message.result != DomoController.ERROR_NONE) {
				Iterator<ServiceListener> iterator = service.mServiceListener.iterator();
				while (iterator.hasNext())
					iterator.next().onRequestError(service.mServiceId, message.result);
				
				service.mStatusListener.onRequestError(service.mServiceId, message.result);

				return;
			}
						
			Iterator<ServiceListener> iterator = service.mServiceListener.iterator();
			
			switch (msg.what) {
				case SERVICE_CONNECTION:
					while (iterator.hasNext())
						iterator.next().onServiceStarted(service.mServiceId);
					
					service.mConnected = true;
					service.mStatusListener.onServiceStarted(service.mServiceId);
					break;
					
				case SERVICE_GET_SWITCHES:
					ArrayList<DomoSwitch> switches = service.doGetSwitchesResponse(message);
					while (iterator.hasNext())
						iterator.next().onGetSwitches(service.mServiceId, switches);
					break;
					
				case SERVICE_GET_STATUS:
				case SERVICE_SET_STATUS:
					boolean status = service.doStatusResponse(message);
					while (iterator.hasNext())
						iterator.next().onSwitchAction(service.mServiceId, message.id, status);
					break;
					
				case SERVICE_SWITCH_EDIT_NAME:
					String name = service.doEditSwitchNameResponse(message);
					while (iterator.hasNext())
						iterator.next().onSwitchNameEdited(service.mServiceId, message.id, name);
					break;
			}
		}
	};

	protected WeakRefHandler mRemoteHandler = new WeakRefHandler(this);
	
	public void addListener(ServiceListener listener) {
		mServiceListener.add(listener);
	}

	protected abstract void onDisconnect();
	
	public final void disconnect() {
		onDisconnect();
		mConnected = false;
	}
	
	public abstract int getServiceType ();
	
	public boolean isConnected() {
		return mConnected;
	}
}
