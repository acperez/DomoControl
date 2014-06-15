package es.acperez.domocontrol.power.controller;

import android.os.Handler;

public class PowerManager {
	
	public static final int ERROR_NONE = 0;
	public static final int ERROR_PASSWORD = 1;
	public static final int ERROR_NETWORK = 2;
	
    public PowerManager() {
	}
    
	public void getStatus(Handler handler) {
		ConnectionManager.getInstance().push(new PowerManagerTask(handler));
	}
	
	public void setStatus(Handler handler, int plug, boolean value) {
		ConnectionManager.getInstance().push(new PowerManagerTask(handler, plug, value));
	}
}