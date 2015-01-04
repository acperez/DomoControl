package es.acperez.domocontrol.systems.power;

import android.content.Intent;
import es.acperez.domocontrol.systems.base.DomoEvent;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;

public class PowerEvent extends DomoEvent {
	
	public int mSocket;

	public PowerEvent(long id, String timeEvent, boolean action, int socket) {
		super(DomoSystem.TYPE_POWER, id, timeEvent, action);
		mSocket = socket;
	}

	@Override
	protected void fillAlarmExtraData(Intent intent) {
		intent.putExtra(PowerAlarm.ALARM_SOCKET, mSocket);
	}
}
