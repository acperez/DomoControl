package es.acperez.domocontrol.systems.power.controller;

import java.util.ArrayList;

import android.content.Context;
import es.acperez.domocontrol.database.EventDbHelper;
import es.acperez.domocontrol.systems.power.PowerData;
import es.acperez.domocontrol.systems.power.PowerEvent;

public class PowerEventManager {
	private Context mContext;
	private EventDbHelper mDbHelper;
	private PowerData mData;
	private ArrayList<PowerEvent> mEvents;
	
	public PowerEventManager(Context context, PowerData data) {
		mDbHelper = new EventDbHelper(context);
		mEvents = mDbHelper.getPowerEvents();
		
		mContext = context;
		mData = data;
		
		if (mData.mAlarmsEnabled)
			enableAlarms(true);
	}
	
	public ArrayList<PowerEvent> getAllEvents() {
		return mEvents;
	}

	public int addEvent(PowerEvent event) {
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
		if (mData.mAlarmsEnabled) {
			event.setAlarm(mContext);
		}
		
		return index;
	}
	
	public void deleteEvent(PowerEvent event) {
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
	
	public void enableAlarms(boolean enable) {
		if (enable) {
			for (int i = 0; i < mEvents.size(); i++) {
				mEvents.get(i).setAlarm(mContext);
			}
		} else {
			for (int i = 0; i < mEvents.size(); i++) {
				mEvents.get(i).cancelAlarm();
			}
		}
	}
}