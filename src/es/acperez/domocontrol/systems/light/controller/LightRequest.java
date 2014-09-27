package es.acperez.domocontrol.systems.light.controller;

import java.util.ArrayList;

import android.os.Handler;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLightState;

import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerRequest;

public class LightRequest implements LightManagerRequest {

	private ArrayList<String> mLights;
	private PHLightState mState;
	
	public LightRequest(ArrayList<String> lights, float[] color) {
		mState = LightUtils.createColorState(color);
		mLights = lights;
	}

	public LightRequest(ArrayList<String> lights, boolean status) {
		mState = LightUtils.createSwitchState(status);
		mLights = lights;
	}
	
	@Override
	public void run(PHBridge bridge, Handler handler) {
		LightListener listener = new LightListener(mLights.size(), LightSystem.UPDATE_LIGHTS, handler);
		
		for (int i = 0; i < mLights.size(); i++) {
			bridge.updateLightState(mLights.get(i), mState, listener);
		}
	}
}