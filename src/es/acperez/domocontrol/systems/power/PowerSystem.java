package es.acperez.domocontrol.systems.power;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.power.controller.PowerManager;

public class PowerSystem extends DomoSystem {
	public PowerData mData;

	public PowerSystem(Context context, Bundle settings, DomoSystemStatusListener listener) {
		super(context.getResources().getString(R.string.system_name_power), DomoSystem.TYPE_POWER);

		this.mData = new PowerData();
		mData.importSettings(settings);
		
		this.mManager = new PowerManager(this, mData, listener);
		
		sendRequest(PowerManager.GET_STATUS, null, true);
	}
	
	@Override
	protected SystemFragment createFragment() {
		return new PowerFragment(this);
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
		
		mFragment.updateContent(0, null);
	}
}
