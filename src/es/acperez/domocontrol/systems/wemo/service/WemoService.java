package es.acperez.domocontrol.systems.wemo.service;

import java.lang.ref.WeakReference;

import es.acperez.domocontrol.common.connectionManager.ConnectionManager;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;
import es.acperez.domocontrol.systems.wemo.WemoData;
import es.acperez.domocontrol.systems.wemo.controller.Device;
import es.acperez.domocontrol.systems.wemo.controller.WemoManagerTask;
import es.acperez.domocontrol.systems.wemo.controller.SSDP.SSDPFinder;
import es.acperez.domocontrol.systems.wemo.controller.SSDP.SSDPFinder.SSDPListener;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class WemoService extends Service implements SSDPListener {
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private final IBinder mBinder = new WemoBinder();
	
	private WemoData mData;
	private SSDPFinder mDeviceFinder;
	private ConnectionManager mConnectionManager = null;
	private Device mDevice;
	private Handler mHandler;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class WemoBinder extends Binder {
		public WemoService getService() {
            return WemoService.this;
        }
    }
	
	@Override
	public void onCreate() {
		super.onCreate();

		mData = new WemoData(this);
		mData.importSettings();
		
		mConnectionManager = ConnectionManager.getInstance();
		
        mDeviceFinder = new SSDPFinder(this);
        mDevice = mData.mDevice;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean action = intent.getBooleanExtra(PowerAlarm.ALARM_ACTION, false);
		setStatus(null, action);
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}

	public WemoData getSettings() {
		return mData;
	}
	
	public void connect(Handler handler) {
		mHandler = handler;
		if (mDevice != null) {
			mConnectionManager.push(new WemoManagerTask(mConnectListener, mDevice));
			return;
		}

		mDeviceFinder.start();
	}
	
	@Override
	public void onDeviceFound(Device device) {
		mData.exportSettings(device);
		mDevice = device;
		
		Message message = Message.obtain(mHandler, DomoSystem.ERROR_NONE);
		mHandler.sendMessage(message);
	}
	
	@Override
	public void onFindTimeout() {
		Message message = Message.obtain(mHandler, DomoSystem.ERROR_FIND);
		mHandler.sendMessage(message);
	}
	
	public void getStatus(Handler handler) {
		mConnectionManager.push(new WemoManagerTask(handler, mDevice));
	}
	
	public void setStatus(Handler handler, boolean value) {
		mConnectionManager.push(new WemoManagerTask(handler, mDevice, value));
	}
	
	private static class WeakRefHandler extends Handler {
		private WeakReference<WemoService> ref;

		public WeakRefHandler(WemoService service) {
			this.ref = new WeakReference<WemoService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			WemoService service = ref.get();
			
			int error = msg.what;
			
			if (error == DomoSystem.ERROR_NONE) {
				
				Message message = service.mHandler.obtainMessage();
				message.copyFrom(msg);
				service.mHandler.sendMessage(message);
			} else {
				service.mDeviceFinder.start();
			}
		}
	};

	protected WeakRefHandler mConnectListener = new WeakRefHandler(this);
}
