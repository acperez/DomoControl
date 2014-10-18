package es.acperez.domocontrol.systems.light;

import android.os.Bundle;
import es.acperez.domocontrol.systems.light.controller.LightManager;

public class LightData {

	public String mServer;
	public int mPort;
	public String mUsername;
	
	public LightData(Bundle settings) {
		mServer = settings.getString(LightManager.SERVER);
	    mUsername = settings.getString(LightManager.USERNAME);
	}
	
	public Bundle exportSettings() {
		Bundle settings = new Bundle();
		settings.putString(LightManager.SERVER, mServer);
		return settings;
	}
}