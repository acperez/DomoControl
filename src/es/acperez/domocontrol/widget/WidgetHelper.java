package es.acperez.domocontrol.widget;

import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.modules.actions.events.DomoAlarm;
import es.acperez.domocontrol.services.philips.hue.LightService;

public class WidgetHelper {
	
	public static void switchLight(final boolean state, Context context) {
		
		System.out.println("WIDGET - Send action");
		Intent intent = new Intent(context, LightService.class);
		intent.putExtra(DomoAlarm.ALARM_ACTION, state);
        context.startService(intent);
        
//		Intent power = new Intent(context, WemoService.class);
//		power.putExtra(PowerAlarm.ALARM_ACTION, state);
//        context.startService(power);
	}
}