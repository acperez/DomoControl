package es.acperez.domocontrol.systems.light;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;

import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.R;
import es.acperez.domocontrol.database.LightDbHelper;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.light.controller.LightEventManager;
import es.acperez.domocontrol.systems.light.controller.Scene;
import es.acperez.domocontrol.systems.light.service.LightService;
import es.acperez.domocontrol.systems.light.service.LightService.LightBinder;
import es.acperez.domocontrol.systems.light.service.LightService.LightSystemRequest;
import es.acperez.domocontrol.systems.light.service.LightService.LightUpdateListener;

public class LightSystem extends DomoSystem implements LightUpdateListener {
	public static final int UPDATE_BRIDGE = 0;
	public static final int UPDATE_LIGHTS = 1;
	public static final int REMOTE_UPDATE_LIGHTS = 2;
	public static final int UPDATE_SETTINGS = 3;
	
	final public static String ALARMS = "light.alarms";	
	public static final String SERVER = "light.host";
	public static final String USERNAME = "light.username";
	
	public LightData mData;
	LightDbHelper mDbHelper;
	private LightEventManager mEventMAnager;
	private LightService mService;
	
	public LightSystem(Context context, DomoSystemStatusListener listener) {
		super(context.getResources().getString(R.string.system_name_light), DomoSystem.TYPE_LIGHT, listener);
		
		mData = new LightData(context);
		mData.importSettings();
		
		Intent intent = new Intent(context, LightService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
		mDbHelper = new LightDbHelper(context);
		
		mEventMAnager = new LightEventManager(context, mData);
	}

	@Override
	protected SystemFragment createFragment() {
		return new LightFragment(this);
	}

	@Override
	public void requestResponse(Message msg) {
		if (msg.arg1 == LightSystem.UPDATE_SETTINGS) {
			updateSettings();
			msg.arg1 = LightSystem.UPDATE_BRIDGE;
		}
		
		mFragment.updateContent(msg.arg1, null);
	}
	
	public void connect(String server) {
		mService.connect(mRequestListener);
	}
	
	public void disconnect() {
		mService.disconnect();
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
	
	public List<PHLight> getAllLights() {
		return mService.getAllLights();
	}
	
	public Map<String, PHLight> getLights() {
		return mService.getLights();
	}
	
	public void updateLights(LightSystemRequest request) {
		mService.updateLights(request);
	}
	
    public void updateSettings() {
    	mFragment.updateServiceSettings(mService.getSettings());
    }
    
	protected ArrayList<LightEvent> getEvents() {
		return mEventMAnager.getAllEvents();
	}

	public void deleteEvent(LightEvent event) {
		mEventMAnager.deleteEvent(event);
	}

	public int addEvent(LightEvent event) {
		return mEventMAnager.addEvent(event);
	}

	public void enableAlarms(boolean action) {
		mEventMAnager.enableAlarms(action);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {

            LightBinder binder = (LightBinder) service;
            mService = binder.getService();
       
            mService.setUpdateListener(LightSystem.this);
            mService.connect(mRequestListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

	@Override
	public void onLightRequestDone(int type, ArrayList<String> lightIds) {
		mFragment.updateContent(type, lightIds);
	}
}
