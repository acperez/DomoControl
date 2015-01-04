package es.acperez.domocontrol.systems.light.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.LightSystem;
import es.acperez.domocontrol.systems.light.controller.LightRequest;
import es.acperez.domocontrol.systems.power.controller.PowerAlarm;

public class LightService extends Service {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private final IBinder mBinder = new LightBinder();
	
	private LightServiceData mData;
	private PHHueSDK phHueSDK;
	private PHBridge mBridge;
	private Handler mHandler;
	private boolean mFind = false;
	private boolean mIsConnected = false;
	private LightUpdateListener mListener;
	public static final String hardUsername = "DomoControl";
	
	public interface LightUpdateListener {
		void onLightRequestDone(int type, ArrayList<String> lightIds);
	}
	
	public interface LightSystemRequest {
		void run(PHBridge bridge, Handler handler);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class LightBinder extends Binder {
		public LightService getService() {
            return LightService.this;
        }
    }
	
	@Override
	public void onCreate() {
		super.onCreate();

		mData = new LightServiceData(this);
		mData.importSettings();
		
		phHueSDK = PHHueSDK.create();
        phHueSDK.setDeviceName("DomoControl");
	}

	public LightServiceData getSettings() {
		return mData;
	}
	
	private PHSDKListener sdkListener = new PHSDKListener() {

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
        	System.out.println("CONNECTED");
        	System.out.println("On bridge connected");
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            mBridge = b;
            
            int result = LightSystem.UPDATE_BRIDGE;
            
            PHBridgeConfiguration config = b.getResourceCache().getBridgeConfiguration();
            if (mFind && (mData.mServer != config.getIpAddress() || mData.mUsername != config.getUsername())) {
            	mData.mServer = config.getIpAddress();
            	mData.mUsername = config.getUsername();
            	mData.exportSettings();
            	result = LightSystem.UPDATE_SETTINGS;
            }
            
            mIsConnected = true;
            
    		Message message = Message.obtain(mHandler, DomoSystem.ERROR_NONE, result);
    		mHandler.sendMessage(message);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        	System.out.println("AUTH REQ");
            System.out.println("Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            
            Message message = Message.obtain(mHandler, DomoSystem.ERROR_NOTIFY);
            mHandler.sendMessage(message);
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
            	Message message = Message.obtain(mHandler, DomoSystem.ERROR_NONE, LightSystem.UPDATE_BRIDGE);
            	mHandler.sendMessage(message);
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

            Message msg = Message.obtain(mHandler, DomoSystem.ERROR_NETWORK, code);
            mHandler.sendMessage(msg);
        }
        
		@Override
		public void onCacheUpdated(List<Integer> flags, PHBridge bridge) {
			System.out.println("On CacheUpdated " + flags.get(0));
			if (!running) {
				Message message = Message.obtain(mHandler, DomoSystem.STATUS_ONLINE, LightSystem.REMOTE_UPDATE_LIGHTS);
				mHandler.sendMessage(message);
			}
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> arg0) {
			System.out.println("On parsing errors");
		}
    };

    private String getUsername() {
    	String username = mData.mUsername;
    	if (username == null || username.length() == 0) {
            username = PHBridgeInternal.generateUniqueKey();
    	}
    	
    	return username;
    }
    
    public void connect(Handler handler) {
    	mHandler = handler;
    	if (mData.mServer != null && mData.mServer.length() > 0) {
			mFind = false;
			PHAccessPoint accessPoint = new PHAccessPoint();
	        accessPoint.setIpAddress(mData.mServer);
	        accessPoint.setUsername(hardUsername);
	       
	        if (!phHueSDK.isAccessPointConnected(accessPoint)) {
	        	phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
	        	phHueSDK.connect(accessPoint);
	        	return;
	        }
		} else {
			mFind = true;
			phHueSDK.getNotificationManager().unregisterSDKListener(sdkListener);
			phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
			PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
			sm.search(true, true);
		}
    }
    
    public void disconnect() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            phHueSDK.getNotificationManager().unregisterSDKListener(sdkListener);
        }
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
    
	public void updateLights(LightSystemRequest request) {
		mRequest.set(request);
		doRequest();
	}
	
	public void setUpdateListener(LightUpdateListener listener) {
		mListener = listener;
	}
	
	private final AtomicReference<LightSystemRequest> mRequest = new AtomicReference<LightSystemRequest>();
	private boolean running = false;
	
	private void doRequest() {
		if (running)
			return;
		
		LightSystemRequest request = mRequest.getAndSet(null);
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
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean action = intent.getBooleanExtra(PowerAlarm.ALARM_ACTION, false);
		
		PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(mData.mServer);
        accessPoint.setUsername(hardUsername);
        
        if (!phHueSDK.isAccessPointConnected(accessPoint)) {
    		phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
    		mHandler = new WeakRefHandler(this, action);
            phHueSDK.connect(accessPoint);
        } else {
			Map<String, PHLight> lights = getLights();
			ArrayList<String> ids = new ArrayList<String>(lights.keySet());
			updateLights(new LightRequest(ids, action));
        }
		
		stopSelf();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private static class WeakRefHandler extends Handler {
		private WeakReference<LightService> ref;
		private boolean action;

		public WeakRefHandler(LightService service, boolean action) {
			this.ref = new WeakReference<LightService>(service);
			this.action = action;
		}

		@Override
		public void handleMessage(Message msg) {
			final LightService service = ref.get();
			
			if (msg.what == DomoSystem.ERROR_NONE) {
				service.mListener = new LightUpdateListener() {
					
					@Override
					public void onLightRequestDone(int type, ArrayList<String> lightIds) {
						service.disconnect();
					}
				};
				
				Map<String, PHLight> lights = service.getLights();
				ArrayList<String> ids = new ArrayList<String>(lights.keySet());
				service.updateLights(new LightRequest(ids, action));
			}
		}
	};
}