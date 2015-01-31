package es.acperez.domocontrol.modules.monitors.light;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;

import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.database.LightDbHelper;
import es.acperez.domocontrol.modules.monitors.DomoMonitorController;
import es.acperez.domocontrol.modules.monitors.DomoMonitorFragment;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.philips.hue.DomoLight;
import es.acperez.domocontrol.services.philips.hue.LightService;
import es.acperez.domocontrol.services.philips.hue.LightService.ColorChangedListener;

public class LightController extends DomoMonitorController implements ColorChangedListener {
	public static final int UPDATE_BRIDGE = 0;
	public static final int UPDATE_LIGHTS = 1;
	public static final int REMOTE_UPDATE_LIGHTS = 2;
	public static final int UPDATE_SETTINGS = 3;
	
	final public static String ALARMS = "light.alarms";	
	public static final String SERVER = "light.host";
	public static final String USERNAME = "light.username";
	
	private LightDbHelper mDbHelper;
	private LightService mService;
	private DomoMonitorFragment mFragment;
	
	public LightController(Context context, int id) {
		super(id,  context.getResources().getString(R.string.monitors_lights_name));
		
		addService(context, LightService.class);
		
		mDbHelper = new LightDbHelper(context);
	}
	
	@Override
	public Fragment createFragment() {
		mFragment = new LightFragment(this);
		return mFragment;
	}
	
	@Override
	protected void onServiceInitialized(DomoService service) {
		if (service.getServiceType() != DomoService.SERVICE_TYPE_PHILIPS_HUE)
			return;
		
		mService = (LightService) service;
		mService.setColorListener(LightController.this);
    }
	
	public ArrayList<Scene> getScenes() {
		return mDbHelper.getAllScenes();
	}
	
	public void saveScene(Scene scene) {
		mDbHelper.insertScene(scene);
	}

	public void deleteScene(Scene scene) {
		mDbHelper.deleteScene(scene);
	}
	
	public List<PHLight> getSceneLights() {
		return mService.getAllLights();
	}
	
	public void setColor(int lightId, PHLightState state) {
		mService.setColor(lightId, state);
	}
	
	public void updateLights(ArrayList<DomoLight> lights, float[] color) {
		mService.setState(lights, color);
	}

	@Override
	public void onLightRequestDone(ArrayList<DomoLight> lights) {
		((LightFragment) mFragment).updateColors(lights);
	}
	
	public boolean isConnected() {
		return isConnected(DomoService.SERVICE_TYPE_PHILIPS_HUE);
	}
}
