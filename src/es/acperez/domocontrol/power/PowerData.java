package es.acperez.domocontrol.power;

import java.util.ArrayList;
import java.util.Iterator;

import es.acperez.domocontrol.common.DomoSystem;
import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;
import es.acperez.domocontrol.common.SystemData;

public class PowerData extends SystemData {
	private int status = DomoSystem.STATUS_OFFLINE;
	private int error;
	public boolean plugs[];
	
	private ArrayList<DomoSystemStatusListener> statusListeners;

	public PowerData() {
		statusListeners = new ArrayList<DomoSystemStatusListener>();
		error = DomoSystem.ERROR_NONE;
		plugs = new boolean[4];
	}
	
	@Override
	public void addSystemListener(DomoSystemStatusListener listener) {
		statusListeners.add(listener);
	}

	@Override
	public void removeSystemListener(DomoSystemStatusListener listener) {
		statusListeners.remove(listener);
	}
	
	public void setStatus(int status) {
		boolean notify = this.status != status;
		this.status = status;
		
		if (notify) {
			Iterator<DomoSystemStatusListener> iterator = statusListeners.iterator();
			while (iterator.hasNext()) {
				iterator.next().onSystemStatusChange(DomoSystem.TYPE_POWER, this.status);
			}
		}
	}
	
	@Override
	public int getStatus() {
		System.out.println("getstatus powerdata");
		return status;
	}

	@Override
	public void setError(int error) {
		this.error = error;
	}
	
	public int getError() {
		return this.error;
	}
}