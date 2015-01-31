package es.acperez.domocontrol.services.gembird;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.common.connection.ConnectionManager;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.actions.events.DomoAlarm;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.ServiceMessage;

public class PowerService extends DomoService {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private PowerServiceData mData;
	private ConnectionManager mConnectionManager = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		mData = new PowerServiceData(this);
		mData.importSettings();
		
		mConnectionManager = ConnectionManager.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int socket = intent.getIntExtra(DomoAlarm.ALARM_SWITCH_ID, 0);
		boolean action = intent.getBooleanExtra(DomoAlarm.ALARM_ACTION, false);
		doSetStatus(null, socket, action);
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public Bundle getSettings() {
		return mData.getSettings();
	}
	
	@Override
	public void saveSettings(Bundle settings) {
		mData.setSettings(settings);
	}
	
	@Override
	public void doEditSwitchName(Handler handler, int id, String name) {
		mData.mNames.set(id, name);
		mData.exportSettings();
		
		Message message = Message.obtain(handler, DomoService.SERVICE_SWITCH_EDIT_NAME, new ServiceMessage(DomoController.ERROR_NONE, id, name));
		handler.sendMessage(message);
	}
	
	@Override
	protected String doEditSwitchNameResponse(ServiceMessage message) {
		return (String) message.data;
	}
	
	@Override
	protected void doConnect(Handler handler) {
		mConnectionManager.push(new PowerManagerTask(handler, DomoService.SERVICE_CONNECTION, mData.mServer, mData.mPort, mData.mPassword));
	}
	
	@Override
	protected void doGetSwitches(Handler handler) {
		mConnectionManager.push(new PowerManagerTask(handler, DomoService.SERVICE_GET_SWITCHES, mData.mServer, mData.mPort, mData.mPassword));
	}

	@Override
	protected ArrayList<DomoSwitch> doGetSwitchesResponse(ServiceMessage message) {
		boolean[] plugs = (boolean[])message.data;
		ArrayList<String> names = mData.getNames(plugs.length);
		ArrayList<DomoSwitch> switches = new ArrayList<DomoSwitch>();
		for (int i = 0; i < plugs.length; i++) {
			switches.add(new DomoSwitch(i, plugs[i], names.get(i), mServiceId));
		}
		return switches;
	}
	
	@Override
	protected void doGetStatus(Handler handler, int plugId) {
		mConnectionManager.push(new PowerManagerTask(handler, DomoService.SERVICE_GET_STATUS, mData.mServer, mData.mPort, mData.mPassword));
	}

	@Override
	protected void doSetStatus(Handler handler, int plugId, boolean status) {
		mConnectionManager.push(new PowerManagerTask(handler, DomoService.SERVICE_SET_STATUS, mData.mServer, mData.mPort, mData.mPassword, plugId, status));		
	}

	@Override
	protected boolean doStatusResponse(ServiceMessage message) {
		boolean[] plugs = (boolean[])message.data;
		return plugs[message.id];
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getServiceType() {
		return DomoService.SERVICE_TYPE_GEMBIRD;
	}
}