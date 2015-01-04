package es.acperez.domocontrol;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import es.acperez.domocontrol.mainList.ContentFragment;
import es.acperez.domocontrol.mainList.SystemListFragment;
import es.acperez.domocontrol.mainList.SystemListFragment.OnItemSelectedListener;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.DomoSystem.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.power.PowerSystem;
import es.acperez.domocontrol.systems.wemo.WemoSystem;

public class DomoControlActivity extends Activity implements OnItemSelectedListener, DomoSystemStatusListener {
	public static final String SYSTEM_SELECTION = "selected_system";
	private static ArrayList<DomoSystem> domoSystems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domo_control);
		
		initSystems();
	}
	
	private void initSystems() {
		domoSystems = new ArrayList<DomoSystem>();
		
		domoSystems.add(new PowerSystem(this, this));
		domoSystems.add(new LightSystem(this, this));
		domoSystems.add(new WemoSystem(this, this));
	}

	@Override
	public void onItemSelected(int position) {
		ContentFragment frag = (ContentFragment) getFragmentManager().findFragmentById(R.id.controlFragment);
		frag.updateControlPanel(position);
	}
	
	public static Fragment getSystemFragment(int position) {
		if (position < 0 || position > domoSystems.size())
		return null;
	
		return domoSystems.get(position).getFragment();
	}

	public static String[] getSystemsName() {
		String[] names = new String[domoSystems.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = domoSystems.get(i).mName;
		}
		
		return names;
	}

	public static int getSystemStatus(int type) {
		return domoSystems.get(type).getStatus();
	}

	@Override
	protected void onStop() {
		((LightSystem) domoSystems.get(DomoSystem.TYPE_LIGHT)).disconnect();
		super.onStop();
	}

	@Override
	public void onSystemStatusChange(DomoSystem system, int status) {
		// Update status in list item
        SystemListFragment fragment = (SystemListFragment)getFragmentManager().findFragmentById(R.id.system_list_fragment);
        fragment.updateStatus(system.mType, status);
	}
}