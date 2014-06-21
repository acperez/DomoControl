package es.acperez.domocontrol.common;

import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;

public abstract class SystemData {

	public abstract void setStatus(int status);
	public abstract void setError(int error);
	public abstract int getStatus();
	public abstract void addSystemListener(DomoSystemStatusListener listener);
	public abstract void removeSystemListener(DomoSystemStatusListener listener);
	
}
