package es.acperez.domocontrol.modules.actions.events;

import java.util.ArrayList;

import es.acperez.domocontrol.database.EventDbHelper;
import android.content.Context;

public class EventManager {
	private Context mContext;
	private EventDbHelper mDbHelper;
	private ArrayList<DomoEvent> mEvents;
	private boolean mAlarmsEnabled = false;
	
	public EventManager(Context context) {
		mDbHelper = new EventDbHelper(context);
		mEvents = mDbHelper.getEvents();
		
		mContext = context;
	}
	
	public ArrayList<DomoEvent> getAllEvents() {
		return mEvents;
	}

	public int addEvent(DomoEvent event) {
		long id = mDbHelper.insertEvent(event);
		event.setId(id);
		
		long timestamp = event.getTime().getTimeInMillis();
		
		int index = 0;
		for (int i = 0; i < mEvents.size(); i++) {
			if (timestamp < mEvents.get(i).getTime().getTimeInMillis())
				break;
			
			index++;
		}
		
		mEvents.add(index, event);
		if (mAlarmsEnabled) {
			event.setAlarm(mContext);
		}
		
		return index;
	}
	
	public void deleteEvent(DomoEvent event) {
		mDbHelper.deleteEvent(event.getId());
		
		long id = event.getId();
		int index = 0;
		for (int i = 0; i < mEvents.size(); i++) {
			if (id == mEvents.get(i).getId())
				break;
			
			index++;
		}
		
		event.cancelAlarm();
		mEvents.remove(index);
	}
	
	public void setAlarmsStatus(boolean enable) {
		if (enable) {
			for (int i = 0; i < mEvents.size(); i++) {
				mEvents.get(i).setAlarm(mContext);
			}
		} else {
			for (int i = 0; i < mEvents.size(); i++) {
				mEvents.get(i).cancelAlarm();
			}
		}
		
		mAlarmsEnabled = enable;
	}
}