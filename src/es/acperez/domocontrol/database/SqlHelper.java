package es.acperez.domocontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import es.acperez.domocontrol.systems.light.controller.LightDbHelper;
import es.acperez.domocontrol.systems.power.controller.PowerDbHelper;

public abstract class SqlHelper extends SQLiteOpenHelper {
	private static final String SQL_DB_NAME = "domocontrol";
	private static final int SQL_DB_VERSION = 1;
    
    private static final String CREATE_TABLE_LIGHT_SCENES = 
    								"CREATE TABLE " + LightDbHelper.SQL_TABLE_LIGHT_SCENES + " ( " +
    								LightDbHelper.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    								LightDbHelper.FIELD_NAME + " TEXT, " +
    								LightDbHelper.FIELD_COLORS + " BLOB)";
    
    private static final String CREATE_TABLE_POWER_EVENTS = 
									"CREATE TABLE " + PowerDbHelper.SQL_TABLE_POWER_EVENTS + " ( " +
									PowerDbHelper.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
									PowerDbHelper.FIELD_TIMEMILLIS + " INTEGER, " +
									PowerDbHelper.FIELD_TIMESTAMP + " TEXT, " +
									PowerDbHelper.FIELD_SOCKET + " INTEGER, " +
									PowerDbHelper.FIELD_ACTION + " INTEGER)";
    
	public SqlHelper(Context context) {
		super(context, SQL_DB_NAME, null, SQL_DB_VERSION);
//		context.deleteDatabase(SQL_DB_NAME);
	}

	@Override
	public final void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LIGHT_SCENES);
		db.execSQL(CREATE_TABLE_POWER_EVENTS);
		
		onDbCreated(db);
	}

	protected abstract void onDbCreated(SQLiteDatabase db);

	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}