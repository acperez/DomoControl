package es.acperez.domocontrol.systems.wemo.controller.SSDP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

import android.os.Handler;
import es.acperez.domocontrol.systems.wemo.controller.Device;
import es.acperez.domocontrol.systems.wemo.controller.WemoHTTPMsg;

public class SSDPFinder implements Runnable {
	private final int PORT = 8008;
	private final int TIMEOUT = 4 * 1000;

	private DatagramSocket mSocket;
	private InetAddress mAddress;
	private SSDPListener mListener;
	private Thread mThread;
	private Handler mTimeoutHandler;
	
	public interface SSDPListener {
		void onDeviceFound(Device device);
		void onFindTimeout();
	}
	
	public SSDPFinder(SSDPListener listener) {
        mThread = new Thread(this);
		mAddress = getHostAddress();
		mListener = listener;
		mTimeoutHandler = new Handler();
	}
	
	public void start() {
		mThread.start();
		mTimeoutHandler.postDelayed(mTimeoutRunnable, TIMEOUT );
	}
	
	Runnable mTimeoutRunnable = new Runnable() {
		@Override
		public void run() {
			mListener.onFindTimeout();
		}
	};
	
	@Override
	public void run() {
		try {
			InetSocketAddress bindInetAddr = new InetSocketAddress(mAddress, PORT);
			
			mSocket = new DatagramSocket(bindInetAddr);
			mSocket.setReuseAddress(true);
			
			search();
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, 1024);
			
			boolean found = false;
			while (!found) {
				mSocket.receive(packet);
				
				if (SSDPPacket.validateSSDPPacket(packet)) {
					Device device = onDeviceFound(new SSDPPacket(packet));
					mListener.onDeviceFound(device);
					found = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
	}
	
	private Device onDeviceFound(SSDPPacket packet) {
		if (!packet.isRootDevice())
            return null;
		
		try {
			URL locationUrl = new URL(packet.location);
			String deviceInfo = getDeviceInfo(locationUrl);
			locationUrl = new URL(locationUrl.getProtocol(), locationUrl.getHost(), locationUrl.getPort(), "");
	
			String tagStart = "<deviceType>";
			String tagEnd = "</deviceType>";
			
			int posStart = deviceInfo.indexOf(tagStart);
			int posEnd = deviceInfo.indexOf(tagEnd);
			
			if (posStart == -1 || posEnd == -1)
				return null;
			
			String deviceType = deviceInfo.substring(posStart + tagStart.length(), posEnd);
			
			Device device = new Device(locationUrl, deviceType);
			if (device.validType()) {
				return device;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private String getDeviceInfo(URL url) throws Exception {
		String data = null;
		
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Length", "0");
        if (url.getHost() != null) {
            connection.setRequestProperty("HOST", url.getHost());
        }
        byte[] buffer = new byte[1024];
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        inputStream.close();
        outputStream.close();
        data = outputStream.toString();
        connection.disconnect();
        
        return data;
	}
	
    public static InetAddress getHostAddress() {
		try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> addrs = ni.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr.isLoopbackAddress() || addr instanceof Inet6Address) {
                        continue;
                    }
                    
                    return addr;
                }
            }
		} catch (SocketException e) {
			e.printStackTrace();
		}

        return null;
    }
    
	public void search() {
		DatagramPacket packet = WemoHTTPMsg.searchRequest();
		
		try {
			mSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
