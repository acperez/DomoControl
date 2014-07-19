package es.acperez.domocontrol.systems.base;

import java.util.ArrayList;
import java.util.Iterator;

import android.os.Bundle;
import android.os.Handler;

public abstract class SystemManager {

	private int status = DomoSystem.STATUS_OFFLINE;
	private int error;
	private ArrayList<DomoSystemStatusListener> statusListeners;
	protected int systemType;

	public interface DomoSystemStatusListener {
	    public void onSystemStatusChange(int systemType, int status);
	}
	
	public SystemManager() {
		error = DomoSystem.ERROR_NONE;
		statusListeners = new ArrayList<DomoSystemStatusListener>();
	}
	
	public void addSystemListener(DomoSystemStatusListener listener) {
		statusListeners.add(listener);
	}

	public void removeSystemListener(DomoSystemStatusListener listener) {
		statusListeners.remove(listener);
	}
	
	public void setStatus(int status) {
		boolean notify = this.status != status;
		this.status = status;
		
		if (notify) {
			Iterator<DomoSystemStatusListener> iterator = statusListeners.iterator();
			while (iterator.hasNext()) {
				iterator.next().onSystemStatusChange(this.systemType, this.status);
			}
		}
	}
	
	public int getStatus() {
		return status;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	public int getError() {
		return this.error;
	}
	
	public void sendRequest(Handler handler, int request, Bundle params, boolean showLoading) {
		if (showLoading)
			setStatus(DomoSystem.STATUS_LOADING);
		
		processRequest(handler, request,params);
	};

	public abstract void processRequest(Handler handler, int request, Bundle params);
}