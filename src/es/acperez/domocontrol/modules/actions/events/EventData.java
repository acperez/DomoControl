package es.acperez.domocontrol.modules.actions.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import es.acperez.domocontrol.modules.base.DomoController;

public class EventData {
	private SharedPreferences mSettings;

	public boolean mAlarmsEnabled;
	
	public EventData(Context context) {
		mSettings = context.getSharedPreferences(DomoController.EVENT_SETTINGS_NAME, Context.MODE_PRIVATE);
	}

	public void importSettings() {
		mAlarmsEnabled = mSettings.getBoolean(EventController.ALARMS, false);
	}
	
	public void exportSettings() {
		Editor editor = mSettings.edit();
		
		editor.putBoolean(EventController.ALARMS, mAlarmsEnabled);
		
		editor.commit();
	}
}