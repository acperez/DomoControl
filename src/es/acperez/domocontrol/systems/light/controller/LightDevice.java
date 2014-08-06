package es.acperez.domocontrol.systems.light.controller;

import java.util.List;

import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHScene;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.systems.base.DomoSystem;
import android.os.Bundle;

public class LightDevice {
	public String mServer;
	public String mUsername;
	
	public List<PHLight> lights;
	public List<PHGroup> groups;
	public List<PHScene> scenes;

	public LightDevice() {
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
}
