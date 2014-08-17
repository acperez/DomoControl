package es.acperez.domocontrol.systems.light;

import android.os.Bundle;
import android.os.Message;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.controller.LightDevice;
import es.acperez.domocontrol.systems.light.controller.LightManager;

public class LightSystem extends DomoSystem {
	
	
	private static final int DISCONNECTIED = 0;
	private static final int CONNECTED = 1;
	private static final int READY = 2;
	
	public LightDevice mDevice;
	private int mState = DISCONNECTIED;

	public LightSystem(String systemName, String fragmentClass, Bundle settings) {
		super(systemName, fragmentClass, DomoSystem.TYPE_POWER);

		mDevice = new LightDevice();
		mDevice.importSettings(settings);
		
		this.mManager = new LightManager(mDevice);
		this.mManager.addSystemListener(this);
		
		sendRequest(LightManager.CONNECT, null, true);
	}
	
	@Override
	public void settingsUpdate() {
		sendRequest(LightManager.CONNECT, null, true);
	}

	@Override
	public Bundle getSettings() {
		return mDevice.exportSettings();
	}

	@Override
	public void requestResponse(Message msg) {
		if (mState == DISCONNECTIED && msg.what == DomoSystem.ERROR_NONE) {
			mState = CONNECTED;
			sendRequest(LightManager.GET_CONFIG, null, false);
			return;
		}
		
		if (mState == CONNECTED && msg.what == DomoSystem.ERROR_NONE) {
			mState = READY;
			mFragment.updateContent(LightDevice.UPDATE_BRIDGE);
			return;
		}
		
		if (mState == READY && msg.what == DomoSystem.ERROR_NONE) {
			mFragment.updateContent(LightDevice.UPDATE_LIGHTS);
		}
	}

	public void setLightsColor(boolean[] lights, float[] color) {
		Bundle params = new Bundle();
		params.putBooleanArray("lights", lights);
		params.putFloatArray("color", color);
		
		sendRequest(LightManager.SET_COLORS, params, false);
	}

	public void switchLights(boolean[] lights, boolean state) {
		Bundle params = new Bundle();
		params.putBooleanArray("lights", lights);
		params.putBoolean("state", state);
		
		sendRequest(LightManager.SWITCH_LIGHTS, params, false);		
	}
}
