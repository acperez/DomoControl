package es.acperez.domocontrol;

import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

public class DomoControlApplication extends Application {
	
    public static final String SYSTEM_SELECTION = "selectes_system";
	private final String prefName = "settings";
	private static DomoSystems mSystemsData = null;
	
	static {
		System.loadLibrary("DomoControl");
	}
    
	public static native byte[] EncryptData(byte[] data);
    public static native byte[] DecryptData(byte[] data);
    
    @Override
	public void onCreate() {
		super.onCreate();
		try {
			mSystemsData = new DomoSystems(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	public void savePreferences(String address, String password) {
		SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(getString(R.string.pref_power_address), address);
		editor.putString(getString(R.string.pref_power_password), new String(EncryptData(password.getBytes())));
		editor.commit();
	}
	
	// Systems related methods
	public static void addSystemListener(int systemType, DomoSystemStatusListener listener) {
		mSystemsData.addSystemListener(systemType, listener);
	}
	
	public static void sendSystemRequest(int systemType, int request) {
		mSystemsData.sendRequest(systemType, request);
	}
	
	public static Fragment getSystemFragment(int position) {
		return mSystemsData.getFragment(position);
	}
	
	public static String[] getSystemsName() {
		return mSystemsData.getSystemsName();
	}
	
	public static int getSystemStatus(int type) {
		return mSystemsData.getStatus(type);
	}
}
