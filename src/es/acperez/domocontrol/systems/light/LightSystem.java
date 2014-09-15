package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.controller.LightManager;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerListener;
import es.acperez.domocontrol.systems.light.controller.LightManager.LightManagerRequest;
import es.acperez.domocontrol.systems.light.controller.Scene;
import es.acperez.domocontrol.systems.light.controller.ScenesSqlHelper;

public class LightSystem extends DomoSystem implements LightManagerListener{
	public static final int UPDATE_BRIDGE = 0;
	public static final int UPDATE_LIGHTS = 1;
	
	public String mServer;
	public String mUsername;
	
	ScenesSqlHelper mDbHelper;

	public LightSystem(String systemName, String fragmentClass, Bundle settings, Context context) {
		super(systemName, fragmentClass, DomoSystem.TYPE_POWER);

		importSettings(settings);
		
		this.mManager = new LightManager(this);
		this.mManager.addSystemListener(this);
		
		sendRequest(LightManager.CONNECT, null, true);
		
		mDbHelper = new ScenesSqlHelper(context);
	}
	
	@Override
	public void settingsUpdate() {
		sendRequest(LightManager.CONNECT, null, true);
	}

	@Override
	public Bundle getSettings() {
		return exportSettings();
	}

	@Override
	public void requestResponse(Message msg) {
		mFragment.updateContent(UPDATE_BRIDGE, null);
	}
	
	public void importSettings(Bundle settings) {
		mServer = settings.getString(LightManager.SERVER);
	    mUsername = settings.getString(LightManager.USERNAME);
	}

	public Bundle exportSettings() {
		Bundle settings = new Bundle();
		settings.putString(LightManager.SERVER, mServer);
		return settings;
	}
	
	public void connected(String server, String username) {
		if (server == mServer || username == mUsername)
			return;
		
		mServer = server;
		mUsername = username;
		
		Bundle settings = exportSettings();
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
	public void onLightRequestDone(ArrayList<String> lightIds) {
		mFragment.updateContent(UPDATE_LIGHTS, lightIds);
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
}
