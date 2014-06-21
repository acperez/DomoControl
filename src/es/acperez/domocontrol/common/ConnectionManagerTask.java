package es.acperez.domocontrol.common;

import java.util.ArrayList;

import es.acperez.domocontrol.common.ConnectionManager.TaskListener;

public abstract class ConnectionManagerTask extends Thread {
	
	private final ArrayList<TaskListener> listeners = new ArrayList<TaskListener>();
	
	public final void addListener(final TaskListener listener) {
		listeners.add(listener);
	}
	
	public final void removeListener(final TaskListener listener) {
		listeners.remove(listener);
	}
	
	private final void notifyListeners() {
		for (TaskListener listener : listeners) {
			listener.onTaskCompleted(this);
		}
	}
	
	@Override
	public final void run() {
		try {
			doRun();
		} finally {
			notifyListeners();
		}
	}
	
	public abstract void doRun();
}