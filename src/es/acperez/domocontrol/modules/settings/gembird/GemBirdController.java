package es.acperez.domocontrol.modules.settings.gembird;

import android.app.Fragment;
import android.content.Context;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.gembird.PowerService;

public class GemBirdController extends DomoSettingsController {
	
	public GemBirdController(Context context, int id, DomoSystemStatusListener listener) {
		super(context, id, context.getResources().getString(R.string.settings_gembird_name),
				PowerService.class, DomoService.SERVICE_TYPE_GEMBIRD, listener);
	}
	
	@Override
	public Fragment createFragment() {
		return new GemBirdFragment(this);
	}
}