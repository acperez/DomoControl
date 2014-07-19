package es.acperez.domocontrol.systems.light.controller;

public class LightRequest {

	public static final int TYPE_CONNECT = 0;
	public static final int TYPE_GET_CONFIG = 1;
	
	public int requestType;
	public int resultStatus;
	
	public LightRequest(int requestType) {
		this.requestType = requestType;
	}

	public static LightRequest createConnectRequest() {
		return new LightRequest(TYPE_CONNECT);
	}
	
	public static LightRequest createGetConfigRequest() {
		return new LightRequest(TYPE_GET_CONFIG);
	}
}
