package es.acperez.domocontrol.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import es.acperez.domocontrol.modules.DomoSwitch;
import es.acperez.domocontrol.modules.actions.events.DomoEvent;

public class EventDbHelper extends SqlHelper {

	public static final String SQL_TABLE_EVENTS = "Events";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_TIMEMILLIS = "timemillis";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_ACTION = "action";
	public static final String FIELD_SWITCH_ID = "switchId";
	public static final String FIELD_SWITCH_NAME = "switchName";
	public static final String FIELD_SERVICE_ID = "serviceId";
    
	public EventDbHelper(Context context) {
		super(context);
	}
	
	public long insertEvent(DomoEvent event) {
		SQLiteDatabase db = getWritableDatabase();
		long id = addEvent(db, event);
		db.close();
		return id;
	}
	
	private long addEvent(SQLiteDatabase db, DomoEvent event) {
		ContentValues values = new ContentValues();
		values.put(FIELD_TIMEMILLIS, event.getTime().getTimeInMillis());
		values.put(FIELD_TIMESTAMP, event.mTimeStr);
		values.put(FIELD_ACTION, event.mAction ? 1 : 0);
		values.put(FIELD_SWITCH_ID, event.mSwitch.id);
		values.put(FIELD_SWITCH_NAME, event.mSwitch.name);
		values.put(FIELD_SERVICE_ID, event.mSwitch.serviceId);
		
		return db.insert(SQL_TABLE_EVENTS, null, values);
	}
	
	public ArrayList<DomoEvent> getEvents() {
		ArrayList<DomoEvent> events = new ArrayList<DomoEvent>();
		
		SQLiteDatabase db = this.getReadableDatabase();		
		Cursor cursor = db.rawQuery("select * from " + SQL_TABLE_EVENTS + " order by " + FIELD_TIMEMILLIS + " asc", null);

		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            	String time = cursor.getString(cursor.getColumnIndex(FIELD_TIMESTAMP));
            	boolean action = cursor.getInt(cursor.getColumnIndex(FIELD_ACTION)) == 1;
            	int switchId = cursor.getInt(cursor.getColumnIndex(FIELD_SWITCH_ID));
            	String switchName = cursor.getString(cursor.getColumnIndex(FIELD_SWITCH_NAME));
            	int serviceId = cursor.getInt(cursor.getColumnIndex(FIELD_SERVICE_ID));
            	
            	events.add(new DomoEvent(id, time, action, new DomoSwitch(switchId, false, switchName, serviceId)));
	            
                cursor.moveToNext();
            }
        }
		
		return events;
	}
	
	public void deleteEvent(long eventId) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(SQL_TABLE_EVENTS, FIELD_ID + " = ? ", new String[] { String.valueOf(eventId) });
		db.close();
	}

}
