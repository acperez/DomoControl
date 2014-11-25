package es.acperez.domocontrol.systems.power.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.service.LightService;
import es.acperez.domocontrol.systems.power.service.PowerService;

public class PowerAlarm extends BroadcastReceiver {
	public static final String ALARM_TYPE = "type";
	public static final String ALARM_SOCKET = "socket";
    public static final String ALARM_ACTION = "action";
    public static final String ALARM_LIGHT_ACTION = "action";

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(ALARM_TYPE, -1);
		
		switch(type) {
			case DomoSystem.TYPE_POWER:
				intent.setClass(context, PowerService.class);
		        context.startService(intent);
		        break;
			case DomoSystem.TYPE_LIGHT:
				intent.setClass(context, LightService.class);
		        context.startService(intent);
		        break;
		}
	}
}
