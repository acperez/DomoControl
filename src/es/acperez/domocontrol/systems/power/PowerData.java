package es.acperez.domocontrol.systems.power;

import android.content.Context;
import android.os.Bundle;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.power.controller.PowerManager;

public class PowerData {

	public String mServer;
	public int mPort;
	public String mPassword;
	public String[] mSocketNanes;
	public boolean mSockets[];
	public boolean mAlarmsEnabled;
	
	public PowerData() {
		mSockets = new boolean[4];
	}

	public void importSettings(Bundle settings, Context context) {
		mAlarmsEnabled = Boolean.valueOf(settings.getString(PowerManager.ALARMS, "false"));
		mServer = settings.getString(PowerManager.SERVER);
		mPort = Integer.valueOf(settings.getString(PowerManager.SERVER_PORT, "5000"));
		
		mPassword = new String();
		String password = settings.getString(PowerManager.PASSWORD);
		if (password != null) {
			byte[] bytes = DomoControlApplication.hexStringToByteArray(password);
			mPassword = new String(DomoControlApplication.DecryptData(bytes));
		}
		
		mSocketNanes = new String[4];
		String name = settings.getString(PowerManager.SOCKET_NAME_1);
		if (name != null && name.length() > 0)
			mSocketNanes[0] = name;
		else
			mSocketNanes[0] = context.getString(R.string.power_socket1_name_desc);
		
		name = settings.getString(PowerManager.SOCKET_NAME_2);
		if (name != null && name.length() > 0)
			mSocketNanes[1] = name;
		else
			mSocketNanes[1] = context.getString(R.string.power_socket2_name_desc);
		
		name = settings.getString(PowerManager.SOCKET_NAME_3);
		if (name != null && name.length() > 0)
			mSocketNanes[2] = name;
		else
			mSocketNanes[2] = context.getString(R.string.power_socket3_name_desc);
		
		name = settings.getString(PowerManager.SOCKET_NAME_4);
		if (name != null && name.length() > 0)
			mSocketNanes[3] = name;
		else
			mSocketNanes[3] = context.getString(R.string.power_socket4_name_desc);
		
		mSockets[0] = Boolean.valueOf(settings.getString(PowerManager.SOCKET_STATUS_1, "false"));
		mSockets[1] = Boolean.valueOf(settings.getString(PowerManager.SOCKET_STATUS_2, "false"));
		mSockets[2] = Boolean.valueOf(settings.getString(PowerManager.SOCKET_STATUS_3, "false"));
		mSockets[3] = Boolean.valueOf(settings.getString(PowerManager.SOCKET_STATUS_4, "false"));
	}
	
	public Bundle exportSettings() {
		byte[] bytes = DomoControlApplication.EncryptData(mPassword.getBytes());
		String encPassword = DomoControlApplication.byteArrayToHexString(bytes);

		Bundle settings = new Bundle();
		settings.putString(PowerManager.ALARMS, String.valueOf(mAlarmsEnabled));
		settings.putString(PowerManager.SERVER, mServer);
		settings.putString(PowerManager.SERVER_PORT, String.valueOf(mPort));
		settings.putString(PowerManager.PASSWORD, encPassword);
		settings.putString(PowerManager.SOCKET_NAME_1, mSocketNanes[0]);
		settings.putString(PowerManager.SOCKET_NAME_2, mSocketNanes[1]);
		settings.putString(PowerManager.SOCKET_NAME_3, mSocketNanes[2]);
		settings.putString(PowerManager.SOCKET_NAME_4, mSocketNanes[3]);
		settings.putString(PowerManager.SOCKET_STATUS_1, String.valueOf(mSockets[0]));
		settings.putString(PowerManager.SOCKET_STATUS_2, String.valueOf(mSockets[1]));
		settings.putString(PowerManager.SOCKET_STATUS_3, String.valueOf(mSockets[2]));
		settings.putString(PowerManager.SOCKET_STATUS_4, String.valueOf(mSockets[3]));
		return settings;
	}
}