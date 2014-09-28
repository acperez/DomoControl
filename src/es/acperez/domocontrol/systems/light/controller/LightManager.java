package es.acperez.domocontrol.systems.light.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.connection.impl.PHBridgeInternal;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;

import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.base.SystemManager;
import es.acperez.domocontrol.systems.light.LightSystem;

public class LightManager extends SystemManager {
	
	public static final int CONNECT = 0;
	public static final int GET_CONFIG = 1;
	
	public static final String SERVER = "light.host";
	public static final String USERNAME = "light.username";
	
	public static final String hardUsername = "DomoControl";
	
	private boolean mIsConnected = false;
	
	private LightSystem mSystem;
	private PHHueSDK phHueSDK;
	private PHBridge mBridge;
	private Handler handler;
	private boolean mFind = false;
	private LightManagerListener mListener;
	
	public interface LightManagerListener {
		void onLightRequestDone(int type, ArrayList<String> lightIds);
	}
	
	public interface LightManagerRequest {
		void run(PHBridge bridge, Handler handler);
	}

	public LightManager(LightSystem system) {
		this.systemType = DomoSystem.TYPE_LIGHT;
		
		phHueSDK = PHHueSDK.create();
		
        phHueSDK.setDeviceName("DomoControl");
		phHueSDK.getNotificationManager().registerSDKListener(listener);
		mSystem = system;
		mListener = system;
	}

	@Override
	public void processRequest(Handler handler, int request, Bundle params) {
		this.handler = handler;

		switch (request) {
			case CONNECT:
				connect();
				break;	
		}
	}
	
	public void connect() {
		if (mSystem.mServer != null && mSystem.mServer.length() > 0) {
			mFind = false;
			PHAccessPoint accessPoint = new PHAccessPoint();
	        accessPoint.setIpAddress(mSystem.mServer);
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
	
	public void close() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
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
        public void onBridgeConnected(PHBridge b) {
        	System.out.println("On bridge connected");
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            mBridge = b;
            
            if (mFind) {
            	PHBridgeConfiguration config = b.getResourceCache().getBridgeConfiguration();
            	mSystem.connected(config.getIpAddress(), hardUsername);
//            	mDevice.connected(config.getIpAddress(), config.getUsername());
            }
            
            mIsConnected = true;
            
    		Message message = Message.obtain(handler, DomoSystem.ERROR_NONE, LightSystem.UPDATE_BRIDGE);
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
            
            if (mIsConnected == false) {
            	mIsConnected = true;
            	Message message = Message.obtain(handler, DomoSystem.ERROR_NONE, LightSystem.UPDATE_BRIDGE);
            	handler.sendMessage(message);
            }
        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            System.out.println("onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
            
            mIsConnected = false;
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
        
		@Override
		public void onCacheUpdated(List<Integer> flags, PHBridge bridge) {
			System.out.println("On CacheUpdated " + flags.get(0));
			if (!running) {
				Message message = Message.obtain(handler, DomoSystem.STATUS_ONLINE, LightSystem.REMOTE_UPDATE_LIGHTS);
	    		handler.sendMessage(message);
			}
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> arg0) {
			System.out.println("On parsing errors");
		}
    };
    
    private String getUsername() {
    	String username = mSystem.mUsername;
    	if (username == null || username.length() == 0) {
            username = PHBridgeInternal.generateUniqueKey();
    	}
    	
    	return username;
    }
    
    public List<PHLight> getAllLights() {
    	try {
    		return mBridge.getResourceCache().getAllLights();
    	} catch (Exception e) {
    		return null;
    	}
	}
    
    public Map<String, PHLight> getLights() {
    	try {
    		return mBridge.getResourceCache().getLights();
    	} catch (Exception e) {
    		return null;
    	}
	}
    
	public void updateLights(LightManagerRequest request) {
		mRequest.set(request);
		doRequest();
	}
    
    private final AtomicReference<LightManagerRequest> mRequest = new AtomicReference<LightManagerRequest>();
	private boolean running = false;
	
	private void doRequest() {
		if (running)
			return;
		
		LightManagerRequest request = mRequest.getAndSet(null);
		if (request == null)
			return;
		
		running = true;

		request.run(mBridge, mLightRequestsHandler);
	}
	
	Handler mLightRequestsHandler = new Handler(Looper.getMainLooper()) {

		@SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
			running = false;
			doRequest();
			mListener.onLightRequestDone(msg.what, (ArrayList<String>) msg.obj);
        }
    };
}