package es.acperez.domocontrol.modules.monitors;

import es.acperez.domocontrol.modules.DomoSwitch;
import android.app.Fragment;

public abstract class DomoMonitorFragment extends Fragment {

	protected DomoMonitorController mController;
	
	abstract public void updateServiceStatus(boolean status);
	abstract public void updateContent();
	abstract public void updateSwitch(DomoSwitch domoSwitch);
	
	protected DomoMonitorFragment(DomoMonitorController controller) {
		mController = controller;
	}
	
	@Override
	public final void onStop() {
		super.onStop();
	}
}
