package es.acperez.domocontrol.widget;

import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.systems.light.service.LightService;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;

public class WidgetHelper {
	
	public static void switchLight(final boolean state, Context context) {
		
		Intent intent = new Intent(context, LightService.class);
		intent.putExtra(PowerAlarm.ALARM_ACTION, state);
        context.startService(intent);
	}
}