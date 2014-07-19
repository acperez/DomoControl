package es.acperez.domocontrol.systems.power;

import android.os.Bundle;
import android.os.Message;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.power.controller.PowerManager;

public class PowerSystem extends DomoSystem {
	public PowerData mData;

	public PowerSystem(String systemName, String fragmentClass, Bundle settings) {
		super(systemName, fragmentClass, DomoSystem.TYPE_POWER);

		this.mData = new PowerData();
		mData.importSettings(settings);
		
		this.mManager = new PowerManager(mData);
		this.mManager.addSystemListener(this);
		
		sendRequest(PowerManager.GET_STATUS, null, true);
	}

	public void requestPlugSwitch(int plug) {
		Bundle params = new Bundle();
		params.putInt("plug", plug);
		params.putBoolean("value", !mData.mSockets[plug]);
		
		sendRequest(PowerManager.SET_STATUS, params, false);
	}
	
	@Override
	public void settingsUpdate() {
		sendRequest(PowerManager.GET_STATUS, null, true);
	}

	@Override
	public Bundle getSettings() {
		return mData.exportSettings();
	}

	@Override
	public void requestResponse(Message msg) {
		boolean[] plugStatus = (boolean[])msg.obj;
		if (plugStatus != null)
			mData.mSockets = plugStatus;
		
		mFragment.updateContent();
	}
}
