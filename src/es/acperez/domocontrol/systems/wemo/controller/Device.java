package es.acperez.domocontrol.systems.wemo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import es.acperez.domocontrol.systems.wemo.controller.SSDP.SSDPPacket;

public class Device {
	private static final int CONNECTION_RETIES = 3;
	public String mDeviceType;
	public URL mBaseURL;
	
	public Device(URL location, String deviceType) {
		mBaseURL = location;
		mDeviceType = deviceType;
	}

	public boolean validType() {
		return (mDeviceType.equalsIgnoreCase("urn:Belkin:device:socket:1") ||
				mDeviceType.equalsIgnoreCase("urn:Belkin:device:sensor:1") ||
				mDeviceType.equalsIgnoreCase("urn:Belkin:device:lightswitch:1") ||
				mDeviceType.equalsIgnoreCase("urn:Belkin:device:controllee:1") ||
				mDeviceType.equalsIgnoreCase("urn:Belkin:device:NetCamSensor:1") ||
				mDeviceType.equalsIgnoreCase("urn:Belkin:device:insight:1"));
	}
	
	public boolean getState() throws Exception {
		int retry = CONNECTION_RETIES;
		Exception exception = new Exception();
		while (retry > 0) {
			try {
				String msg = WemoHTTPMsg.getState();
		    	byte[] requestData = msg.getBytes("UTF-8");
		    	URL url = new URL(mBaseURL.getProtocol(), mBaseURL.getHost(), mBaseURL.getPort(), "/upnp/control/basicevent1");
	
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    connection.setReadTimeout(6000);
			    connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
			    connection.setRequestProperty("HOST", mBaseURL.getHost());
			    connection.setRequestProperty("Content-Length", "" + requestData.length);
			    connection.setRequestProperty("SOAPACTION", "\"urn:Belkin:service:basicevent:1#GetBinaryState\"");
			    
			    connection.setRequestMethod("POST");
			    connection.setDoOutput(true);
			    connection.setDoInput(true);
		
			    OutputStream os = connection.getOutputStream();
			    os.write(requestData, 0, requestData.length);
			    os.flush();
			    os.close();
			    
		    	InputStream is = connection.getInputStream();
		    	String response = readStream(is);
		    	is.close();
		    	connection.disconnect();
		    	return parseGetBinaryState(response);
		    	
			} catch (Exception e) {
				exception = e;
				retry--;
			}
		}
		
		throw exception;
	}
	
	public void setState(boolean state) throws Exception {
		int retry = CONNECTION_RETIES;
		Exception exception = new Exception();
		while (retry > 0) {
			try {
				String msg = WemoHTTPMsg.setState(state);
			    byte[] requestData = msg.getBytes("UTF-8");
			    URL url = new URL(mBaseURL.getProtocol(), mBaseURL.getHost(), mBaseURL.getPort(), "/upnp/control/basicevent1");
			    
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    connection.setRequestProperty("Content-Type", "text/xml; charset=\"utf-8\"");
			    connection.setRequestProperty("HOST", mBaseURL.getHost());
			    connection.setRequestProperty("Content-Length", "" + requestData.length);
			    connection.setRequestProperty("SOAPACTION", "\"urn:Belkin:service:basicevent:1#SetBinaryState\"");
			    
			    connection.setRequestMethod("POST");
			    connection.setDoOutput(true);
			    connection.setDoInput(true);
		
			    OutputStream os = connection.getOutputStream();
			    os.write(requestData, 0, requestData.length);
			    os.flush();
			    os.close();
			    
			    InputStream is = connection.getInputStream();
			    is.close();
			    connection.disconnect();
			    return;
			} catch (Exception e) {
				exception = e;
				retry--;
			}
		}
		
		throw exception;
	}

	private String readStream(InputStream is) {
		int ch;
		StringBuilder sb = new StringBuilder();
		try {
			while ((ch = is.read()) != -1)
				sb.append((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	private boolean parseGetBinaryState(String response) throws Exception {
		String field = "<BinaryState>";
		if (response.contains(field)) {
			int pos = response.indexOf(field) + field.length();
			return "1".equals(response.substring(pos, pos + 1));
		}

		throw new Exception("Invalid response for GetBinaryState");
	}
	
	public static Device createDevice(SSDPPacket packet, String deviceStr) throws MalformedURLException {
		String tagStart = "<deviceType>";
		String tagEnd = "</deviceType>";
		
		int posStart = deviceStr.indexOf(tagStart);
		int posEnd = deviceStr.indexOf(tagEnd);
		
		if (posStart == -1 || posEnd == -1)
			return null;
		
		String deviceType = deviceStr.substring(posStart + tagStart.length(), posEnd);
		
        URL url = new URL(packet.location);
        url = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
		Device device = new Device(url, deviceType);
		if (device.validType()) {
			return device;
		}
		
		return null;
	}
}