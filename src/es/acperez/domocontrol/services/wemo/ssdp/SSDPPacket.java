package es.acperez.domocontrol.services.wemo.ssdp;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Locale;

public class SSDPPacket {
    public static final String ROOTDEVICE = "upnp:rootdevice";
    public static final String ROOTDEVICE2 = "\"upnp:rootdevice\"";
    public static final String EVENT = "upnp:event";
    
	private String NT;
	private String ST;
	private String USN;
	public String UDN;
	public String serialNumber;
	public String location;
	HashMap<String, String> mPacket;
	
	public SSDPPacket(DatagramPacket packet) {
		mPacket = new HashMap<String, String>();
		try {
			LineNumberReader reader = getReader(packet);
			for (String lineStr = reader.readLine(); lineStr != null; lineStr = reader.readLine()) {
				if (lineStr.length() <= 0)
					break;
				
				String[] header = headerReader(lineStr);
				if (header != null)
					mPacket.put(header[0].toUpperCase(Locale.getDefault()), header[1]);
	        }
		}
        catch (IOException e) {
            e.printStackTrace();
        }
		
		NT = mPacket.get("NT");
		ST = mPacket.get("ST");
		USN = mPacket.get("USN");
		UDN = getUDN(USN);
		serialNumber = getSerialNumber(UDN);
		location = mPacket.get("LOCATION");
	}
	
	public boolean isRootDevice() {
		if (NT != null && NT.startsWith(ROOTDEVICE))
			return true;
		
		if (ST != null && (ST.equals(ROOTDEVICE) || ST.equals(ROOTDEVICE2)))
			return true;
		
		if (USN != null && USN.startsWith(ROOTDEVICE))
			return true;

		return false;
	}
	
    private String getUDN(String usn) {
        if (usn == null) {
            return "";
        }
        
        int idx = usn.indexOf("::");
        if (idx < 0) {
            return usn.trim();
        }
        
        String udn = new String(usn.getBytes(), 0, idx);
        return udn.trim();
    }
    
    private String getSerialNumber(String udn) {
    	return udn.substring(udn.lastIndexOf("-") + 1, udn.length());
    }


	public static boolean validateSSDPPacket(DatagramPacket packet) {
		LineNumberReader reader = getReader(packet);
        
		String name = "USN";
		try {
			for (String lineStr = reader.readLine(); lineStr != null; lineStr = reader.readLine()) {
				if (lineStr.length() <= 0)
					break;
				
				String[] header = headerReader(lineStr);
				if (header != null && header[0].toUpperCase(Locale.getDefault()).equals(name)) {
					
					if (header[1].contains("Controlee") || header[1].contains("Socket") ||
							header[1].contains("Sensor") || header[1].contains("Lightswitch") ||
							header[1].contains("NetCamSensor") || header[1].contains("Insight")) {
		                return true;
		            }
				}
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
		return false;
	}
	
	private static LineNumberReader getReader(DatagramPacket packet) {
		String data = new String(packet.getData(), 0, packet.getLength());
		
		StringReader strReader = new StringReader(data);
		return new LineNumberReader(strReader);
	}
	
    private static String[] headerReader(String lineStr) {
    	String[] header = new String[2];
        if (lineStr == null) {
            return null;
        }
        
        int colonPos = lineStr.indexOf(58);
        if (colonPos < 0) {
            return null;
        }
        
        String name = new String(lineStr.getBytes(), 0, colonPos);
        String value = new String(lineStr.getBytes(), colonPos + 1, lineStr.length() - colonPos - 1);
        header[0] = name.trim();
        header[1] = value.trim();
        
        return header;
    }
}
