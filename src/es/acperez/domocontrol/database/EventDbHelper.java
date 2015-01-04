package es.acperez.domocontrol.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import es.acperez.domocontrol.systems.base.DomoEvent;
import es.acperez.domocontrol.systems.base.DomoSystem;
import es.acperez.domocontrol.systems.light.LightEvent;
import es.acperez.domocontrol.systems.power.PowerEvent;

public class EventDbHelper extends SqlHelper {

	public static final String SQL_TABLE_EVENTS = "Events";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_TYPE = "TYPE";
	public static final String FIELD_TIMEMILLIS = "timemillis";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_ACTION = "action";
	public static final String FIELD_EXTRA = "extra";
    
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
		values.put(FIELD_TYPE, event.mType);
		values.put(FIELD_TIMEMILLIS, event.getTime().getTimeInMillis());
		values.put(FIELD_TIMESTAMP, event.mTimeStr);
		values.put(FIELD_ACTION, event.mAction ? 1 : 0);
		
		if (event.mType == DomoSystem.TYPE_POWER) {
			PowerEvent evt = (PowerEvent) event;
			values.put(FIELD_EXTRA, evt.mSocket);
		}
		
		return db.insert(SQL_TABLE_EVENTS, null, values);
	}
	
	public ArrayList<DomoEvent> getAllEvents() {
		ArrayList<DomoEvent> events = new ArrayList<DomoEvent>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from " + SQL_TABLE_EVENTS + " order by " + FIELD_TIMEMILLIS + " asc", null);
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            	int type = cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
            	String time = cursor.getString(cursor.getColumnIndex(FIELD_TIMESTAMP));
            	boolean action = cursor.getInt(cursor.getColumnIndex(FIELD_ACTION)) == 1;
            	int extra = cursor.getInt(cursor.getColumnIndex(FIELD_EXTRA));
            	
            	switch (type) {
	            	case DomoSystem.TYPE_POWER:
	            		events.add(new PowerEvent(id, time, action, extra));
	            		break;
	            		
	            	case DomoSystem.TYPE_LIGHT:
	            		events.add(new LightEvent(id, time, action));
	            		break;
            	}

                cursor.moveToNext();
            }
        }
		
		return events;
	}
	
	public ArrayList<LightEvent> getLightEvents() {
		ArrayList<LightEvent> events = new ArrayList<LightEvent>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from " + SQL_TABLE_EVENTS + " where " + FIELD_TYPE + "=" +
									DomoSystem.TYPE_LIGHT + " order by " + FIELD_TIMEMILLIS + " asc", null);
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            	String time = cursor.getString(cursor.getColumnIndex(FIELD_TIMESTAMP));
            	boolean action = cursor.getInt(cursor.getColumnIndex(FIELD_ACTION)) == 1;
            	
	            events.add(new LightEvent(id, time, action));
	            
                cursor.moveToNext();
            }
        }
		
		return events;
	}
	
	public ArrayList<PowerEvent> getPowerEvents() {
		ArrayList<PowerEvent> events = new ArrayList<PowerEvent>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from " + SQL_TABLE_EVENTS + " where " + FIELD_TYPE + "=" +
									DomoSystem.TYPE_POWER + " order by " + FIELD_TIMEMILLIS + " asc", null);
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            	String time = cursor.getString(cursor.getColumnIndex(FIELD_TIMESTAMP));
            	boolean action = cursor.getInt(cursor.getColumnIndex(FIELD_ACTION)) == 1;
            	int extra = cursor.getInt(cursor.getColumnIndex(FIELD_EXTRA));
	            
            	events.add(new PowerEvent(id, time, action, extra));
	            
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
