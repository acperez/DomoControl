package es.acperez.domocontrol.power;

import es.acperez.domocontrol.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PowerFragment extends Fragment {

	public PowerFragment() {
		System.out.println("PowerFragment constructor");
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.power_monitor, container, false);
        return view;
    }
}