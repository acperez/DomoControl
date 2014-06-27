package es.acperez.domocontrol.systems.empty;

import android.os.Bundle;
import android.os.Message;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class EmptySystem extends DomoSystem {

	public EmptySystem(String name, String fragmentClass) {
		super(name, fragmentClass, DomoSystem.TYPE_EMPTY);
	}

	@Override
	public void settingsUpdate() {
	}

	@Override
	public Bundle getSettings() {
		return null;
	}

	@Override
	public int getStatus() {
		return DomoSystem.STATUS_OFFLINE;
	}

	@Override
	public void requestResponse(Message msg) {
	}

}
