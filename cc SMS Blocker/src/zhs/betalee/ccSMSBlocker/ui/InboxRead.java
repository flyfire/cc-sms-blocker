package zhs.betalee.ccSMSBlocker.ui;


import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class InboxRead extends ListActivity{

	private ListView listView;
	protected Cursor cursor;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		listView = getListView();
		cursor=getSms();
		SimpleCursorAdapter simpleCursorAdapter=null;
		if (Build.VERSION.SDK_INT >= 11){
			simpleCursorAdapter=new SimpleCursorAdapter(
					listView.getContext(), android.R.layout.simple_list_item_2, cursor,
					new String[] { "address", "body"}, new int[] { android.R.id.text1, android.R.id.text2},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		}else {
			simpleCursorAdapter=new SimpleCursorAdapter(
					listView.getContext(), android.R.layout.simple_list_item_2, cursor,
					new String[] { "address", "body"}, new int[] { android.R.id.text1, android.R.id.text2});
		}
		listView.setAdapter(simpleCursorAdapter);
	}
	
	
private Cursor getSms(){
	final String SMS_URI_ALL = "content://sms/";  
	final String SMS_URI_INBOX = "content://sms/inbox";  
	final String SMS_URI_SEND = "content://sms/sent";  
	final String SMS_URI_DRAFT = "content://sms/draft";  
	final String SMS_URI_OUTBOX = "content://sms/outbox";  
	final String SMS_URI_FAILED = "content://sms/failed";  
	final String SMS_URI_QUEUED = "content://sms/queued";  
	// long weektime=System.currentTimeMillis()-1814400000;
	Uri uri = Uri.parse(SMS_URI_INBOX);  
//	String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
	String[] projection = new String[] { "_id", "address", "body", "date" }; 
//	Cursor cur = getContentResolver().query(uri, projection, "person is null and date >'"+weektime+"'", null, "date desc");
	Cursor cur = getContentResolver().query(uri, projection, "person is null", null, "date desc");
	// 获取手机内部短信
	return cur;
}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		cursor.close();
		super.onDestroy();
		
	}


}
