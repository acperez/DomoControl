package es.acperez.domocontrol.services.wemo;

import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.common.connection.ConnectionManagerTask;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.ServiceMessage;

public class WemoManagerTask extends ConnectionManagerTask {
	protected static final int GET = 0;
	protected static final int SET = 1;
		
	private Handler mHandler;
	private int mRequestId;
	private int mRequest;
	private boolean mValue;
	private Device mDevice;
	
	public WemoManagerTask(Handler handler, int requestId, Device device) {
		mHandler = handler;
		mDevice = device;
		mRequest = GET;
		mRequestId = requestId;
	}

	public WemoManagerTask(Handler handler, int requestId, Device device, boolean value) {
		mHandler = handler;
		mDevice = device;
		mRequest = SET;
		mValue = value;
		mRequestId = requestId;
	}
	
	@Override
	public void doRun() {
    	int result = DomoController.ERROR_NONE;
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
    		result = DomoController.ERROR_NETWORK;
    	}
    	
    	if (mHandler != null) {
    		Message message = Message.obtain(mHandler, mRequestId, new ServiceMessage(result, 0, status));
    		mHandler.sendMessage(message);
    	}
	}
}