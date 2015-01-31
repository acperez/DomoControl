package es.acperez.domocontrol.common.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.modules.actions.events.EventData;
import es.acperez.domocontrol.modules.actions.events.EventManager;

public class BootLoader extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			EventData data = new EventData(context);
			data.importSettings();
			
			if (!data.mAlarmsEnabled)
				return;
			
			EventManager manager = new EventManager(context);
			manager.setAlarmsStatus(true);
	    }
	}
}