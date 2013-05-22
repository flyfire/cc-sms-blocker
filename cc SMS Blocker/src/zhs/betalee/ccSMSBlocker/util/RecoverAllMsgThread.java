package zhs.betalee.ccSMSBlocker.util;

import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;



public class RecoverAllMsgThread extends Thread{

	private Context context;
//	private Cursor mBlockedMsgCursor;
	private DbAdapter mDbAdapter=null;
	private Handler handler;
	private int status;
//	private static Object lock = new Object();
	public RecoverAllMsgThread(Context context,DbAdapter mDbAdapter,int status,Handler handler) {//ππ‘Ï∆˜
		this.context = context;
		this.mDbAdapter=mDbAdapter;
		this.handler=handler;
		this.status=status;
		context=null;
		mDbAdapter=null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		synchronized (lock) {

			Cursor mBlockedMsgCursor=mDbAdapter.fetchBlockedMSGAll();
			int zhe=0;
			if (mBlockedMsgCursor.moveToFirst()) {

				do {
					zhe=mBlockedMsgCursor.getInt(5);
					//Œ¥∂¡ == 0
					//“—∂¡ == 1
					if ((zhe & 1) ==status) {
						String tempAddString=mBlockedMsgCursor.getString(1);
						String tempMSGString=mBlockedMsgCursor.getString(2);
						Long tempTimeString=mBlockedMsgCursor.getLong(3);	
						if (SmsManager.createSms(context, tempAddString, tempMSGString, tempTimeString)) {
							mDbAdapter.deleteOne(DbAdapter.BLOCKED_MESSAGES_DB_TABLE,mBlockedMsgCursor.getLong(0));	
						}else {
							//						ª÷∏¥∂Ã–≈ ß∞‹
							handler.sendEmptyMessage(3);
						}
					}

				} while (mBlockedMsgCursor.moveToNext());
				//			Main.refreshBlockedmsgView();

			}
			handler.sendEmptyMessage(2);
//		}
	}
}
