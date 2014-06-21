package es.acperez.domocontrol;

import android.os.Handler;
import es.acperez.domocontrol.common.DomoSystem;
import es.acperez.domocontrol.common.DomoSystem.SystemManager;

public class EmptyManager implements SystemManager {
	
	public int getStatus() {
		return DomoSystem.STATUS_OFFLINE;
	}

	@Override
	public void sendRequest(Handler handler, int request) {
	}
}