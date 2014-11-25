package es.acperez.domocontrol.systems.power;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class PowerData {
	private SharedPreferences mSettings;
	private Context mContext;

	public String[] mSocketNanes;
	public boolean mSockets[];
	public boolean mAlarmsEnabled;
	
	public PowerData(Context context) {
		mSettings = context.getSharedPreferences(DomoSystem.POWER_SETTINGS_NAME, Context.MODE_PRIVATE);
		mContext = context;
		
		mSockets = new boolean[4];
		mSocketNanes = new String[4];
	}

	public void importSettings() {
		mAlarmsEnabled = mSettings.getBoolean(PowerSystem.ALARMS, false);		
		String name = mSettings.getString(PowerSystem.SOCKET_NAME_1, null);
		if (name != null && name.length() > 0)
			mSocketNanes[0] = name;
		else
			mSocketNanes[0] = mContext.getString(R.string.power_socket1_name_desc);
		
		name = mSettings.getString(PowerSystem.SOCKET_NAME_2, null);
		if (name != null && name.length() > 0)
			mSocketNanes[1] = name;
		else
			mSocketNanes[1] = mContext.getString(R.string.power_socket2_name_desc);
		
		name = mSettings.getString(PowerSystem.SOCKET_NAME_3, null);
		if (name != null && name.length() > 0)
			mSocketNanes[2] = name;
		else
			mSocketNanes[2] = mContext.getString(R.string.power_socket3_name_desc);
		
		name = mSettings.getString(PowerSystem.SOCKET_NAME_4, null);
		if (name != null && name.length() > 0)
			mSocketNanes[3] = name;
		else
			mSocketNanes[3] = mContext.getString(R.string.power_socket4_name_desc);
		
		mSockets[0] = mSettings.getBoolean(PowerSystem.SOCKET_STATUS_1, false);
		mSockets[1] = mSettings.getBoolean(PowerSystem.SOCKET_STATUS_2, false);
		mSockets[2] = mSettings.getBoolean(PowerSystem.SOCKET_STATUS_3, false);
		mSockets[3] = mSettings.getBoolean(PowerSystem.SOCKET_STATUS_4, false);
	}
	
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putBoolean(PowerSystem.ALARMS, mAlarmsEnabled);
		editor.putString(PowerSystem.SOCKET_NAME_1, mSocketNanes[0]);
		editor.putString(PowerSystem.SOCKET_NAME_2, mSocketNanes[1]);
		editor.putString(PowerSystem.SOCKET_NAME_3, mSocketNanes[2]);
		editor.putString(PowerSystem.SOCKET_NAME_4, mSocketNanes[3]);
		editor.putBoolean(PowerSystem.SOCKET_STATUS_1, mSockets[0]);
		editor.putBoolean(PowerSystem.SOCKET_STATUS_2, mSockets[1]);
		editor.putBoolean(PowerSystem.SOCKET_STATUS_3, mSockets[2]);
		editor.putBoolean(PowerSystem.SOCKET_STATUS_4, mSockets[3]);
		
		editor.commit();
	}
}