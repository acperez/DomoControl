package es.acperez.domocontrol;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class DomoControlApplication extends Application {
	
    public static final String SYSTEM_SELECTION = "selectes_system";
	private final String prefName = "settings";
	static {
		System.loadLibrary("DomoControl");
	}
    
    public static native byte[] EncryptData(byte[] data);
    public static native byte[] DecryptData(byte[] data);
 
	public void Init() throws Exception {
		
	}
	
	public void savePreferences(String address, String password) {
		SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.pref_power_address), address);
		editor.putString(getString(R.string.pref_power_password), new String(EncryptData(password.getBytes())));
		editor.commit();
	}
}
