package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.light.controller.LightManager;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerListener;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerRequest;
import es.acperez.domocontrol.systems.light.controller.LightRequest;
import es.acperez.domocontrol.systems.light.controller.Scene;
import es.acperez.domocontrol.systems.light.controller.LightDbHelper;

public class LightSystem extends DomoSystem implements LightManagerListener {
	public static final int UPDATE_BRIDGE = 0;
	public static final int UPDATE_LIGHTS = 1;
	public static final int REMOTE_UPDATE_LIGHTS = 2;
	public static final int UPDATE_SETTINGS = 3;
	
	LightDbHelper mDbHelper;
	
	public LightData mData;
	private boolean mWidgetContext = false;

	public LightSystem(Context context, Bundle settings, DomoSystemStatusListener listener, boolean widgetContext) {
		super(context.getResources().getString(R.string.system_name_light), DomoSystem.TYPE_LIGHT);

		mWidgetContext = widgetContext;
		mData = new LightData(settings);
		
		this.mManager = new LightManager(this, mData, listener);
		
		sendRequest(LightManager.CONNECT, null, true);
		
		mDbHelper = new LightDbHelper(context);
	}

	@Override
	protected SystemFragment createFragment() {
		return new LightFragment(this);
	}
	
	@Override
	public void settingsUpdate() {
		sendRequest(LightManager.CONNECT, null, true);
	}

	@Override
	public Bundle getSettings() {
		return mData.exportSettings();
	}

	@Override
	public void requestResponse(Message msg) {
		if (msg.arg1 == LightSystem.UPDATE_SETTINGS) {
			connected();
			msg.arg1 = LightSystem.UPDATE_BRIDGE;
		}
		
		if (!mWidgetContext)
			mFragment.updateContent(msg.arg1, null);
	}
	
	public void connected() {
		Bundle settings = mData.exportSettings();
		DomoControlApplication.savePreferences(settings, DomoSystem.LIGHT_SETTINGS_NAME);
	}
	
	public List<PHLight> getAllLights() {
		return ((LightManager)mManager).getAllLights();
	}
	
	public Map<String, PHLight> getLights() {
		return ((LightManager)mManager).getLights();
	}

	public void updateLights(LightManagerRequest request) {
		((LightManager)mManager).updateLights(request);
	}
	
	@Override
	public void onLightRequestDone(int type, ArrayList<String> lightIds) {
		if (!mWidgetContext) {
			mFragment.updateContent(type, lightIds);
			return;
		}
		
		sendRequest(LightManager.DISCONNECT, null, false);
	}
	
	public ArrayList<Scene> getScenes() {
		return mDbHelper.getAllScenes();
	}
	
	public void saveScene(Scene scene) {
		mDbHelper.insertScene(scene);
	}

	public void deleteScene(Scene scene) {
		mDbHelper.deleteScene(scene);
	}

	public void widgetSwitch(boolean state) {
		Map<String, PHLight> lights = getLights();
		ArrayList<String> ids = new ArrayList<String>(lights.keySet());
		updateLights(new LightRequest(ids, state));
	}
}
