package es.acperez.domocontrol.services.philips.hue;

import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.ServiceMessage;
import es.acperez.domocontrol.services.philips.hue.LightService.LightSystemRequest;

public class LightSwitchRequest implements LightSystemRequest, PHLightListener {
	
	private String mLight;
	private PHLightState mState;
	private Handler mHandler;
	private int id;

	public LightSwitchRequest(String light, boolean status, Handler handler) {
		mState = LightUtils.createSwitchState(status);
		mLight = light;
		id = Integer.valueOf(light);
		mHandler = handler;
	}

	public LightSwitchRequest(String light, PHLightState state, Handler handler) {
		mState = state;
		mLight = light;
		id = Integer.valueOf(light);
		mHandler = handler;
	}
	
	@Override
	public void run(PHBridge bridge) {
		bridge.updateLightState(mLight, mState, this);
	}

	@Override
	public void onError(int arg0, String arg1) {
		System.out.println("Switch light error");
		System.out.println(arg0);
		System.out.println(arg1);
		ServiceMessage serviceMessage = new ServiceMessage(DomoController.ERROR_NETWORK, id, mState.isOn());
		Message message = Message.obtain(mHandler, DomoService.SERVICE_SET_STATUS, serviceMessage);
		mHandler.sendMessage(message);
	}

	@Override
	public void onSuccess() {
		ServiceMessage serviceMessage = new ServiceMessage(DomoController.ERROR_NONE, id, mState.isOn());
		Message message = Message.obtain(mHandler, DomoService.SERVICE_SET_STATUS, serviceMessage);
		mHandler.sendMessage(message);
	}
	
	@Override
	public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {}

	@Override
	public void onReceivingLightDetails(PHLight arg0) {}

	@Override
	public void onReceivingLights(List<PHBridgeResource> arg0) {}

	@Override
	public void onSearchComplete() {}
}