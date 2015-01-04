package es.acperez.domocontrol.systems.light.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.LightSystem;

public class LightServiceData extends ServiceData {
	private SharedPreferences mSettings;

	public String mServer;
	public String mUsername;
	
	public LightServiceData(Context context) {
		mSettings = context.getSharedPreferences(DomoSystem.LIGHT_SERVICE_SETTINGS_NAME, Context.MODE_PRIVATE);
	}
	
	public void importSettings() {
		mServer = mSettings.getString(LightSystem.SERVER, "");
	    mUsername = mSettings.getString(LightSystem.USERNAME, "");
	}
	
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putString(LightSystem.SERVER, mServer);
		editor.putString(LightSystem.USERNAME, mUsername);
		
		editor.commit();
	}
}