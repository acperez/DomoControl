package es.acperez.domocontrol.modules.settings.wemo;

import android.app.Fragment;
import android.content.Context;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.modules.settings.DomoSettingsController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.wemo.WemoService;

public class WemoController extends DomoSettingsController {
	
    public WemoController(Context context, int id, DomoSystemStatusListener listener) {
		super(context, id, context.getResources().getString(R.string.settings_wemo_name),
				WemoService.class, DomoService.SERVICE_TYPE_WEMO, listener);        
    }

	@Override
	public Fragment createFragment() {
		return new WemoFragment(this);
	}
}
