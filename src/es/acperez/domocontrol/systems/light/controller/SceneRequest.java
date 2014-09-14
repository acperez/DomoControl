package es.acperez.domocontrol.systems.light.controller;

import java.util.List;

import android.os.Handler;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerRequest;

public class SceneRequest implements LightManagerRequest{

	private Scene mScene;
	
	public SceneRequest(Scene scene) {
		mScene = scene;
	}
	
	@Override
	public void run(PHBridge bridge, Handler handler) {
		List<PHLightState> states = mScene.states;
		List<PHLight> lights = bridge.getResourceCache().getAllLights();
		if (states.size() < lights.size()) {
			PHLightState state = states.get(states.size() - 1);
			for (int i = states.size(); i < lights.size(); i++) {
				states.add(state);
			}
		}
		
		LightListener listener = new LightListener(lights.size(), handler);
		
		for (int i = 0; i < lights.size(); i++) {
			bridge.updateLightState(lights.get(i), states.get(i), listener);
		}
	}
}