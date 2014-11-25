package es.acperez.domocontrol.systems.base;

import es.acperez.domocontrol.systems.light.service.ServiceData;
import android.app.Fragment;

public abstract class SystemFragment extends Fragment {

	abstract public void updateStatus();
	
	abstract public void updateContent(int what, Object obj);

	abstract public void updateServiceSettings(ServiceData settings);
	
}
