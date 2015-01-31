package es.acperez.domocontrol.services.philips.hue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.philips.lighting.model.PHLightState;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.actions.events.DomoAlarm;
import es.acperez.domocontrol.modules.base.DomoController;
import es.acperez.domocontrol.modules.monitors.light.LightController;
import es.acperez.domocontrol.services.DomoService;
import es.acperez.domocontrol.services.ServiceMessage;

public class LightService extends DomoService {
	
	public static final int GET_STATUS = 0;
	public static final int SET_STATUS = 1;
	
	private LightServiceData mData;
	private PHHueSDK phHueSDK;
	private PHBridge mBridge;
	private Handler mHandler;
	private boolean mFind = false;
	private boolean mIsConnected = false;
	public static final String hardUsername = "DomoControl";
	private ColorChangedListener mColorListener;
	private boolean running = false;
	private int mRequestsWaitingAck = 0;
	private final AtomicReference<LightSystemRequest> mRequest = new AtomicReference<LightSystemRequest>();
	
	public interface LightSystemRequest {
		void run(PHBridge bridge);
	}
	
	public interface ColorChangedListener {
		public void onLightRequestDone(ArrayList<DomoLight> lights);
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
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean action = intent.getBooleanExtra(DomoAlarm.ALARM_ACTION, false);
		
		PHAccessPoint accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(mData.mServer);
        accessPoint.setUsername(hardUsername);
        
		if (phHueSDK.isAccessPointConnected(accessPoint)) {
			Map<String, PHLight> lights = getLights();
			ArrayList<String> ids = new ArrayList<String>(lights.keySet());
			for (String id : ids)
				new LightSwitchRequest(id, action, mHandler).run(mBridge);
			
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}

   		phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
   		System.out.println(mRemoteHandler);
   		mHandler = new WeakRefWidgetHandler(this, action);
        phHueSDK.connect(accessPoint);
        
        stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public Bundle getSettings() {
		return mData.getSettings();
	}
	
	@Override
	public void saveSettings(Bundle settings) {
		mData.setSettings(settings);
	}

    private String getUsername() {
    	String username = mData.mUsername;
    	if (username == null || username.length() == 0) {
            username = PHBridgeInternal.generateUniqueKey();
    	}
    	
    	return username;
    }
    
	@Override
	protected void doConnect(Handler handler) {
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
    
    public void onDisconnect() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            phHueSDK.getNotificationManager().unregisterSDKListener(sdkListener);
        }
	}
	
    @Override
	protected void doGetSwitches(Handler handler) {
		Message message = Message.obtain(handler, DomoService.SERVICE_GET_SWITCHES, new ServiceMessage(DomoController.ERROR_NONE, 0, false));
		handler.sendMessage(message);
	}

	@Override
	protected ArrayList<DomoSwitch> doGetSwitchesResponse(ServiceMessage message) {
		List<PHLight> lights = getAllLights();
		ArrayList<DomoSwitch> switches = new ArrayList<DomoSwitch>();
		Iterator<PHLight> iterator = lights.iterator();
		while(iterator.hasNext()) {
			PHLight light = iterator.next();
			int id = Integer.valueOf(light.getIdentifier());
			switches.add(new DomoLight(id, light.getLastKnownLightState().isOn(), light.getName(), mServiceId, LightUtils.createThumb(light)));
		}
		
		return switches;
	}
	
	@Override
	protected void doGetStatus(Handler handler, int plugId) {
		boolean status = getLights().get(String.valueOf(plugId)).getLastKnownLightState().isOn();
		
		Message message = Message.obtain(handler, DomoService.SERVICE_GET_STATUS, new ServiceMessage(DomoController.ERROR_NONE, plugId, status));
		mHandler.sendMessage(message);
	}
	
	@Override
	protected void doSetStatus(Handler handler, int plugId, boolean status) {
		mRequestsWaitingAck++;
		new LightSwitchRequest(String.valueOf(plugId), status, handler).run(mBridge);
	}
	
	public void setColor(int lightId, PHLightState state) {
		mRequestsWaitingAck++;
		new LightSwitchRequest(String.valueOf(lightId), state, mHandler).run(mBridge);
	}
	
	@Override
	protected boolean doStatusResponse(ServiceMessage message) {
		return (Boolean) message.data;
	}

	public void setState(LightSystemRequest request) {
		mRequest.set(request);
		doRequest();
	}
	
	public void setState(ArrayList<DomoLight> lights, float[] color) {
		mRequest.set(new LightRequest(lights, color, mColorHandler));
		doRequest();
	}
	
	private void doRequest() {
		if (running)
			return;
		
		LightSystemRequest request = mRequest.getAndSet(null);
		if (request == null)
			return;
		
		running = true;
		mRequestsWaitingAck++;
		request.run(mBridge);
	}
	
	protected static class WeakRefHandler extends Handler {
		private WeakReference<LightService> ref;

		public WeakRefHandler(LightService service) {
			this.ref = new WeakReference<LightService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			LightService service = ref.get();
			service.running = false;
			service.mColorListener.onLightRequestDone((ArrayList<DomoLight>) msg.obj);
			service.doRequest();
		}
	};

	protected WeakRefHandler mColorHandler = new WeakRefHandler(this);

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
            
            int result = LightController.UPDATE_BRIDGE;
            
            PHBridgeConfiguration config = b.getResourceCache().getBridgeConfiguration();
            if (mFind && (mData.mServer != config.getIpAddress() || mData.mUsername != config.getUsername())) {
            	mData.mServer = config.getIpAddress();
            	mData.mUsername = config.getUsername();
            	mData.exportSettings();
            	result = LightController.UPDATE_SETTINGS;
            }
            
            mIsConnected = true;
 
            Message message = Message.obtain(mHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_NONE, result, false));
            mHandler.sendMessage(message);
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        	System.out.println("AUTH REQ");
            System.out.println("Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            
            Message message = Message.obtain(mHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_NOTIFY, 0, false));
            mHandler.sendMessage(message);
        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            System.out.println("onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {
                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                	System.out.println("connection resumed remove algo");
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }
            
            if (mIsConnected == false) {
            	System.out.println("onconnection resumed connect again");
            	mIsConnected = true;
                Message message = Message.obtain(mHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_NONE, LightController.UPDATE_BRIDGE, false));
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

            Message msg = Message.obtain(mHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoController.ERROR_NETWORK, code, false));
            mHandler.sendMessage(msg);
        }
        
		@Override
		public void onCacheUpdated(List<Integer> flags, PHBridge bridge) {
//			System.out.println("On CacheUpdated " + flags.get(0));
//			
//			PHLight light = getLights().get("4");
//			System.out.println("Current color: " + light.getLastKnownLightState().getX() + " - " + light.getLastKnownLightState().getY());
//			
//			if (mRequestsWaitingAck == 0) {
//				// Change from external app
//				System.out.println("SEND STARTTTTTT");
//                Message message = Message.obtain(mHandler, DomoService.SERVICE_CONNECTION, new ServiceMessage(DomoSystem.ERROR_NONE, LightSystem.REMOTE_UPDATE_LIGHTS, false));
//                mHandler.sendMessage(message);
//                return;
//			} else {
//				System.out.println("expected");
//			}
//			
//			mRequestsWaitingAck--;
		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> arg0) {
			System.out.println("On parsing errors");
		}
    };
    
    public void setColorListener(ColorChangedListener listener) {
    	mColorListener = listener;
    }

	@Override
	protected void doEditSwitchName(Handler handler, int id, String name) {
		LightNameListener listener = new LightNameListener(handler, id, name);
		PHLight light = getLights().get(String.valueOf(id));
		light.setName(name);
		mBridge.updateLight(light, listener);
	}

	@Override
	protected String doEditSwitchNameResponse(ServiceMessage message) {
		return (String)message.data;
	}
	
	protected static class WeakRefWidgetHandler extends Handler {
		private WeakReference<LightService> ref;
		private boolean mAction;

		public WeakRefWidgetHandler(LightService service, boolean action) {
			this.ref = new WeakReference<LightService>(service);
			mAction = action;
		}

		@Override
		public void handleMessage(Message msg) {
			LightService service = ref.get();
			
			ServiceMessage message = (ServiceMessage)msg.obj;
			
			if (message.result != DomoController.ERROR_NONE) {
				return;
			}
			
			switch (msg.what) {
				case SERVICE_CONNECTION:
					Map<String, PHLight> lights = service.getLights();
					ArrayList<String> ids = new ArrayList<String>(lights.keySet());
					for (String id : ids)
						new LightSwitchRequest(id, mAction, service.mRemoteHandler).run(service.mBridge);
					
					service.disconnect();
					
					break;
			}
		}
	}

	@Override
	public int getServiceType() {
		return DomoService.SERVICE_TYPE_PHILIPS_HUE;
	};
}