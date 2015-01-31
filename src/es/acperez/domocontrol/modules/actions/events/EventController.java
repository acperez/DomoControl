package es.acperez.domocontrol.modules.actions.events;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.actions.DomoActionController;
import es.acperez.domocontrol.modules.monitors.DomoMonitorFragment;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.DomoService.DomoServiceBinder;
import es.acperez.domocontrol.services.DomoService.ServiceListener;
import es.acperez.domocontrol.services.gembird.PowerService;
import es.acperez.domocontrol.services.philips.hue.LightService;

public class EventController extends DomoActionController implements ServiceListener {
	final public static String ALARMS = "event.alarms";
	
	private EventData mData;
	private ArrayList<DomoService> mServices;
	private ArrayList<DomoSwitch> mSwitches;
	private DomoMonitorFragment mFragment;
	private EventManager mEventManager;
	
	public EventController(Context context, int id) {
		super(id, "Events");
		
		mData = new EventData(context);
		mData.importSettings();
		
		mSwitches = new ArrayList<DomoSwitch>();
		
		mEventManager = new EventManager(context);
		if (mData.mAlarmsEnabled)
			mEventManager.setAlarmsStatus(true);
		
		mServices = new ArrayList<DomoService>();
        launchService(context, PowerService.class);
        launchService(context, LightService.class);
	}
	
	// ServiceListener methods
	
	@Override
	public void onGetSwitches(int serviceId, ArrayList<DomoSwitch> switches) {
		mSwitches.addAll(switches);
		if (mFragment != null)
			mFragment.updateContent();
	}
	
	@Override
	public void onRequestError(int serviceId, int result) {
		System.out.println("Service error");
		Iterator<DomoSwitch> iterator = mSwitches.iterator();
		while (iterator.hasNext()) {
			DomoSwitch item = iterator.next();
			if (item.serviceId == serviceId) {
				iterator.remove();
			}
		}
		if (mFragment != null)
			mFragment.updateContent();
	}
	
	@Override
	public void onServiceStarted(int serviceId) {}
	
	@Override
	public void onSwitchAction(int serviceId, int id, boolean status) {}
	
	// Internal methods
	
	protected ArrayList<DomoEvent> getEvents() {
		return mEventManager.getAllEvents();
	}
	
	public int addEvent(DomoEvent event) {
		return mEventManager.addEvent(event);
	}
	
	public void deleteEvent(DomoEvent event) {
		mEventManager.deleteEvent(event);
	}
	
	public void enableAlarms(boolean action) {
		mData.mAlarmsEnabled = action;
		mData.exportSettings();
		mEventManager.setAlarmsStatus(action);
	}
	
	private boolean launchService(Context context, Class<?> serviceClass) {
		Intent intent = new Intent(context, serviceClass);
        return context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DomoServiceBinder binder = (DomoServiceBinder) service;
            DomoService serv = binder.getService();
            serv.addListener(EventController.this);
        	mServices.add(serv);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

	@Override
	public Fragment createFragment() {
		return new EventFragment(this);
	}

	public ArrayList<DomoSwitch> getSwitches() {
		return mSwitches;
	}

	@Override
	public void onStop() {}

	public boolean isAlarmEnabled() {
		return mData.mAlarmsEnabled;
	}

	@Override
	public void onSwitchNameEdited(int serviceId, int id, String name) {
		// TODO Auto-generated method stub
		
	}
}