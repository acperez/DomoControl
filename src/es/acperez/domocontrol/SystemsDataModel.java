package es.acperez.domocontrol;

import android.app.Fragment;
import android.content.Context;

public class SystemsDataModel {
	private String[] systems;
	private String[] fragmentClasses;
	private Fragment[] fragments;
	private int items = 0;
	
	public SystemsDataModel(Context context) throws Exception {
		systems = context.getResources().getStringArray(R.array.systems_array);
		fragmentClasses = context.getResources().getStringArray(R.array.system_fragments_array);
		if (systems.length != fragmentClasses.length) {
			throw new Exception("Invalid datamodel length");
		}
		
		items = systems.length;
		fragments = new Fragment[items];
	}

	public String[] getSystemsName() {
		return systems;
	}
	
	public Fragment getFragment(int position) {
		if (position < 0 || position > items)
			return null;
		
		Fragment fragment = fragments[position];
		if (fragment == null) {
			try {
				Class<?> claz = ContentFragment.class.getClassLoader().loadClass(fragmentClasses[position]);
				fragment = (Fragment)claz.newInstance();
				fragments[position] = fragment;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return fragment;
	}
}