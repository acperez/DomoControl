package es.acperez.domocontrol.systems.base;

import android.app.Fragment;

public abstract class SystemFragment extends Fragment {

	abstract public void updateStatus();
	
	abstract public void updateContent(int what, Object obj);
	
}
