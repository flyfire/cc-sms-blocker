package zhs.betalee.ccSMSBlocker.database;


import zhs.betalee.ccSMSBlocker.database.DbAdapter.DatabaseHelper;
import zhs.betalee.ccSMSBlocker.util.JsonParse;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

public class UpdataVerDatabase extends Thread{
	
	private Context context;
	private SQLiteDatabase mDb;
	private Handler handler=null;
//	private DatabaseHelper mDbHelper;
	


	public UpdataVerDatabase(Context context,DbAdapter mDbAdapter,Handler handler) {//构造器
		this.context = context;
		this.mDb=mDbAdapter.getmDb();
		this.handler=handler;

		context=null;
//		mDbAdapter=null;
	}
		
	public UpdataVerDatabase(Context context,DbAdapter mDbAdapter) {//构造器
		this.context = context;
		this.mDb=mDbAdapter.getmDb();;
		context=null;
//		mDbAdapter=null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		//黑关键词 
//		mDb.beginTransaction(); 
		try {
//			mDbHelper = new DatabaseHelper(context);
//			mDb = mDbHelper.getWritableDatabase();
			
			Thread.sleep(300);
			mDb.beginTransaction(); 
			final String[] jsonKeyWord=JsonParse.stringBuilderToArray(JsonParse.jsonToStringBuilder(context,zhs.betalee.ccSMSBlocker.R.raw.black_keyword_list,true),"WORD");
			
			
			int size=jsonKeyWord.length;
			
			for (int i=0;i<size;i++) {
//				Cursor c = mDb.rawQuery("SELECT * FROM rules WHERE rule='"+ jsonKeyWord[i] +"' and type='"+Constants.IN_BLOCKED_KEYWORD+"'", null);
//				if (c.getCount() > 0) {
//					continue;
//				}
				ContentValues values = new ContentValues();
				values.put("rule", jsonKeyWord[i]);
				values.put( "type", Constants.IN_BLOCKED_KEYWORD);
//				values.put(KEY_ENABLED, "1");
				mDb.insert("rules", null, values);
			}
			
		  //白号码
			final String[] jsonWhitePhoneNumber=JsonParse.stringBuilderToArray(JsonParse.jsonToStringBuilder(context,zhs.betalee.ccSMSBlocker.R.raw.white_num_list,false),null);
  		
			size=jsonWhitePhoneNumber.length;
			for (int i=0;i<size;i++) {
				Cursor c = mDb.rawQuery("SELECT * FROM rules WHERE rule='"+ jsonWhitePhoneNumber[i] +"' and type='"+Constants.IN_TRUSTED_NUMBER+"'", null);
				if (c.getCount() > 0) {
					continue;
				}
				ContentValues values = new ContentValues();
				values.put("rule", jsonWhitePhoneNumber[i]);
				values.put( "type", Constants.IN_TRUSTED_NUMBER);
//				values.put(KEY_ENABLED, "1");
				mDb.insert("rules", null, values);
			}
						
			ContentValues values = new ContentValues();
			values.put("rule", ".*(http://|www|wap)[\\./#$%+-_?=&a-z\\d]+.*");
			values.put( "type", Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP);
			mDb.insert("rules", null, values);
				
			
			//Here数据库操作
			mDb.setTransactionSuccessful(); //别忘了这句 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			mDb.endTransaction(); //Commit
			e.printStackTrace();
		}finally{
			mDb.endTransaction(); //Commit
		}
		
		
		if (handler!=null) {
			handler.sendEmptyMessage(4);
		}


		super.run();
	}
	
	

	
	
	

	
	
}
