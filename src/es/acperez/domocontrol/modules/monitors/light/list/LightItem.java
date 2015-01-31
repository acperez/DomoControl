package es.acperez.domocontrol.modules.monitors.light.list;

import es.acperez.domocontrol.modules.monitors.light.list.LightList.LightView;
import es.acperez.domocontrol.services.philips.hue.DomoLight;

public class LightItem {
	DomoLight light;
	LightView view;
	
	public LightItem(DomoLight light, LightView view) {
		this.light = light;
		this.view = view;
	}
}
