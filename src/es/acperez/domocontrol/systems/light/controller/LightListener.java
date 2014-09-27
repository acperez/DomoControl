package es.acperez.domocontrol.systems.light.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

public class LightListener implements PHLightListener {
	private int mRequests = 0;
	private Handler mListener;
	private ArrayList<String> mLightIds;
	private int mType;

	public LightListener(int requests, int type, Handler handler) {
		mRequests = requests;
		mListener = handler;
		mLightIds = new ArrayList<String>();
		mType = type;
	}
	
	private void onResponse() {
		mRequests--;
		if (mRequests > 0)
			return;
		
		Message msg = mListener.obtainMessage(mType, mLightIds);
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
		for (Map.Entry<String, String> light : lights.entrySet()) {
			String[] path = light.getKey().split("/");
			if (!mLightIds.contains(path[2])) {
				mLightIds.add(path[2]);
			}
		}
	}

	@Override
	public void onReceivingLightDetails(PHLight arg0) {}

	@Override
	public void onReceivingLights(List<PHBridgeResource> arg0) {}

	@Override
	public void onSearchComplete() {}
}
