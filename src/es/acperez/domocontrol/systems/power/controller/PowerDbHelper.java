package es.acperez.domocontrol.systems.power.controller;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import es.acperez.domocontrol.database.SqlHelper;
import es.acperez.domocontrol.systems.power.PowerEvent;

public class PowerDbHelper extends SqlHelper {
	public static final String SQL_TABLE_POWER_EVENTS = "PowerEvents";
	
	public static final String FIELD_ID = "id";
	public static final String FIELD_TIMEMILLIS = "timemillis";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_SOCKET = "socket";
	public static final String FIELD_ACTION = "action";
	
	public PowerDbHelper(Context context) {
		super(context);
	}

	@Override
	protected void onDbCreated(SQLiteDatabase db) {
	}
	
	public long insertEvent(PowerEvent event) {
		SQLiteDatabase db = getWritableDatabase();
		long id = addEvent(db, event);
		db.close();
		return id;
	}
	
	private long addEvent(SQLiteDatabase db, PowerEvent event) {
		ContentValues values = new ContentValues();
		values.put(FIELD_TIMEMILLIS, event.getTime().getTimeInMillis());
		values.put(FIELD_TIMESTAMP, event.mTimeStr);
		values.put(FIELD_SOCKET, event.mSocket);
		values.put(FIELD_ACTION, event.mAction ? 1 : 0);
		return db.insert(SQL_TABLE_POWER_EVENTS, null, values);
	}
	
	public ArrayList<PowerEvent> getAllEvents() {
		ArrayList<PowerEvent> events = new ArrayList<PowerEvent>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery("select * from " + SQL_TABLE_POWER_EVENTS + " order by " + FIELD_TIMEMILLIS + " asc", null);
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
            	String time = cursor.getString(cursor.getColumnIndex(FIELD_TIMESTAMP));
            	int socket = cursor.getInt(cursor.getColumnIndex(FIELD_SOCKET));
            	boolean action = cursor.getInt(cursor.getColumnIndex(FIELD_ACTION)) == 1;
                 
                events.add(new PowerEvent(id, time, action, socket));
                cursor.moveToNext();
            }
        }
		
		return events;
	}
	
	public void deleteEvent(PowerEvent event) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(SQL_TABLE_POWER_EVENTS, FIELD_ID + " = ?", new String[] { String.valueOf(event.getId()) });
		db.close();
	}
}