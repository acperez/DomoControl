package es.acperez.domocontrol.systems.power.controller;

import android.os.Bundle;
import android.os.Handler;
import es.acperez.domocontrol.common.connectionManager.ConnectionManager;
import es.acperez.domocontrol.systems.base.SystemManager;
import es.acperez.domocontrol.systems.power.PowerData;
import es.acperez.domocontrol.systems.power.PowerSystem;

public class PowerManager extends SystemManager {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
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
	
	private ConnectionManager connectionManager = null;
	private PowerData mData;

	public PowerManager(PowerSystem system, PowerData data, DomoSystemStatusListener listener) {
		super(system, listener);
		
		connectionManager = ConnectionManager.getInstance();
		mData = data;
	}

	// Send request to remote device in order to update the plugs status
	private void sendRequestGetPlugsStatus(Handler handler) {
		connectionManager.push(new PowerManagerTask(handler, mData.mServer, mData.mPort, mData.mPassword));
	}
	
	// Send request to remote device in order to switch a plug
	private void sendRequestSwitchPlug(Handler handler, int plug, boolean value) {
		connectionManager.push(new PowerManagerTask(handler, mData.mServer, mData.mPort, mData.mPassword, plug, value));
	}

	@Override
	public void processRequest(Handler handler, int request, Bundle params) {
		switch (request) {
			case GET_STATUS:
				sendRequestGetPlugsStatus(handler);
				break;
			case SET_STATUS:
				sendRequestSwitchPlug(handler, params.getInt("plug"), params.getBoolean("value"));
				break;
		}
	}
}