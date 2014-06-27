package es.acperez.domocontrol;

import android.app.Activity;
import android.os.Bundle;
import es.acperez.domocontrol.mainList.ContentFragment;
import es.acperez.domocontrol.mainList.SystemListFragment;
import es.acperez.domocontrol.mainList.SystemListFragment.OnItemSelectedListener;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;

public class DomoControlActivity extends Activity implements OnItemSelectedListener, DomoSystemStatusListener {
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domo_control);
	}

	@Override
	public void onStart() {
	  super.onStart();
      InitializeSystemsStatus();	  
	}

	@Override
	public void onItemSelected(int position) {
		ContentFragment frag = (ContentFragment) getFragmentManager().findFragmentById(R.id.controlFragment);
		frag.updateControlPanel(position);
	}

	private void InitializeSystemsStatus() {
      DomoControlApplication.addSystemListener(this);
	}

	@Override
	public void onSystemStatusChange(int systemType, int status) {
		// Update status in list item
        SystemListFragment fragment = (SystemListFragment)getFragmentManager().findFragmentById(R.id.system_list_fragment);
        fragment.updateStatus(systemType, status);
	}
}