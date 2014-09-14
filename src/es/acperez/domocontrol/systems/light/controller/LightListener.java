package es.acperez.domocontrol.systems.light.controller;

import java.util.List;
import java.util.Map;

import android.os.Handler;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

public class LightListener implements PHLightListener{
	private int mRequests = 0;
	private Handler mListener;

	public LightListener(int requests, Handler handler) {
		mRequests = requests;
		mListener = handler;
	}
	
	private void onResponse() {
		mRequests--;
		if (mRequests > 0)
			return;
		
		mListener.sendEmptyMessage(0);
	}
	
	@Override
	public void onError(int arg0, String arg1) {
		System.err.println("error: " + arg0 + " " + arg1);
		onResponse();
	}

	@Override
	public void onSuccess() {}

	@Override
	public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
		onResponse();
	}

	@Override
	public void onReceivingLightDetails(PHLight arg0) {}

	@Override
	public void onReceivingLights(List<PHBridgeResource> arg0) {}

	@Override
	public void onSearchComplete() {}
}
