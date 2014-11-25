package es.acperez.domocontrol.systems.power.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.service.ServiceData;
import es.acperez.domocontrol.systems.power.PowerSystem;

public class PowerServiceData extends ServiceData {
	private SharedPreferences mSettings;
	public String mServer;
	public int mPort;
	public String mPassword;
	
	public PowerServiceData(Context context) {
		mSettings = context.getSharedPreferences(DomoSystem.POWER_SERVICE_SETTINGS_NAME, Context.MODE_PRIVATE);
	}
	
	public void importSettings() {
		mServer = mSettings.getString(PowerSystem.SERVER, "");
		mPort = mSettings.getInt(PowerSystem.SERVER_PORT, 5000);		
		mPassword = mSettings.getString(PowerSystem.PASSWORD, null);
		if (mPassword != null) {
			byte[] bytes = DomoControlApplication.hexStringToByteArray(mPassword);
			mPassword = new String(DomoControlApplication.DecryptData(bytes));
		} else {
			mPassword = "";
		}
	}
	
	public void exportSettings() {
		byte[] bytes = DomoControlApplication.EncryptData(mPassword.getBytes());
		String encPassword = DomoControlApplication.byteArrayToHexString(bytes);
		
		Editor editor = mSettings.edit();
		editor.putString(PowerSystem.SERVER, mServer);
		editor.putInt(PowerSystem.SERVER_PORT, mPort);
		editor.putString(PowerSystem.PASSWORD, encPassword);
		
		editor.commit();
	}
}
