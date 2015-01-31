package es.acperez.domocontrol.services.philips.hue;

import java.util.ArrayList;
import java.util.Map;

import android.os.Handler;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import es.acperez.domocontrol.services.philips.hue.DomoLight;
import es.acperez.domocontrol.services.philips.hue.LightService.LightSystemRequest;

public class LightRequest implements LightSystemRequest {

	private ArrayList<DomoLight> mLights;
	private PHLightState mState;
	private Handler mHandler;
	
	public LightRequest(ArrayList<DomoLight> lights, float[] color, Handler handler) {
		mState = LightUtils.createColorState(color);
		mLights = lights;
		mHandler = handler;
	}
	
	@Override
	public void run(PHBridge bridge) {
		Map<String, PHLight> lights = bridge.getResourceCache().getLights();
		LightStateListener listener = new LightStateListener(lights, mLights.size(), mHandler);

		for (int i = 0; i < mLights.size(); i++) {
			bridge.updateLightState(String.valueOf((mLights.get(i).id)), mState, listener);
		}
	}
}