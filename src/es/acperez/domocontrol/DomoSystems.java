package es.acperez.domocontrol;

import es.acperez.domocontrol.common.DomoSystem;
import es.acperez.domocontrol.common.DomoSystem.DomoSystemStatusListener;
import android.app.Fragment;
import android.content.Context;

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
			domoSystems[systemTypes[i]] = new DomoSystem(names[i], fragmentClasses[i], systemTypes[i]);
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

	public void addSystemListener(int type, DomoSystemStatusListener listener) {
		domoSystems[type].addSystemListener(listener);
	}

	public void sendRequest(int type, int request) {
		domoSystems[type].sendRequest(request);
	}
}