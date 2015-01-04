package es.acperez.domocontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class SqlHelper extends SQLiteOpenHelper {
	private static final String SQL_DB_NAME = "domocontrol";
	private static final int SQL_DB_VERSION = 1;
    
    private static final String CREATE_TABLE_LIGHT_SCENES = 
    								"CREATE TABLE " + LightDbHelper.SQL_TABLE_LIGHT_SCENES + " ( " +
    								LightDbHelper.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    								LightDbHelper.FIELD_NAME + " TEXT, " +
    								LightDbHelper.FIELD_COLORS + " BLOB)";

    private static final String CREATE_TABLE_EVENTS = 
									"CREATE TABLE " + EventDbHelper.SQL_TABLE_EVENTS + " ( " +
									EventDbHelper.FIELD_TYPE + " INTEGER, " +
									EventDbHelper.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
									EventDbHelper.FIELD_TIMEMILLIS + " INTEGER, " +
									EventDbHelper.FIELD_TIMESTAMP + " TEXT, " +
									EventDbHelper.FIELD_ACTION + " INTEGER, " +
									EventDbHelper.FIELD_EXTRA + " INTEGER)";
    
	public SqlHelper(Context context) {
		super(context, SQL_DB_NAME, null, SQL_DB_VERSION);
//		context.deleteDatabase(SQL_DB_NAME);
	}

	@Override
	public final void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LIGHT_SCENES);
		db.execSQL(CREATE_TABLE_EVENTS);
		
		LightDbHelper.initDb(db);
	}

	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}