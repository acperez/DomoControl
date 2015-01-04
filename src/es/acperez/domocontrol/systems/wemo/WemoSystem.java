package es.acperez.domocontrol.systems.wemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import es.acperez.domocontrol.R;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemFragment;
import es.acperez.domocontrol.systems.wemo.service.WemoService;
import es.acperez.domocontrol.systems.wemo.service.WemoService.WemoBinder;

public class WemoSystem extends DomoSystem {
	final public static String DEVICE_STATUS = "wemo.socket.status";
	final public static String DEVICE_URL = "wemo.socket.locationURL";
	final public static String DEVICE_TYPE = "wemo.socket.deviceType";
	final public static String DEVICE_NAME = "wemo.socket.deviceName";
	
	public WemoData mData;
	private WemoService mService;
	
    public WemoSystem(Context context, DomoSystemStatusListener listener) {
		super(context.getResources().getString(R.string.system_name_wemo), DomoSystem.TYPE_WEMO, listener);

		this.mData = new WemoData(context);
		mData.importSettings();
        
		Intent intent = new Intent(context, WemoService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	protected SystemFragment createFragment() {
		return new WemoFragment(this);
	}
	
	public void requestPlugSwitch() {
		mService.setStatus(mRequestListener, !mData.mStatus);
	}
	
	@Override
	public void requestResponse(Message msg) {
		if (msg.obj != null)
			mData.mStatus = (Boolean)msg.obj;
		
		mFragment.updateContent(0, null);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WemoBinder binder = (WemoBinder) service;
            mService = binder.getService();

            mService.connect(mRequestListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
}
