package es.acperez.domocontrol.systems.power;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.power.controller.PowerEventManager;
import es.acperez.domocontrol.systems.power.service.PowerService;
import es.acperez.domocontrol.systems.power.service.PowerService.PowerBinder;

public class PowerSystem extends DomoSystem {

	final public static String ALARMS = "power.alarms";	
	final public static String SERVER = "power.host";
	final public static String SERVER_PORT = "power.port";
	final public static String PASSWORD = "power.password";
	final public static String SOCKET_NAME_1 = "power.socket.name.1";
	final public static String SOCKET_NAME_2 = "power.socket.name.2";
	final public static String SOCKET_NAME_3 = "power.socket.name.3";
	final public static String SOCKET_NAME_4 = "power.socket.name.4";
	final public static String SOCKET_STATUS_1 = "power.socket.status.1";
	final public static String SOCKET_STATUS_2 = "power.socket.status.2";
	final public static String SOCKET_STATUS_3 = "power.socket.status.3";
	final public static String SOCKET_STATUS_4 = "power.socket.status.4";
	
	public PowerData mData;
	private PowerService mService;
	
	private PowerEventManager mEventMAnager;

	public PowerSystem(Context context, DomoSystemStatusListener listener) {
		super(context.getResources().getString(R.string.system_name_power), DomoSystem.TYPE_POWER, listener);

		this.mData = new PowerData(context);
		mData.importSettings();
		
		Intent intent = new Intent(context, PowerService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        
		mEventMAnager = new PowerEventManager(context, mData);
	}
	
	@Override
	protected SystemFragment createFragment() {
		return new PowerFragment(this);
	}

	public void requestPlugSwitch(int plug) {
		Bundle params = new Bundle();
		params.putInt("plug", plug);
		params.putBoolean("value", !mData.mSockets[plug]);
		
		mService.setStatus(mRequestListener, plug, !mData.mSockets[plug]);
	}
	
	public void connect(String server, int port, String password) {
		mService.setSettings(server, port, password);
		mService.getStatus(mRequestListener);
	}

	@Override
	public void requestResponse(Message msg) {
		boolean[] plugStatus = (boolean[])msg.obj;
		if (plugStatus != null)
			mData.mSockets = plugStatus;
		
		mFragment.updateContent(0, null);
	}
	
	protected ArrayList<PowerEvent> getEvents() {
		return mEventMAnager.getAllEvents();
	}

	public void deleteEvent(PowerEvent event) {
		mEventMAnager.deleteEvent(event);
	}

	public int addEvent(PowerEvent event) {
		return mEventMAnager.addEvent(event);
	}

	public void enableAlarms(boolean action) {
		mEventMAnager.enableAlarms(action);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PowerBinder binder = (PowerBinder) service;
            mService = binder.getService();
            
            updateSettings();
            mService.getStatus(mRequestListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    
    public void updateSettings() {
    	mFragment.updateServiceSettings(mService.getSettings());
    }
}