package es.acperez.domocontrol.modules.actions.events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.gembird.PowerService;
import es.acperez.domocontrol.services.philips.hue.LightService;

public class DomoAlarm extends BroadcastReceiver {
	public static final String ALARM_SERVICE_ID = "serviceId";
	public static final String ALARM_SWITCH_ID = "switchId";
    public static final String ALARM_ACTION = "action";

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(ALARM_SERVICE_ID, -1);
		
		switch(type) {
			case DomoService.SERVICE_TYPE_GEMBIRD:
				intent.setClass(context, PowerService.class);
		        context.startService(intent);
		        break;
			case DomoService.SERVICE_TYPE_PHILIPS_HUE:
				intent.setClass(context, LightService.class);
		        context.startService(intent);
		        break;
		}
	}
}
