package zhs.betalee.ccSMSBlocker.util;

import zhs.betalee.ccSMSBlocker.ui.Main;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class SmsManager {
	public static boolean createSms(Context mContext, String address, String msg,long timestamp)
	{
		//	    boolean bool = false;
		//	    int i = findMaxSmsID(mContext);
		Uri localUri = Uri.parse("content://sms");
		ContentValues localContentValues = new ContentValues();
		//	    localContentValues.put("_id", i + 1);
		localContentValues.put("address", address);
		localContentValues.put("read", 1);
		localContentValues.put("status", -1);
		localContentValues.put("body", msg);
		localContentValues.put("date", timestamp);
		localContentValues.put("type", 1);
		if (mContext.getContentResolver().insert(localUri, localContentValues) == null)
		{
//			Log.d("ccSB", "Failed to create SMS: " + address + ", " + msg.substring(0, 20));
			return false;
		}
		else {
			Toast.makeText(mContext,
					"已恢复短信到收件箱",Toast.LENGTH_LONG).show();
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
