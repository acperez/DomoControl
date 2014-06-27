package es.acperez.domocontrol;

import java.util.Map;

import es.acperez.domocontrol.systems.DomoSystems;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class DomoControlApplication extends Application {
	
    public static final String SYSTEM_SELECTION = "selected_system";
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
    
	public static void savePreferences(Context context, Bundle settings, String prefName) {
		SharedPreferences sharedPref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		
		for (String key : settings.keySet()) {
			editor.putString(key, settings.getString(key));
		}

		editor.commit();
	}
	
	public static Bundle restorePreferences(Context context, String prefName) {
		Bundle settings = new Bundle();
		SharedPreferences sharedPref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		Map<String, ?> preferences = sharedPref.getAll();
		for (String key : preferences.keySet()) {
			settings.putString(key, (String)preferences.get(key));
		}
		
		return settings;
	}
	
	// Systems related methods
	public static void addSystemListener(DomoSystemStatusListener listener) {
		mSystemsData.addSystemListener(listener);
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
	
	public static String byteArrayToHexString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return buffer.toString();
	}
	
	public static byte[] hexStringToByteArray(String string) {
		byte[] bytes = new byte[string.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			int charPos = i * 2;
			bytes[i] = Integer.decode("0x" + string.substring(charPos, charPos + 2)).byteValue();
		}
		return bytes;
	}
}
