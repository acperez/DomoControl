package es.acperez.domocontrol.common.connectionManager;

import java.util.ArrayList;

public class ConnectionManager {
    
    public static final int MAX_CONNECTIONS = 1;

    private ArrayList<Thread> active = new ArrayList<Thread>();
    private ArrayList<Thread> queue = new ArrayList<Thread>();
    
    private static ConnectionManager instance;
    
    public interface TaskListener {
        void onTaskCompleted(final Thread thread);
    }

    private ConnectionManager() {
    	
    }
    
    public static ConnectionManager getInstance() {
         if (instance == null)
              instance = new ConnectionManager();
         return instance;
    }
    
    public void push(ConnectionManagerTask task) {
    	queue.add(task);
    	if (active.size() < MAX_CONNECTIONS)
    		startNext();
    }

    private void startNext() {
         if (!queue.isEmpty()) {
              Thread next = queue.get(0);
              queue.remove(0);
              active.add(next);

              next.start();
         }
    }

    public void didComplete(Thread task) {
        active.remove(task);
        startNext();
	}
}