package es.acperez.domocontrol.services.wemo.ssdp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WemoHTTPMsg {
	private final static String NEWLINE = "\r\n";
	private final static String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
	private final static String ADDRESS = "239.255.255.250";
//	private final static String ADDRESS_IP6 = "FF02::C";
	private final static int PORT = 1900;
	private final static String MAN = "\"ssdp:discover\"";
	private final static int MX = 3; // Maximum time (seconds) to wait for the M-SEARCH response
    private final static String ST = "upnp:rootdevice";
    
    public static DatagramPacket searchRequest() {
        StringBuilder msg = new StringBuilder();
        msg.append(SL_MSEARCH).append(NEWLINE);
       	msg.append("HOST: " + ADDRESS + ":" + PORT).append(NEWLINE);
        msg.append("MAN: " + MAN).append(NEWLINE);
        msg.append("MX: " + MX).append(NEWLINE);
        msg.append("ST: " + ST).append(NEWLINE);
        msg.append(NEWLINE);
        
		DatagramPacket packet = null;
		try {
			InetAddress inetAddr = InetAddress.getByName(ADDRESS);
			packet = new DatagramPacket(msg.toString().getBytes(), msg.length(), inetAddr, PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
        return packet;
    }
    
    public static String getState() {
        StringBuilder msg = new StringBuilder();
        msg.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append(NEWLINE);
        msg.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">").append(NEWLINE);
    	msg.append("   <s:Body>").append(NEWLINE);
        msg.append("      <u:GetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\"></u:GetBinaryState>").append(NEWLINE);
        msg.append("   </s:Body>").append(NEWLINE);
        msg.append("</s:Envelope>").append(NEWLINE);
        msg.append(NEWLINE);
        
        return msg.toString();
    }
    
    public static String setState(boolean state) {
        StringBuilder msg = new StringBuilder();
        msg.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append(NEWLINE);
        msg.append("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">").append(NEWLINE);
    	msg.append("   <s:Body>").append(NEWLINE);
        msg.append("      <u:SetBinaryState xmlns:u=\"urn:Belkin:service:basicevent:1\">").append(NEWLINE);
        msg.append("         <BinaryState>" + (state ? 1 : 0) + "</BinaryState>").append(NEWLINE);
        msg.append("         <Duration></Duration>").append(NEWLINE);
        msg.append("         <EndAction></EndAction>").append(NEWLINE);
        msg.append("         <UDN></UDN>").append(NEWLINE);
        msg.append("      </u:SetBinaryState>").append(NEWLINE);
        msg.append("   </s:Body>").append(NEWLINE);
        msg.append("</s:Envelope>").append(NEWLINE);
        msg.append(NEWLINE);
        
        return msg.toString();
    }
}
