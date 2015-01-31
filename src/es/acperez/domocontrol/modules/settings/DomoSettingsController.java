package es.acperez.domocontrol.modules.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.DomoService.DomoServiceBinder;
import es.acperez.domocontrol.services.DomoService.ServiceStatusListener;

public abstract class DomoSettingsController extends DomoController implements ServiceStatusListener {
	protected DomoService mService;
	private int mError;
	private DomoSystemStatusListener mStatusListener;
	private int mServiceType;

	public DomoSettingsController(Context context, int id, String name, Class<?> serviceClass, int serviceType, DomoSystemStatusListener listener) {
		super(id, name, DomoController.TYPE_SETTINGS);
		mError = DomoSettingsController.ERROR_NONE;
		mStatusListener = listener;
		mServiceType = serviceType;
		
		launchService(context, serviceClass, mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	DomoServiceBinder binder = (DomoServiceBinder) service;
            mService = binder.getService();
            mService.start(mServiceType, DomoSettingsController.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    
	public Bundle getServiceSettings() {
		return mService.getSettings();
	}
	
	public void setServiceSettings(Bundle settings) {
		setStatus(STATUS_LOADING);
		mService.setSettings(settings);
	}
	
	@Override
	public void onStop() {
		mService.disconnect();
	}

	public int getError() {
		return mError;
	}
	
	protected void setStatus(int status) {
		if (mStatus != status) {
			mStatus = status;
			((DomoSettingsFragment)getFragment()).updateStatus();
			mStatusListener.onSystemStatusChange(this, status);
		}
	}
	
	// ServiceListener methods
	
	@Override
	public void onServiceStarted(int serviceId) {
		onStatusReceived(DomoSettingsController.ERROR_NONE);
		mService.getSwitches();
	}
	
	@Override
	public void onRequestError(int serviceId, int result) {
		onStatusReceived(result);
	}

	private void onStatusReceived(int result) {
		mError = result;
		
		int status = DomoSettingsController.STATUS_OFFLINE;

		if (mError == DomoSettingsController.ERROR_NONE) {
			status = DomoSettingsController.STATUS_ONLINE;
		} else if (mError == DomoSettingsController.ERROR_NOTIFY) {
			status = DomoSettingsController.STATUS_WARNING;
		}
		
		setStatus(status);
	}
}
