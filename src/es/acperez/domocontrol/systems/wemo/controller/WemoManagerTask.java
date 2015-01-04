package es.acperez.domocontrol.systems.wemo.controller;

import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.common.connectionManager.ConnectionManagerTask;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class WemoManagerTask extends ConnectionManagerTask {
	protected static final int GET = 0;
	protected static final int SET = 1;
		
	private Handler mHandler;
	private int mRequest;
	private boolean mValue;
	private Device mDevice;
	
	public WemoManagerTask(Handler handler, Device device) {
		mHandler = handler;
		mDevice = device;
		mRequest = GET;
	}

	public WemoManagerTask(Handler handler, Device device, boolean value) {
		mHandler = handler;
		mDevice = device;
		mRequest = SET;
		mValue = value;
	}
	
	@Override
	public void doRun() {
    	int result = DomoSystem.ERROR_NONE;
    	boolean status = false;
    	
    	try {
			switch (mRequest) {
			case GET:
				status = mDevice.getState();
				break;
			case SET:
				mDevice.setState(mValue);
				status = mValue;
				break;
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = DomoSystem.ERROR_NETWORK;
    	}
    	
    	if (mHandler != null) {
    		Message message = Message.obtain(mHandler, result, status);
    		mHandler.sendMessage(message);
    	}
	}
}