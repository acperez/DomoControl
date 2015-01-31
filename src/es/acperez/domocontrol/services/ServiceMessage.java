package es.acperez.domocontrol.services;

public class ServiceMessage {
	public int result;
	public Object data;
	public int id;
	
	public ServiceMessage(int result, int id, Object data) {
		this.result = result;
		this.data = data;
		this.id = id;
	}
}
