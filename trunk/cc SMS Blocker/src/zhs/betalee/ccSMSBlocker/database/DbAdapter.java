package zhs.betalee.ccSMSBlocker.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {

	private final Context context;
	private static DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "rule";
	public static final String KEY_TYPE = "type";
	
	public static final String FORMADDRESS = "number";
	public static final String MSGBODY = "msgbody";
	public static final String FORMTIME = "timestamp";
	public static final String BLOCKEDRULE = "blockedrule";
	public static final String STATUS="status";
	
	private static final String DB_NAME = "database";
	
	private static final String BLOCKED_MESSAGES_DB_TABLE = "blockedmessages";
	private static final String DB_TABLE = "rules";
	
	private static final int DB_VERSION = 16;
	
	private static final String DB_SQLSTRING =
		"create table " + DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		KEY_NAME + " text not null, " + 
		KEY_TYPE + " integer);";
	private static final String DB_MESSAGES_SQLSTRING =
		"create table " + BLOCKED_MESSAGES_DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		FORMADDRESS + " text not null, " + MSGBODY + " text not null, " +
		FORMTIME + " integer, " + BLOCKEDRULE + "  text, "+ STATUS +" integer);";
		
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DB_SQLSTRING);
			db.execSQL(DB_MESSAGES_SQLSTRING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop table if exist " + DB_TABLE);
			db.execSQL("drop table if exist " + BLOCKED_MESSAGES_DB_TABLE);
			onCreate(db);
		}
		
	}
	
	public DbAdapter(Context ctx){
		this.context = ctx;
	}
	
	public DbAdapter open() throws SQLException{
		mDbHelper = new DatabaseHelper(context);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDb.close();
		mDbHelper.close();
	}
	
	public long createOne(String name, int count){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_TYPE, count);
		return mDb.insert(DB_TABLE, null, values);
	}
	
	public long createOne(String formaddress,String mdgbody,long formtime,String rule){
		ContentValues values = new ContentValues();
		values.put(FORMADDRESS, formaddress);
		values.put(MSGBODY, mdgbody);
		values.put(FORMTIME, formtime);
		values.put(BLOCKEDRULE, rule);
		values.put(STATUS, 0);
		return mDb.insert(BLOCKED_MESSAGES_DB_TABLE, null, values);
	}
	
	public boolean deleteOne(String tablename,long rowId){
		return mDb.delete(tablename, KEY_ID + "=" + rowId, null)>0;
	}
	public boolean deleteAll(){
		return mDb.delete(DB_TABLE, null, null)>0;
	}
	
	public boolean updateOneId(long rowId, String name, long count){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_TYPE, count);
		return mDb.update(DB_TABLE, values, KEY_ID + "=" + rowId, null)>0;
	}
	
	public boolean updateMSGStatus(long rowId, int status){
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		return mDb.update(BLOCKED_MESSAGES_DB_TABLE, values, KEY_ID + "=" + rowId, null)>0;
	}
	
	public Cursor fetchOneId(long rowId){
		Cursor cursor = 
			mDb.query(true, 
					DB_TABLE, 
					new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, 
					KEY_ID + "=" + rowId, 
					null, null, null, null, null);
		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;
	}
	
	public Cursor fetchRulesAll(){
		return mDb.query(DB_TABLE, 
				new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, 
				null, null, null, null, KEY_TYPE);
	}
	public Cursor fetchBlockedMSGAll(){
		return mDb.query(BLOCKED_MESSAGES_DB_TABLE, 
				new String[] {KEY_ID,FORMADDRESS, MSGBODY, FORMTIME,BLOCKEDRULE,STATUS}, 
				null, null, null, null, KEY_ID + " DESC");
	}
	
	public Cursor fetchAllRulesType(String selection) {
		return mDb.query(DB_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, selection, null, null, null, null);
		
		//		return mDb.query(DB_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, selection, null,
//				null, null, "_id DESC");
	}
	
	
}

