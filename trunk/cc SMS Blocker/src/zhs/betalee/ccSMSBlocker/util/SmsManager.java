package zhs.betalee.ccSMSBlocker.util;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class SmsManager {
	public synchronized static boolean createSms(Context mContext, String address, String body,Long date)
	{
		final Uri CONTENT_URI =Uri.parse("content://sms/inbox");
		final String ADDRESS = "address";
		final String DATE = "date";
		final String READ = "read";
		final String BODY = "body";
		final String STATUS = "status";
		final int STATUS_NONE = -1;
		
        ContentValues values = new ContentValues(5);

        values.put(ADDRESS, address);
        if (date != null) {
            values.put(DATE, date);
        }
        values.put(READ, 0);
//        values.put(SUBJECT, subject);
        values.put(BODY, body);
        values.put(STATUS, STATUS_NONE);
        
//        if (threadId != -1L) {
//            values.put(THREAD_ID, threadId);
//        }
//        return mContext.getContentResolver().insert(CONTENT_URI, values);
		
		
		
		//	    boolean bool = false;
		//	    int i = findMaxSmsID(mContext);
//		Uri localUri = Uri.parse("content://sms");
//		ContentValues localContentValues = new ContentValues();
//		//	    localContentValues.put("_id", i + 1);
//		localContentValues.put("address", address);
//		localContentValues.put("read", 1);
//		localContentValues.put("status", -1);
//		localContentValues.put("body", body);
//		localContentValues.put("date", date);
//		localContentValues.put("type", 1);
		if (mContext.getContentResolver().insert(CONTENT_URI, values) == null)
		{
//			Log.d("ccSB", "Failed to create SMS: " + address + ", " + msg.substring(0, 20));
			return false;
		}else {
			
//			Toast.makeText(mContext,
//					"已恢复短信到收件箱",Toast.LENGTH_SHORT).show();
			return true;
		}
	}
	
	private static int findMaxSmsID(Context mContext)
	  {
	    Uri localUri = Uri.parse("content://sms");
	    String[] arrayOfString = { "_id" };
	    Cursor localCursor = mContext.getContentResolver().query(localUri, arrayOfString, null, null, "date desc");
	    int i = 0;
	    while (true)
	    {
	      if (!localCursor.moveToNext())
	        return i;
	      int j = localCursor.getInt(localCursor.getColumnIndex("_id"));
	      if (i < j)
	        i = j;
	    }
	  }
	
}
