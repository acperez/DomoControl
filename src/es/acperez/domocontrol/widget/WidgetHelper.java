package es.acperez.domocontrol.widget;

import android.content.Context;
import android.os.Bundle;
import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.light.LightSystem;

public class WidgetHelper {
	
	public static void switchLight(final boolean state, Context context) {
		Bundle lightSettings = DomoControlApplication.restorePreferences(DomoSystem.LIGHT_SETTINGS_NAME);
		
		new LightSystem(context, lightSettings, new DomoSystemStatusListener() {
			@Override
			public void onSystemStatusChange(DomoSystem system, int status) {
				if (status == DomoSystem.STATUS_ONLINE)
					((LightSystem)system).widgetSwitch(state);
			}
		}, true);
	}
}
