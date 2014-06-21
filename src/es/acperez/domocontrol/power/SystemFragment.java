package es.acperez.domocontrol.power;

import android.app.Fragment;

public abstract class SystemFragment extends Fragment {

	abstract public void updateStatus(int status, int error);
	
}
