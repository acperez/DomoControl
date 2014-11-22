package es.acperez.domocontrol.systems.power.controller;

import es.acperez.domocontrol.systems.power.PowerEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PowerAlarm extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		int socket = intent.getIntExtra(PowerEvent.ALARM_SOCKET, 0);
		boolean action = intent.getBooleanExtra(PowerEvent.ALARM_ACTION, false);
		
		System.out.println("ALARM!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println("SOCKET: " + socket);
		System.out.println("ACTION: " + action);
	}

}
