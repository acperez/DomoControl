package es.acperez.domocontrol.services.wemo;

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
import es.acperez.domocontrol.services.wemo.ssdp.SSDPFinder;
import es.acperez.domocontrol.services.wemo.ssdp.SSDPFinder.SSDPListener;

public class WemoService extends DomoService implements SSDPListener {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private SSDPFinder mDeviceFinder;
	private Device mDevice;
	private WemoServiceData mData;
	private ConnectionManager mConnectionManager = null;
	
	@Override
	public void onCreate() {
		super.onCreate();

		mData = new WemoServiceData(this);
		mData.importSettings();
		
		mConnectionManager = ConnectionManager.getInstance();
		
        mDeviceFinder = new SSDPFinder(this);
        mDevice = mData.mDevice;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean action = intent.getBooleanExtra(DomoAlarm.ALARM_ACTION, false);
		doSetStatus(null, 0, action);
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	protected void doConnect(Handler handler) {
		if (mDevice != null) {
			mConnectionManager.push(new WemoManagerTask(handler, DomoService.SERVICE_CONNECTION, mDevice));
			return;
		}

		mDeviceFinder.start();
	}
	
	@Override
	protected void doGetSwitches(Handler handler) {
		mConnectionManager.push(new WemoManagerTask(handler, DomoService.SERVICE_GET_SWITCHES, mDevice));
	}

	@Override
	protected ArrayList<DomoSwitch> doGetSwitchesResponse(ServiceMessage message) {
		boolean status = (Boolean)message.data;
		ArrayList<DomoSwitch> switches = new ArrayList<DomoSwitch>();
		switches.add(new DomoSwitch(0, status, "asd", mServiceId));
		return switches;
	}
	
	@Override
	protected void doGetStatus(Handler handler, int plugId) {
		mConnectionManager.push(new WemoManagerTask(handler, DomoService.SERVICE_GET_STATUS, mDevice));
	}

	@Override
	protected void doSetStatus(Handler handler, int plugId, boolean status) {
		mConnectionManager.push(new WemoManagerTask(handler, DomoService.SERVICE_GET_STATUS, mDevice, status));
	}
	
	@Override
	protected boolean doStatusResponse(ServiceMessage message) {
		return (Boolean) message.data;
	}

	@Override
	public void onDeviceFound(Device device) {
		mData.setDevice(device);
		mData.exportSettings();
		mDevice = device;
		
		Message message = Message.obtain(mRemoteHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_NONE, 0, true));
		mRemoteHandler.sendMessage(message);
	}

	@Override
	public void onFindTimeout() {
		Message message = Message.obtain(mRemoteHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_FIND, 0, false));
		mRemoteHandler.sendMessage(message);
	}

	@Override
	public Bundle getSettings() {
		return mData.getSettings();
	}

	@Override
	public void saveSettings(Bundle settings) {
		mDevice = null;
	}

	@Override
	public void onDisconnect() {
	}

	@Override
	protected void doEditSwitchName(Handler handler, int id, String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doEditSwitchNameResponse(ServiceMessage message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getServiceType() {
		return DomoService.SERVICE_TYPE_WEMO;
	}
}