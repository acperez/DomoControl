package es.acperez.domocontrol.common;

import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;

public class EmptyData extends SystemData {

	@Override
	public void setStatus(int status) {
	}

	@Override
	public void setError(int error) {
	}

	@Override
	public int getStatus() {
		return DomoSystem.STATUS_OFFLINE;
	}

	@Override
	public void addSystemListener(DomoSystemStatusListener listener) {
	}

	@Override
	public void removeSystemListener(DomoSystemStatusListener listener) {
	}
}