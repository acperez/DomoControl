package es.acperez.domocontrol.power.controller;

import android.os.Handler;
import es.acperez.domocontrol.common.ConnectionManager;
import es.acperez.domocontrol.common.DomoSystem.SystemManager;

public class PowerManager implements SystemManager {
	
	public static final int GET_STATUS = 0;
	
	private ConnectionManager connectionManager = null;

	public PowerManager() {
		connectionManager = ConnectionManager.getInstance();
	}
	
	// Send request to remote device in order to update the plugs status
	private void sendRequestGetPlugsStatus(Handler handler) {
		connectionManager.push(new PowerManagerTask(handler));
	}
	
	// Send request to remote device in order to switch a plug
	private void sendRequestSwitchPlug(Handler handler, int plug, boolean value) {
		connectionManager.push(new PowerManagerTask(handler, plug, value));
	}

	@Override
	public void sendRequest(Handler handler, int request) {
		switch (request) {
			case GET_STATUS:
				sendRequestGetPlugsStatus(handler);
				break;
		}
	}
}