package es.acperez.domocontrol.services.gembird;

import java.util.ArrayList;

import org.json.hue.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.ServiceData;

public class PowerServiceData extends ServiceData {
		
	final public static String SERVER = "power.host";
	final public static String SERVER_PORT = "power.port";
	final public static String PASSWORD = "power.password";
	final public static String NAMES = "power.names";
	final public static String SOCKET_DEFAULT_NAME = "Socket";
	
	private SharedPreferences mSettings;
	public String mServer;
	public int mPort;
	public String mPassword;
	public ArrayList<String> mNames;
	
	public PowerServiceData(Context context) {
		mSettings = context.getSharedPreferences(DomoController.POWER_SERVICE_SETTINGS_NAME, Context.MODE_PRIVATE);
	}
	
	public void importSettings() {
		mServer = mSettings.getString(SERVER, "");
		mPort = mSettings.getInt(SERVER_PORT, 5000);
		
		String names = mSettings.getString(NAMES, null);
		if (names != null) {
			mNames = new ArrayList<String>();
			JSONArray json = new JSONArray(names);
			
		    for (int i = 0; i < json.length(); i++)
		         mNames.add(json.getString(i));
		}
		
		mPassword = mSettings.getString(PASSWORD, null);
		if (mPassword != null) {
			byte[] bytes = DomoControlApplication.hexStringToByteArray(mPassword);
			mPassword = new String(DomoControlApplication.DecryptData(bytes));
			mPassword = "1";
		} else
			mPassword = "";
	}
	
	public void exportSettings() {
		byte[] bytes = DomoControlApplication.EncryptData(mPassword.getBytes());
		String encPassword = DomoControlApplication.byteArrayToHexString(bytes);
		
		Editor editor = mSettings.edit();
		editor.putString(SERVER, mServer);
		editor.putInt(SERVER_PORT, mPort);
		editor.putString(PASSWORD, encPassword);
		
		if (mNames != null) {
			JSONArray json = new JSONArray(mNames);
			editor.putString(NAMES, json.toString());
		}
		
		editor.commit();
	}
	
	public Bundle getSettings() {
		Bundle data = new Bundle();
		data.putString(SERVER, mServer);
		data.putInt(SERVER_PORT, mPort);
		data.putString(PASSWORD, "1");
		
		return data;
	}

	public void setSettings(Bundle settings) {
		mServer = settings.getString(SERVER, mServer);
		mPort = settings.getInt(SERVER_PORT, mPort);
		mPassword = settings.getString(PASSWORD, "1");
		
		exportSettings();
	}
	
	public ArrayList<String> getNames(int length) {
		if (mNames == null) {
			mNames = new ArrayList<String>();
			for (int i = 0; i < length; i++) {
				mNames.add(SOCKET_DEFAULT_NAME + " " + i);
				exportSettings();
			}
		}
		
		return mNames;
	}
}
