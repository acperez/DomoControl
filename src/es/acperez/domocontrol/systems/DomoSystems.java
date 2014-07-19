package es.acperez.domocontrol.systems;

import es.acperez.domocontrol.DomoControlApplication;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemManager.DomoSystemStatusListener;
import es.acperez.domocontrol.systems.empty.EmptySystem;
import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.power.PowerSystem;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

public class DomoSystems {
	private int items = 0;
	private DomoSystem[] domoSystems;
	
	public DomoSystems(Context context) throws Exception {
		String[] names = context.getResources().getStringArray(R.array.systems_array);
		String[] fragmentClasses = context.getResources().getStringArray(R.array.system_fragments_array);
		int[] systemTypes = context.getResources().getIntArray(R.array.system_types_array);
		if (names.length != fragmentClasses.length && names.length != systemTypes.length) {
			throw new Exception("Invalid datamodel length");
		}
		
		items = names.length;
		domoSystems = new DomoSystem[items];
		for (int i = 0; i < items; i++) {
			switch (systemTypes[i]) {
				case DomoSystem.TYPE_POWER:
					Bundle powerSettings = DomoControlApplication.restorePreferences(DomoSystem.POWER_SETTINGS_NAME);
					domoSystems[systemTypes[i]] = new PowerSystem(names[i], fragmentClasses[i], powerSettings);
					break;
				case DomoSystem.TYPE_LIGHT:
					Bundle lightSettings = DomoControlApplication.restorePreferences(DomoSystem.LIGHT_SETTINGS_NAME);
					domoSystems[systemTypes[i]] = new LightSystem(names[i], fragmentClasses[i], lightSettings);
					break;
				case DomoSystem.TYPE_EMPTY:
					domoSystems[systemTypes[i]] = new EmptySystem(names[i], fragmentClasses[i]);
					break;
				default:
					throw new Exception("Invalid system type");
			}
		}
	}

	public String[] getSystemsName() {
		String[] names = new String[items];
		for (int i = 0; i < items; i++) {
			names[i] = domoSystems[i].name;
		}
		
		return names;
	}
	
	public Fragment getFragment(int position) {
		if (position < 0 || position > items)
			return null;
		
		return domoSystems[position].getFragment();
	}
	
	public int getStatus(int type) {
		return domoSystems[type].getStatus();
	}

	public void sendRequest(int type, int request, Bundle params, boolean showLoading) {
		domoSystems[type].sendRequest(request, params, showLoading);
	}

	public void addSystemListener(DomoSystemStatusListener listener) {
		for (int i = 0; i < items; i++) {
			domoSystems[i].addSystemListener(listener);
		}
	}
}