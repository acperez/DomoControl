package es.acperez.domocontrol.modules.monitors;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.SparseArray;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.DomoService.DomoServiceBinder;
import es.acperez.domocontrol.services.DomoService.ServiceListener;

public abstract class DomoMonitorController extends DomoController implements ServiceListener {
	
	protected SparseArray<DomoService> mServices;
	private ArrayList<DomoSwitch> mSwitches;
	
	public DomoMonitorController(int id, String name) {
		super(id, name, DomoController.TYPE_MONITOR);
		
		mServices = new SparseArray<DomoService>();
		mSwitches = new ArrayList<DomoSwitch>();
	}
	
	protected void addService(Context context, Class<?> serviceClass) {
		launchService(context, serviceClass, mConnection);
	}
    
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            DomoServiceBinder binder = (DomoServiceBinder) service;
            DomoService serv = binder.getService();
            mServices.put(serv.getServiceType(), serv);
            serv.addListener(DomoMonitorController.this);
        	onServiceInitialized(serv);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    
    protected void onServiceInitialized(DomoService service) {}
    
    // ServiceListener methods
    
	@Override
	public final void onServiceStarted(int serviceId) {
		if (mFragment != null)
			((DomoMonitorFragment) mFragment).updateServiceStatus(true);
	}
	
	@Override
	public final void onGetSwitches(int serviceId, ArrayList<DomoSwitch> switches) {
		clearSwitchesByServiceId(serviceId);
		
		mSwitches.addAll(switches);
		
		if (mFragment != null)
			((DomoMonitorFragment) mFragment).updateContent();
	}
	
	@Override
	public final void onRequestError(int serviceId, int result) {
		System.out.println("Request Error on service " + serviceId + ": " + result);
		clearSwitchesByServiceId(serviceId);
		
		if (mFragment != null)
			((DomoMonitorFragment) mFragment).updateContent();
	}
	
	@Override
	public final void onSwitchAction(int serviceId, int id, boolean status) {
		if (mFragment == null)
			return;
		
		DomoSwitch domoSwitch = findSwitch(serviceId, id);
		domoSwitch.status = status;
		
		((DomoMonitorFragment) mFragment).updateSwitch(domoSwitch);
	}
	
	@Override
	public final void onSwitchNameEdited(int serviceId, int id, String name) {
		if (mFragment == null)
			return;
		
		DomoSwitch domoSwitch = findSwitch(serviceId, id);
		domoSwitch.name = name;
		
		((DomoMonitorFragment) mFragment).updateSwitch(domoSwitch);
	}

	private void clearSwitchesByServiceId(int serviceId) {
		Iterator<DomoSwitch> iterator = mSwitches.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().serviceId == serviceId)
				iterator.remove();
		}		
	}
	
	private DomoSwitch findSwitch(int serviceId, int switchId) {
		Iterator<DomoSwitch> iterator = mSwitches.iterator();
		while (iterator.hasNext()) {
			DomoSwitch domoSwitch = iterator.next();
			if (domoSwitch.id == switchId && domoSwitch.serviceId == serviceId) {
				return domoSwitch;
			}
		}
		
		return null;
	}
	
	public ArrayList<DomoSwitch> getSwitches() {
		return mSwitches;
	}
	
	public boolean isConnected(int serviceType) {
		return mServices.get(serviceType).isConnected();
	}
	
	public void setSwitchStatus(DomoSwitch domoSwitch, boolean status) {
		mServices.get(domoSwitch.serviceId).setStatus(domoSwitch.id, status);
	}
	
	public void editSwitchName(DomoSwitch domoSwitch, String name) {
		mServices.get(domoSwitch.serviceId).editSwitchName(domoSwitch.id, name);
	}
	
	@Override
	public final void onStop() {}
}
