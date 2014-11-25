package es.acperez.domocontrol.systems.light;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class LightData {
	private SharedPreferences mSettings;

	public boolean mAlarmsEnabled;
	
	public LightData(Context context) {
		mSettings = context.getSharedPreferences(DomoSystem.LIGHT_SETTINGS_NAME, Context.MODE_PRIVATE);
	}

	public void importSettings() {
		mAlarmsEnabled = mSettings.getBoolean(LightSystem.ALARMS, false);
	}
	
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putBoolean(LightSystem.ALARMS, mAlarmsEnabled);
		
		editor.commit();
	}
}