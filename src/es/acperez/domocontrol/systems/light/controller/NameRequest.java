package es.acperez.domocontrol.systems.light.controller;

import java.util.Map;

import android.os.Handler;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerRequest;

public class NameRequest implements LightManagerRequest {

	private Map<String, String> mNames;
	private Map<String, PHLight> mLights;
		
	public NameRequest(Map<String, String> names, Map<String, PHLight> lights) {
		mNames = names;
		mLights = lights;
	}

	@Override
	public void run(PHBridge bridge, Handler handler) {
		LightListener listener = new LightListener(mNames.size(), LightSystem.UPDATE_BRIDGE, handler);
		
		for (Map.Entry<String, String> entry : mNames.entrySet()) {
			PHLight light = mLights.get(entry.getKey());
			light.setName(entry.getValue());
			bridge.updateLight(light, listener);
		}
	}
}