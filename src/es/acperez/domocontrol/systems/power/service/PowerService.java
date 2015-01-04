package es.acperez.domocontrol.systems.power.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import es.acperez.domocontrol.common.connectionManager.ConnectionManager;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;
import es.acperez.domocontrol.systems.power.controller.PowerManagerTask;

public class PowerService extends Service {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private final IBinder mBinder = new PowerBinder();
	
	private PowerServiceData mData;
	private ConnectionManager mConnectionManager = null;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class PowerBinder extends Binder {
		public PowerService getService() {
            return PowerService.this;
        }
    }
	
	@Override
	public void onCreate() {
		super.onCreate();

		mData = new PowerServiceData(this);
		mData.importSettings();
		
		mConnectionManager = ConnectionManager.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int socket = intent.getIntExtra(PowerAlarm.ALARM_SOCKET, 0);
		boolean action = intent.getBooleanExtra(PowerAlarm.ALARM_ACTION, false);
		setStatus(null, socket, action);
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}

	public PowerServiceData getSettings() {
		return mData;
	}
	
	public void setSettings(String server, int port, String password) {
		mData.mServer = server;
		mData.mPort = port;
		mData.mPassword = password;
		mData.exportSettings();
	}
	
	// Send request to remote device in order to update the plugs status
	public void getStatus(Handler handler) {
		mConnectionManager.push(new PowerManagerTask(handler, mData.mServer, mData.mPort, mData.mPassword));
	}
	
	// Send request to remote device in order to switch a plug
	public void setStatus(Handler handler, int plug, boolean value) {
		mConnectionManager.push(new PowerManagerTask(handler, mData.mServer, mData.mPort, mData.mPassword, plug, value));
	}
}