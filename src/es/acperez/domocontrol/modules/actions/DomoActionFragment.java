package es.acperez.domocontrol.modules.actions;

import android.app.Fragment;

public abstract class DomoActionFragment extends Fragment {

	protected DomoActionController mController;
	
	abstract public void updateContent();

	protected DomoActionFragment(DomoActionController controller) {
		mController = controller;
	}
}
