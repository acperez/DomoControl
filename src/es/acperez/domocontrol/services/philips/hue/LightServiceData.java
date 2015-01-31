package es.acperez.domocontrol.services.philips.hue;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.ServiceData;

public class LightServiceData extends ServiceData {
	final public static String SERVER = "light.host";
	final public static String USERNAME = "light.username";
	
	private SharedPreferences mSettings;

	public String mServer;
	public String mUsername;
	
	public LightServiceData(Context context) {
		mSettings = context.getSharedPreferences(DomoController.LIGHT_SERVICE_SETTINGS_NAME, Context.MODE_PRIVATE);
	}
	
	public void importSettings() {
		mServer = mSettings.getString(SERVER, "");
	    mUsername = mSettings.getString(USERNAME, "");
	}
	
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putString(SERVER, mServer);
		editor.putString(USERNAME, mUsername);
		
		editor.commit();
	}
	
	public Bundle getSettings() {
		Bundle data = new Bundle();
		data.putString(SERVER, mServer);
		
		return data;
	}

	public void setSettings(Bundle settings) {
		mServer = settings.getString(SERVER, null);
		
		exportSettings();
	}
}