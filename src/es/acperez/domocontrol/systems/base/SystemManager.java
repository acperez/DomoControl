package es.acperez.domocontrol.systems.base;

import android.os.Bundle;
import android.os.Handler;

public abstract class SystemManager {

	private int status = DomoSystem.STATUS_OFFLINE;
	private int error;
	private DomoSystemStatusListener statusListener;
	private DomoSystem mSystem;

	public interface DomoSystemStatusListener {
		public void onSystemStatusChange(DomoSystem system, int status);
	}
	
	public SystemManager(DomoSystem system, DomoSystemStatusListener listener) {
		error = DomoSystem.ERROR_NONE;
		mSystem = system;
		statusListener = listener;
	}
	
	public void setStatus(int status) {
		boolean notify = this.status != status;
		this.status = status;
		
		if (notify) {
			statusListener.onSystemStatusChange(mSystem, status);
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