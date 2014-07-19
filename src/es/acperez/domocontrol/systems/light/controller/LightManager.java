package es.acperez.domocontrol.systems.light.controller;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.connection.impl.PHBridgeInternal;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHScene;

import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemManager;

public class LightManager extends SystemManager {
	
	public static final int CONNECT = 0;
	public static final int GET_CONFIG = 1;
	
	public static final String SERVER = "light.host";
	public static final String USERNAME = "light.username";
	
	public static final String hardUsername = "DomoControl";
	
	private LightDevice mDevice;
	private PHHueSDK phHueSDK;
	private PHBridge mBridge;
	private Handler handler;
	private boolean mFind = false;

	public LightManager(LightDevice device) {
		this.systemType = DomoSystem.TYPE_LIGHT;
		
		phHueSDK = PHHueSDK.create();
		
        phHueSDK.setDeviceName("DomoControl");
		phHueSDK.getNotificationManager().registerSDKListener(listener);
		mDevice = device;
	}

	@Override
	public void processRequest(Handler handler, int request, Bundle params) {
		this.handler = handler;

		switch (request) {
			case CONNECT:
				connect();
				break;
			case GET_CONFIG:
				requestConfig();
				break;
		}
	}
	
	public void connect() {
		if (mDevice.mServer != null && mDevice.mServer.length() > 0) {
			mFind = false;
			PHAccessPoint accessPoint = new PHAccessPoint();
	        accessPoint.setIpAddress(mDevice.mServer);
	        accessPoint.setUsername(hardUsername);
	       
	        if (!phHueSDK.isAccessPointConnected(accessPoint)) {
	           phHueSDK.connect(accessPoint);
	           return;
	        }
		} else {
			mFind = true;
			PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
			sm.search(true, true);
		}
	}
	
	private void requestConfig() {
		List<PHLight> lights = mBridge.getResourceCache().getAllLights();
		List<PHGroup> groups = mBridge.getResourceCache().getAllGroups();
		List<PHScene> scenes = mBridge.getResourceCache().getAllScenes();
		
		System.out.println("sadsad");
//		phHueSDK.getGroupNames(groups);
//		phHueSDK.getLightNames(lights);
	}
	
	public void close() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
        }
	}
	
	public void setLigths(int aR, int aG, int aB) {

		
		PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        for (PHLight light : allLights) {
        	
            float xy[] = PHUtilities.calculateXYFromRGB(aR, aG, aB, light.getModelNumber());
            PHLightState lightState = new PHLightState();
            lightState.setX(xy[0]);
        	lightState.setY(xy[1]);
        	
            bridge.updateLightState(light, lightState);
        }
	}
	
	private PHSDKListener listener = new PHSDKListener() {

		@Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoints) {
            if (accessPoints != null && accessPoints.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoints);

                PHAccessPoint accessPoint = accessPoints.get(0);
                accessPoint.setUsername(getUsername());
                
                PHBridge connectedBridge = phHueSDK.getSelectedBridge();       

                if (connectedBridge != null) {
                    String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                    if (connectedIP != null) {
                        phHueSDK.disableHeartbeat(connectedBridge);
                        phHueSDK.disconnect(connectedBridge);
                    }
                }
                
                phHueSDK.connect(accessPoint);
            }
        }
        
        @Override
        public void onCacheUpdated(int flags, PHBridge bridge) {
            System.out.println("On CacheUpdated");
        }

        @Override
        public void onBridgeConnected(PHBridge b) {
        	System.out.println("On bridge connected");
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            mBridge = b;
            
            if (mFind) {
            	PHBridgeConfiguration config = b.getResourceCache().getBridgeConfiguration();
            	mDevice.connected(config.getIpAddress(), hardUsername);
//            	mDevice.connected(config.getIpAddress(), config.getUsername());
            }
            
    		Message message = Message.obtain(handler, DomoSystem.ERROR_NONE);
    		handler.sendMessage(message);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            System.out.println("Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            
            Message message = Message.obtain(handler, DomoSystem.ERROR_NOTIFY);
    		handler.sendMessage(message);
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            System.out.println("onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            System.out.println("onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }
        
        @Override
        public void onError(int code, final String message) {
            System.out.println("on Error Called : " + code + ":" + message);

            if (code == PHHueError.NO_CONNECTION) {
                System.out.println("No Connection");
            } else if (code == PHHueError.AUTHENTICATION_FAILED || code==1158) {  
                System.out.println("auth failed");
            } else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                System.out.println("Bridge Not Responding . . . ");
            } else if (code == PHMessageType.BRIDGE_NOT_FOUND) {
            	System.out.println("bridge not found");
            } else if (code == PHMessageType.PUSHLINK_BUTTON_NOT_PRESSED) {
            		return;
            }

            Message msg = Message.obtain(handler, DomoSystem.ERROR_NETWORK, code);
    		handler.sendMessage(msg);
        }
    };
    
    private String getUsername() {
    	String username = mDevice.mUsername;
    	if (username == null || username.length() == 0) {
            username = PHBridgeInternal.generateUniqueKey();
    	}
    	
    	return username;
    }
}