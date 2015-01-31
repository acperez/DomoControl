package es.acperez.domocontrol.services.gembird;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.common.connection.ConnectionManagerTask;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.services.ServiceMessage;

public class PowerManagerTask extends ConnectionManagerTask {
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private PowerDevice powerDevice;
	
	protected static final int GET = 0;
	protected static final int SET = 1;
	
    protected static final byte[] BYE = {0x01, 0x02, 0x03, 0x04};
    protected static final byte HELLO = 0x11;

		
	private Handler mHandler;
	private int mRequestId;
	private int mRequest;
	private int mPlug;
	private boolean mValue;
	private String mHost;
	private int mPort;
	private String mPassword;
	
	public PowerManagerTask(Handler handler, int requestId, String host, int port, String password) {
		mHandler = handler;
		mHost = host;
		mPort = port;
		mPassword = password;
		mRequest = GET;
		mRequestId = requestId;
		mPlug = 0;
	}

	public PowerManagerTask(Handler handler, int requestId, String host, int port, String password, int plug, boolean value) {
		mHandler = handler;
		mHost = host;
		mPort = port;
		mPassword = password;
		mRequest = SET;
		mPlug = plug;
		mValue = value;
		mRequestId = requestId;
	}
	
	@Override
	public void doRun() {
    	int result = DomoController.ERROR_NONE;
    	powerDevice = new PowerDevice(mPassword);
    	boolean plugsStatus[] = null;
    	
    	try {
			InetAddress serverAddr = InetAddress.getByName(mHost);
			socket = new Socket(serverAddr, mPort);
			socket.setSoTimeout(4 * 1000);
			
			is = socket.getInputStream();
			os = socket.getOutputStream();
    	
			switch (mRequest) {
			case GET:
				result = doGetStatus();
				break;
			case SET:
				result = doSetStatus();
				break;
			}
			
			os.write(BYE);
			
			is.close();
			os.close();
			socket.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = DomoController.ERROR_NETWORK;
    	}

    	if (result == DomoController.ERROR_NONE) {
    		plugsStatus = powerDevice.plugsStatus;
    	}
    	
    	if (mHandler != null) {
    		Message message = Message.obtain(mHandler, mRequestId, new ServiceMessage(result, mPlug, plugsStatus));
    		mHandler.sendMessage(message);
    	}
	}
    
    private int doSetStatus() throws IOException {
		byte[] buffer = new byte[4];
		
		int result = doGetStatus();
		if (result != DomoController.ERROR_NONE) {
			return result;
		}

		byte[] message = powerDevice.setStatus(mPlug, mValue);
		os.write(message);
		is.read(buffer);

		powerDevice.addStatus(buffer);
		
		return DomoController.ERROR_NONE;
    }

	private int doGetStatus() throws IOException {
		byte[] buffer = new byte[4];
		
		os.write(HELLO);
		is.read(buffer);
		powerDevice.addToken(buffer);

		os.write(powerDevice.tokenResponse);
		
		try {
			is.read(buffer);
			
		} catch (IOException e) {
			// Invalid Password
			System.out.println("Error invalid password");
			return DomoController.ERROR_AUTH;
		}
		
		powerDevice.addStatus(buffer);
		return DomoController.ERROR_NONE;
	}
}