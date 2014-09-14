package es.acperez.domocontrol.systems.light.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScenesSqlHelper extends SQLiteOpenHelper {
	private static final String SQL_DB_NAME = "domocontrol";
	private static final String SQL_DB_TABLE = "scenes";
	private static final int SQL_DB_VERSION = 1;
    
	private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_COLORS = "colors";
    
    private static final String SQL_CREATE = 
    										"CREATE TABLE " + SQL_DB_TABLE + " ( " +
    										FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
    										FIELD_NAME + " TEXT, " +
    										FIELD_COLORS + " BLOB)";
    
	public ScenesSqlHelper(Context context) {
		super(context, SQL_DB_NAME, null, SQL_DB_VERSION);
//		context.deleteDatabase(SQL_DB_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE);
		
		int elements = defNames.length;
		ArrayList<Scene> scenes = new ArrayList<Scene>();
		for (int i = 0; i < elements; i++) {
			scenes.add(new Scene(defNames[i], defColours[i]));
		}
		
		insertScenes(db, scenes);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	public void insertScene(Scene scene) {
		SQLiteDatabase db = this.getWritableDatabase();
		addScene(db, scene);
		db.close();
	}
	
	private void insertScenes(SQLiteDatabase db, ArrayList<Scene> scenes) {
		for (Scene scene : scenes) {
			addScene(db, scene);
		}		 
	}
	
	private void addScene(SQLiteDatabase db, Scene scene) {
		ByteArrayOutputStream blob = new ByteArrayOutputStream();
		
		for (int i = 0; i < scene.colors.length; i++) {
			byte[] buffer = intToByteArray(scene.colors[i]);
			blob.write(buffer,0,buffer.length);
		}
		
		ContentValues values = new ContentValues();
		values.put(FIELD_NAME, scene.name);
		values.put(FIELD_COLORS, blob.toByteArray());
		db.insert(SQL_DB_TABLE, null, values);
	}
	
	public ArrayList<Scene> getAllScenes() {
		ArrayList<Scene> scenes = new ArrayList<Scene>();
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + SQL_DB_TABLE, null);
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	int id = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
                String name = cursor.getString(cursor.getColumnIndex(FIELD_NAME));
                ByteArrayInputStream data = new ByteArrayInputStream(cursor.getBlob(cursor.getColumnIndex(FIELD_COLORS)));
                
                int[] colors = new int[data.available() / 4];
                byte[] buffer = new byte[4];
                int index = 0;
                while (data.read(buffer, 0, 4) != -1) {
                	colors[index] = byteArrayToInt(buffer);
                	index++;
                }
                
                scenes.add(new Scene(id, name, colors));
                cursor.moveToNext();
            }
        }
		
		return scenes;
	}
	
	public void deleteScene(Scene scene) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(SQL_DB_TABLE, FIELD_ID + " = ?", new String[] { String.valueOf(scene.id) });
		db.close();
	}
	
	private byte[] intToByteArray(int value) {
	    return new byte[] { (byte)((value >> 24) & 0xFF),
	    					(byte)((value >> 16) & 0xFF),
	    					(byte)((value >> 8) & 0xFF),
	    					(byte)(value & 0xFF) };
	}
	
	private int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	
	
	
	private static final String[] defNames = {"Blue", "Orange", "Red", "Green", "Sea", "Peach", "Fire", "Forest",
		"Blue", "Orange", "Red", "Green", "Sea", "Peach", "Fire", "Forest"};

	private static final int[][] defColours = {{0xFF0000FF}, {0xFFFF9600}, {0xFFFF0000}, {0xFF00FF00},
												{0xFF0030FF, 0xFF009CFF, 0xFF0FF1FF},
												{0xFFFFA800, 0xFFFF9600, 0xFFFFC600},
												{0xFFFF0000, 0xFFFF4545, 0xFFFF1E00},
												{0xFF1B8F00, 0xFF30FF00, 0xFF86FF6A},
												{0xFF0000FF}, {0xFFFF9600}, {0xFFFF0000}, {0xFF00FF00},
												{0xFF0030FF, 0xFF009CFF, 0xFF0FF1FF},
												{0xFFFFA800, 0xFFFF9600, 0xFFFFC600},
												{0xFFFF0000, 0xFFFF4545, 0xFFFF1E00},
												{0xFF1B8F00, 0xFF30FF00, 0xFF86FF6A}};
}
