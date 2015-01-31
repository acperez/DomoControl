package es.acperez.domocontrol.services.philips.hue;

import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.ServiceMessage;

public class LightNameListener implements PHLightListener {
	private Handler mListener;
	private int mLightId;
	private String mName;

	public LightNameListener(Handler handler, int lightId, String name) {
		mListener = handler;
		mLightId = lightId;
		mName = name;
	}
	
	@Override
	public void onError(int arg0, String arg1) {
		System.err.println("Error: " + arg0 + " " + arg1);
	}

	@Override
	public void onSuccess() {
		Message msg = Message.obtain(mListener, DomoService.SERVICE_SWITCH_EDIT_NAME, new ServiceMessage(DomoController.ERROR_NONE, mLightId, mName));
		mListener.sendMessage(msg);
	}

	@Override
	public void onStateUpdate(Map<String, String> lights, List<PHHueError> arg1) {}

	@Override
	public void onReceivingLightDetails(PHLight arg0) {}

	@Override
	public void onReceivingLights(List<PHBridgeResource> arg0) {}

	@Override
	public void onSearchComplete() {}
}
