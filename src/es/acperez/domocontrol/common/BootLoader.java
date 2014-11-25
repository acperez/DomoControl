package es.acperez.domocontrol.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.systems.light.LightData;
import es.acperez.domocontrol.systems.light.controller.LightEventManager;
import es.acperez.domocontrol.systems.power.PowerData;
import es.acperez.domocontrol.systems.power.controller.PowerEventManager;

public class BootLoader extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			PowerData powerConf = new PowerData(context);
			powerConf.importSettings();
			new PowerEventManager(context, powerConf);
				
			LightData lightConf = new LightData(context);
			lightConf.importSettings();
			new LightEventManager(context, lightConf);
	    }
	}
}