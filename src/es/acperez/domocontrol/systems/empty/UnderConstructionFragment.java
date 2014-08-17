package es.acperez.domocontrol.systems.empty;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UnderConstructionFragment extends SystemFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.under_construction_fragment, container, false);
    }

	@Override
	public void updateStatus() {
	}

	@Override
	public void setSystem(DomoSystem system) {
	}

	@Override
	public void updateContent(int what) {		
	}
}