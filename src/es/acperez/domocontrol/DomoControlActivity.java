package es.acperez.domocontrol;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import es.acperez.domocontrol.mainlist.ContentFragment;
import es.acperez.domocontrol.mainlist.MenuItem;
import es.acperez.domocontrol.mainlist.SystemListFragment;
import es.acperez.domocontrol.mainlist.SystemListFragment.OnItemSelectedListener;
import es.acperez.domocontrol.modules.actions.events.EventController;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.modules.base.DomoController.DomoSystemStatusListener;
import es.acperez.domocontrol.modules.monitors.light.LightController;
import es.acperez.domocontrol.modules.monitors.switches.SwitchesController;
import es.acperez.domocontrol.modules.settings.gembird.GemBirdController;
import es.acperez.domocontrol.modules.settings.philips.hue.PhilipsHueController;
import es.acperez.domocontrol.modules.settings.wemo.WemoController;

public class DomoControlActivity extends Activity implements OnItemSelectedListener, DomoSystemStatusListener {
	private static ArrayList<DomoController> domoSystems;
	private SystemListFragment mFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSystems();
		setContentView(R.layout.domo_control);
		
		mFragment = (SystemListFragment)getFragmentManager().findFragmentById(R.id.system_list_fragment);
	}
	
	private void initSystems() {
		domoSystems = new ArrayList<DomoController>();
		
		domoSystems.add(new SwitchesController(this, 0));
		domoSystems.add(new LightController(this, 1));
		domoSystems.add(new EventController(this, 2));
		domoSystems.add(new GemBirdController(this, 3, this));
		domoSystems.add(new PhilipsHueController(this, 4, this));
		domoSystems.add(new WemoController(this, 5, this));
	}

	@Override
	public void onItemSelected(int id) {
		ContentFragment frag = (ContentFragment) getFragmentManager().findFragmentById(R.id.controlFragment);
		frag.updateControlPanel(domoSystems.get(id).getFragment());
	}

	public static ArrayList<MenuItem> getSystemsName(int systemType) {
		ArrayList<MenuItem> names = new ArrayList<MenuItem>();
		for (int i = 0; i < domoSystems.size(); i++) {
			DomoController system = domoSystems.get(i);

			if (system.mType == systemType)
				names.add(new MenuItem(system.mId, system.mName, system.getStatus()));
		}
		
		return names;
	}

	public static int getSystemStatus(int id) {
		return domoSystems.get(id).getStatus();
	}

	@Override
	protected void onStop() {
		for (int i = 0; i < domoSystems.size(); i++) {
			domoSystems.get(i).onStop();
		}
		
		super.onStop();
	}

	@Override
	public void onSystemStatusChange(DomoController system, int status) {
		// Update status in list item
        mFragment.updateStatus(system.mId, status);
	}
}