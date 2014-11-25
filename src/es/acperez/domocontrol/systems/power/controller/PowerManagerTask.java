package es.acperez.domocontrol.systems.power.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.common.connectionManager.ConnectionManagerTask;
import es.acperez.domocontrol.systems.base.DomoSystem;

public class PowerManagerTask extends ConnectionManagerTask {
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private PowerDevice powerDevice;
	
	protected static final int GET = 0;
	protected static final int SET = 1;
		
	private Handler mHandler;
	private int mRequest;
	private int mPlug;
	private boolean mValue;
	private String mHost;
	private int mPort;
	private String mPassword;
	
	public PowerManagerTask(Handler handler, String host, int port, String password) {
		mHandler = handler;
		mHost = host;
		mPort = port;
		mPassword = password;
		mRequest = GET;
	}

	public PowerManagerTask(Handler handler, String host, int port, String password, int plug, boolean value) {
		mHandler = handler;
		mHost = host;
		mPort = port;
		mPassword = password;
		mRequest = SET;
		mPlug = plug;
		mValue = value;
	}
	
	@Override
	public void doRun() {
    	int result = DomoSystem.ERROR_NONE;
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
			
			os.write(PowerDevice.BYE);
			
			is.close();
			os.close();
			socket.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = DomoSystem.ERROR_NETWORK;
    	}

    	if (result == DomoSystem.ERROR_NONE) {
    		plugsStatus = powerDevice.plugsStatus;
    	}
    	
    	if (mHandler != null) {
    		Message message = Message.obtain(mHandler, result, plugsStatus);
    		mHandler.sendMessage(message);
    	}
	}
    
    private int doSetStatus() throws IOException {
		byte[] buffer = new byte[4];
		
		int result = doGetStatus();
		if (result != DomoSystem.ERROR_NONE) {
			return result;
		}

		byte[] message = powerDevice.setStatus(mPlug, mValue);
		os.write(message);
		is.read(buffer);

		powerDevice.addStatus(buffer);
		
		return DomoSystem.ERROR_NONE;
    }

	private int doGetStatus() throws IOException {
		byte[] buffer = new byte[4];
		
		os.write(PowerDevice.HELLO);
		is.read(buffer);
		powerDevice.addToken(buffer);

		os.write(powerDevice.tokenResponse);
		
		try {
			is.read(buffer);
			
		} catch (IOException e) {
			// Invalid Password
			System.out.println("Error invalid password");
			return DomoSystem.ERROR_PASSWORD;
		}
		
		powerDevice.addStatus(buffer);
		return DomoSystem.ERROR_NONE;
	}
}