package es.acperez.domocontrol.services.philips.hue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.services.philips.hue.DomoLight;

public class LightStateListener implements PHLightListener {
	private int mRequests = 0;
	private Handler mListener;
	private ArrayList<DomoLight> mLights;
	private Map<String, PHLight> mHueLights;

	public LightStateListener(Map<String, PHLight> lights, int requests, Handler handler) {
		mHueLights = lights;
		mRequests = requests;
		mListener = handler;
		mLights = new ArrayList<DomoLight>();
	}
	
	private void onResponse() {
		mRequests--;
		if (mRequests > 0)
			return;
		
		Message msg = Message.obtain(mListener, 0, mLights);
		mListener.sendMessage(msg);
	}
	
	@Override
	public void onError(int arg0, String arg1) {
		System.err.println("Error: " + arg0 + " " + arg1);
		onResponse();
	}

	@Override
	public void onSuccess() {
		onResponse();
	}

	@Override
	public void onStateUpdate(Map<String, String> lights, List<PHHueError> arg1) {
		String key = lights.keySet().iterator().next();
		String id = key.split("/")[2];
		
		PHLight light = mHueLights.get(id);
		mLights.add(new DomoLight(Integer.valueOf(id), light.getLastKnownLightState().isOn(),
					light.getName(), 1, LightUtils.createThumb(light)));
	}

	@Override
	public void onReceivingLightDetails(PHLight arg0) {
		System.out.println("hola listener");
	}

	@Override
	public void onReceivingLights(List<PHBridgeResource> arg0) {
		System.out.println("hola listener");
	}

	@Override
	public void onSearchComplete() {
		System.out.println("hola listener");
	}
}
