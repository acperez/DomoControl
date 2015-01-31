package es.acperez.domocontrol.modules.settings.philips.hue;

import android.app.Fragment;
import android.content.Context;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.philips.hue.LightService;

public class PhilipsHueController extends DomoSettingsController {
	
	public PhilipsHueController(Context context, int id, DomoSystemStatusListener listener) {
		super(context, id, context.getResources().getString(R.string.settings_philips_hue_name),
				LightService.class, DomoService.SERVICE_TYPE_PHILIPS_HUE, listener);
	}
	
	@Override
	public Fragment createFragment() {
		return new PhilipsHueFragment(this);
	}
}
