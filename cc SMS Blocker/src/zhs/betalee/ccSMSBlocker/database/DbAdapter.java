package zhs.betalee.ccSMSBlocker.database;


import zhs.betalee.ccSMSBlocker.R.string;
import zhs.betalee.ccSMSBlocker.util.JsonParse;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DbAdapter {

	private Context context=null;
	private DatabaseHelper mDbHelper=null;
	private SQLiteDatabase mDb=null;
	
	public static final String KEY_ID = "_id";
	public static final String KEY_NAME = "rule";
	public static final String KEY_TYPE = "type";
//	public static final String KEY_ENABLED = "isenabled";
	
	public static final String FORMADDRESS = "number";
	public static final String MSGBODY = "msgbody";
	public static final String FORMTIME = "timestamp";
	public static final String BLOCKEDRULE = "blockedrule";
	public static final String STATUS="status";
	
	public static final String ALLOWEDRULE = "allowedrule";
	
	private static final String DB_NAME = "database";
	
	public static final String BLOCKED_MESSAGES_DB_TABLE = "blockedmessages";
	public static final String ALLOW_MESSAGES_DB_TABLE = "allowedmessages";
	public static final String DB_TABLE = "rules";
	public static final String BLOCKED_PHONE_DB_TABLE = "blockedphone";
//	public static final String PHONE_DB_TABLE = "phonerules";
	
	private static final int DB_VERSION = 21;
	
	private static final String DB_SQLSTRING =
		"create table " + DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		KEY_NAME + " text not null, " + 
		KEY_TYPE +" integer);";
	private static final String DB_MESSAGES_SQLSTRING =
		"create table " + BLOCKED_MESSAGES_DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		FORMADDRESS + " text not null, " + MSGBODY + " text not null, " +
		FORMTIME + " integer, " + BLOCKEDRULE + "  text, "+ STATUS +" integer);";
		
//	private static final String PHONE_DB_SQLSTRING =
//		"create table " + PHONE_DB_TABLE + "(" + 
//		KEY_ID + " integer primary key autoincrement, " + 
//		KEY_NAME + " text not null, " + 
//		KEY_TYPE +" integer);";
	
	private static final String DB_BLOCKED_PHONE_SQLSTRING =
		"create table " + BLOCKED_PHONE_DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		FORMADDRESS + " text not null, " + 
		FORMTIME + " integer, " + BLOCKEDRULE + "  text, "+ STATUS +" integer);";
	
	private static final String DB_ALLOW_MESSAGES_SQLSTRING =
		"create table " + ALLOW_MESSAGES_DB_TABLE + "(" + 
		KEY_ID + " integer primary key autoincrement, " + 
		FORMADDRESS + " text not null, " + 
		FORMTIME + " integer, " + ALLOWEDRULE + "  text, "+ STATUS +" integer);";
	
	
	public static class DatabaseHelper extends SQLiteOpenHelper{
		
		private static SQLiteOpenHelper instance;
		
	    public static synchronized SQLiteOpenHelper getHelper(Context context)
	    {
	        if (instance == null)
	            instance = new DatabaseHelper(context);

	        return instance;
	    }
	    
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DB_SQLSTRING);
			db.execSQL(DB_MESSAGES_SQLSTRING);
//			db.execSQL(PHONE_DB_SQLSTRING);
//			db.execSQL(DB_BLOCKED_PHONE_SQLSTRING);
			db.execSQL(DB_ALLOW_MESSAGES_SQLSTRING);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//			db.execSQL("drop table if exist " + DB_TABLE);
			//			db.execSQL("drop table if exist " + BLOCKED_MESSAGES_DB_TABLE);
			//			onCreate(db);

			if (oldVersion==16) {
				/////////0.0.3.0/////////ver 16 to 17  彩信
				String sql = "Update rules set type = 0 where type = 2";
				String sql2 = "Update rules set type = 2 where type = 7";
				try {
					db.execSQL(sql);
					db.execSQL(sql2);
				} catch (SQLException e) {
					System.out.println("onUpgrade Error!");
				}
				/////////0.0.3.0/////////ver 16 to 17
			}
			if (oldVersion<=17) {
				/////////0.0.3.3/////////ver 17 to 18 内置黑白名单
				String sql = "Update rules set type = 7 where type = 11";
				try {
					db.execSQL(sql);
				} catch (SQLException e) {
					System.out.println("onUpgrade Error!");
				}
			}
			if (oldVersion<=18) {
				/////////0.0.3.5/////////ver 18 to 19 彩信2-0
				String sql = "Update rules set type = 0 where type = 2";
				try {
					db.execSQL(sql);
				} catch (SQLException e) {
					System.out.println("onUpgrade Error!");
				}
			}
			if (oldVersion<=19) {
				/////////0.0.3.6/////////ver 19 to 20 
				try {
					db.execSQL(DB_BLOCKED_PHONE_SQLSTRING);
				} catch (SQLException e) {
					System.out.println("onUpgrade Error!");
				}
			}
			if (oldVersion<=20) {
				/////////0.0.3.7/////////ver 20 to 21 
				try {
					db.execSQL(DB_ALLOW_MESSAGES_SQLSTRING);
				} catch (SQLException e) {
					System.out.println("onUpgrade Error!");
				}
			}
		}

	}
	
	/*public DbAdapter(Context ctx){
		this.context = ctx;
//		ctx=null;
	}*/
	
	public DbAdapter(Context context) {
		mDb = DatabaseHelper.getHelper(context).getReadableDatabase();
	}

	public DbAdapter(SQLiteDatabase value) {
		mDb = value;
	}
	/**
	 * @return the mDb
	 */
	public SQLiteDatabase getmDb() {
		return mDb;
	}
	
/*	public DbAdapter open() throws SQLException{
		if (mDbHelper==null) {
			mDbHelper = new DatabaseHelper(context);
		}
		mDb = mDbHelper.getReadableDatabase();
		return this;
	}
	
	public void close(){
		mDb.close();
		mDbHelper.close();
	}*/
	
	public long createOne(String name, int count){
		Cursor c = mDb.rawQuery("SELECT * FROM rules WHERE rule='"+ name +"' and type='"+count+"'", null);
		if (c.getCount() > 0) {
			return -2;
		}
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_TYPE, count);
//		values.put(KEY_ENABLED, "1");
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
	
	public long createOne(String formaddress,long formtime,String rule){
		ContentValues values = new ContentValues();
		values.put(FORMADDRESS, formaddress);
		values.put(FORMTIME, formtime);
		values.put(BLOCKEDRULE, rule);
		values.put(STATUS, 0);
		return mDb.insert(BLOCKED_PHONE_DB_TABLE, null, values);
	}
	
	public long createAllowOne(String formaddress,long formtime,String rule){
		ContentValues values = new ContentValues();
		values.put(FORMADDRESS, formaddress);
		values.put(FORMTIME, formtime);
		values.put(ALLOWEDRULE, rule);
		values.put(STATUS, 0);
		return mDb.insert(ALLOW_MESSAGES_DB_TABLE, null, values);
	}
	
	public boolean deleteOne(String tablename,long rowId){
		return mDb.delete(tablename, KEY_ID + "=" + rowId, null)>0;
	}
	
	public boolean deleteAll(String table){
		return mDb.delete(table, null, null)>0;
	}
	public boolean deleteAll(String table,String selection){
		return mDb.delete(table, selection, null)>0;
	}
	public boolean updateOneId(long rowId, String name, long count){
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_TYPE, count);
//		values.put(KEY_ENABLED, isEnabled);
		return mDb.update(DB_TABLE, values, KEY_ID + "=" + rowId, null)>0;
	}
	
	public boolean updateMSGStatus(long rowId, int status){
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		return mDb.update(BLOCKED_MESSAGES_DB_TABLE, values, KEY_ID + "=" + rowId, null)>0;
	}
	public boolean updateMSGStatus(int status, String selection){
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		return mDb.update(BLOCKED_MESSAGES_DB_TABLE, values, selection, null)>0;
	}
	
	public boolean updateStatus(String table,long rowId, int status){
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		return mDb.update(table, values, KEY_ID + "=" + rowId, null)>0;
	}
	
	public boolean updatePhoneStatus(int status, String selection){
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		return mDb.update(BLOCKED_PHONE_DB_TABLE, values, selection, null)>0;
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
	public Cursor fetchBlockedPhoneAll(){
		return mDb.query(BLOCKED_PHONE_DB_TABLE, 
				new String[] {KEY_ID,FORMADDRESS, FORMTIME,BLOCKEDRULE,STATUS}, 
				null, null, null, null, KEY_ID + " DESC");
	}
	
	public Cursor fetchAllowMsgAll(){
		return mDb.query(ALLOW_MESSAGES_DB_TABLE, 
				new String[] {KEY_ID,FORMADDRESS, FORMTIME,ALLOWEDRULE,STATUS}, 
				null, null, null, null, KEY_ID + " DESC");
	}
	
	public Cursor fetchAllRulesType(String selection) {
		return mDb.query(DB_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, selection, null, null, null, KEY_TYPE);
		
		//		return mDb.query(DB_TABLE, new String[] {KEY_ID, KEY_NAME, KEY_TYPE}, selection, null,
//				null, null, "_id DESC");
	}

	public Cursor fetchAllRulesTypeRawQuery(String where) {
		return mDb.rawQuery("SELECT * FROM rules WHERE "+ where, null);
	}
	
	/*public Cursor fuzzySearch(String key,int type) {
		return mDb.rawQuery("select * from rules where rule like '%"+key+"%' and type = "+type,null);
	}*/
	public Cursor fuzzySearch(String key) {
		return mDb.rawQuery("select * from rules where rule like '%"+key+"%'",null);
	}
}

