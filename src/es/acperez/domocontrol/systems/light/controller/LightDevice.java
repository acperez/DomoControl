package es.acperez.domocontrol.systems.light.controller;

import java.util.List;

import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHScene;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.systems.base.DomoSystem;
import android.os.Bundle;

public class LightDevice {
	public static final int UPDATE_BRIDGE = 0;
	public static final int UPDATE_LIGHTS = 1;
	
	public String mServer;
	public String mUsername;
	
	private PHBridge mBridge;
//	public List<Light> lights;
	public List<PHGroup> groups;
	public List<PHScene> scenes;

	public LightDevice() {
//		lights = new ArrayList<Light>();
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

	public void setBridge(PHBridge bridge) {
		mBridge = bridge;
	}

	public PHLight getLight(int index) {
		return mBridge.getResourceCache().getAllLights().get(index);
	}

	public List<PHLight> getLights() {
		return mBridge.getResourceCache().getAllLights();
	}

//	public void setLights(List<PHLight> lights) {
//		this.lights.clear();
//		
//		for (PHLight light : lights) {
//			this.lights.add(new Light(light));
//		}
//	}

//	public void refreshLightsState() {
////		for (Light light : this.lights) {
////			light.refresh();
////		}
//		
//		this.lights.get(2).refresh();
//	}
}
