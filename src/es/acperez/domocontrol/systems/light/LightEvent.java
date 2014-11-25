package es.acperez.domocontrol.systems.light;

import android.content.Intent;
import es.acperez.domocontrol.systems.base.DomoEvent;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class LightEvent extends DomoEvent {    
	public LightEvent(long id, String timeEvent, boolean action) {
		super(DomoSystem.TYPE_LIGHT, id, timeEvent, action);
	}

	@Override
	protected void fillAlarmExtraData(Intent intent) {}
}