package es.acperez.domocontrol.modules.monitors.switches;

import android.app.Fragment;
import android.content.Context;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.monitors.DomoMonitorController;
import es.acperez.domocontrol.modules.monitors.DomoMonitorFragment;
import es.acperez.domocontrol.services.gembird.PowerService;
import es.acperez.domocontrol.services.philips.hue.LightService;
import es.acperez.domocontrol.services.wemo.WemoService;

public class SwitchesController extends DomoMonitorController {

	private DomoMonitorFragment mFragment;
	
	public SwitchesController(Context context, int id) {
		super(id, context.getResources().getString(R.string.monitors_switches_name));
		
        addService(context, PowerService.class);
        addService(context, LightService.class);
        addService(context, WemoService.class);
	}
	
	@Override
	public Fragment createFragment() {
		mFragment = new SwitchesFragment(this);
		return mFragment;
	}
}