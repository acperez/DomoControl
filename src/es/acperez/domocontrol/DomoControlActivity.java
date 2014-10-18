package es.acperez.domocontrol;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import es.acperez.domocontrol.mainList.ContentFragment;
import es.acperez.domocontrol.mainList.SystemListFragment;
import es.acperez.domocontrol.mainList.SystemListFragment.OnItemSelectedListener;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.light.controller.LightManager;
import es.acperez.domocontrol.systems.power.PowerSystem;

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
		
		Bundle powerSettings = DomoControlApplication.restorePreferences(DomoSystem.POWER_SETTINGS_NAME);
		domoSystems.add(new PowerSystem(this, powerSettings, this));

		Bundle lightSettings = DomoControlApplication.restorePreferences(DomoSystem.LIGHT_SETTINGS_NAME);
		domoSystems.add(new LightSystem(this, lightSettings, this, false));
	}

	@Override
	public void onItemSelected(int position) {
		ContentFragment frag = (ContentFragment) getFragmentManager().findFragmentById(R.id.controlFragment);
		frag.updateControlPanel(position);
	}

	@Override
	public void onSystemStatusChange(DomoSystem system, int status) {
		// Update status in list item
        SystemListFragment fragment = (SystemListFragment)getFragmentManager().findFragmentById(R.id.system_list_fragment);
        fragment.updateStatus(system.type, status);
        
        system.updateStatus();
	}
	
	public static Fragment getSystemFragment(int position) {
		if (position < 0 || position > domoSystems.size())
		return null;
	
		return domoSystems.get(position).getFragment();
	}

	public static String[] getSystemsName() {
		String[] names = new String[domoSystems.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = domoSystems.get(i).name;
		}
		
		return names;
	}

	public static int getSystemStatus(int type) {
		return domoSystems.get(type).getStatus();
	}

	@Override
	protected void onStop() {
		domoSystems.get(DomoSystem.TYPE_LIGHT).sendRequest(LightManager.DISCONNECT, null, false);
		super.onStop();
	}
}