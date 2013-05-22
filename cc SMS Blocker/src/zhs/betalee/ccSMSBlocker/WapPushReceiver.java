package zhs.betalee.ccSMSBlocker;

import java.util.Iterator;

import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.ui.Settings;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
//import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class WapPushReceiver extends BroadcastReceiver
{
	private static final String LOGTAG = "WapPushReceiverLog";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
//		Log.d(LOGTAG, "onReceive, intent action: " + intent.getAction());
//    	开启Wap Push
    	if (!Settings.getBoolean(context, "enablewappushblocker")) {
    		return;
		} 
		if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED"))
		{
			debugIntent(intent);

			if (intent.getType().equals("application/vnd.wap.sic"))	//application/vnd.wap.slc (text/vnd.wap.sl).
			{
				Log.d(LOGTAG, "mime is application/vnd.wap.sic");
				AsyncTask<Intent, Void, Void> localReceiveWapPushTask = new ReceiveWapPushTask(context);
			    localReceiveWapPushTask.execute(intent);
			    abortBroadcast();
			}
		}
	}
	
	private class ReceiveWapPushTask extends AsyncTask<Intent, Void, Void>
	{
		private Context context;
		
		public ReceiveWapPushTask(Context context)
	    {
	      this.context = context;
	    }
	     
		@Override
		protected Void doInBackground(Intent... paramArrayOfIntent)
		{
			Log.d(LOGTAG, "ReceiveWapPushTask doInBackground");
			Intent intent = paramArrayOfIntent[0];
			byte[] pushData = intent.getByteArrayExtra("data");
		
			Log.d(LOGTAG, "paser push message");
			WapPushWbxmlParser w = null;
			try
			{
				w = new WapPushWbxmlParser(pushData);
			}
			catch (Exception ex)
			{
				Log.d(LOGTAG, ex.toString());
			}
//			Log.d(LOGTAG, "SI: " + w.getSI());
//			Log.d(LOGTAG, "title: " + w.getTitle());
//			Log.d(LOGTAG, "content: " + w.getContent());
			
//			Log.d(LOGTAG, "store wap push message to sms inbox");
//			storeWapPushMessage(context, "WAP PUSH", w.getTitle() + " " + w.getContent());
			blockMessage(context,"WAP PUSH", w.getTitle() + " " + w.getContent());
			
			
//			Log.d(LOGTAG, "update unread number of the mms icon on the desktop and lockscreen");
//			updateUnreadNumber(context);
		      
//			Log.d(LOGTAG, "play delivery report ring tone & vibrate");
//			playDeliveryReportRingToneAndVibrate(context);
		
//			Log.d(LOGTAG, "wake up phone screen");
//			wakeupPhoneScreen(context);
			
			/*
			Log.d(LOGTAG, "run mms app");
	        Intent newIntent = new Intent(Intent.ACTION_MAIN, Uri.parse("content://mms-sms"));
	        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        this.context.startActivity(newIntent);  
			*/
			return null;
		}
	    private void blockMessage(Context mContext, String address,String wapbody) {
			// TODO Auto-generated method stub
	    	
			DbAdapter mDbAdapter = new DbAdapter(mContext);

	        Long blockedcount=mDbAdapter.createOne(address, wapbody,System.currentTimeMillis()," [WapPush SI]");

	        mDbAdapter=null;
	        
//	        int unreadcount =MessageUtils.readUnreadCountSharedPreferences(mContext);
//	        MessageUtils.writeUnreadCountSharedPreferences(mContext, ++unreadcount);
	        MessageUtils.writeStringSharedPreferences(mContext, "blockedcount", blockedcount.toString());
//	        MessageUtils.updateNotifications(mContext,address,wapbody);
	        

		}
		private void updateUnreadNumber(Context context)
		{
			//get unread count
			Cursor smsCursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, "read = 0", null, null);
			int smsUnreadCount = smsCursor.getCount();
			Log.d(LOGTAG, "sms_unread_count: " + smsUnreadCount);
			
			Cursor mmsCursor = context.getContentResolver().query(Uri.parse("content://mms/inbox"), null, "read = 0", null, null);
			int mmsUnreadCount = mmsCursor.getCount();
			Log.d(LOGTAG, "mms_unread_count: " + mmsUnreadCount);

			int unreadCount = smsUnreadCount + mmsUnreadCount;
			Log.d(LOGTAG, "unread_count: " + unreadCount);
			
			Log.d(LOGTAG, "broadcast android.intent.action.APPLICATION_MESSAGE_UPDATE");
			Intent mmsIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
			mmsIntent.putExtra("android.intent.extra.update_application_message", String.valueOf(unreadCount));
			mmsIntent.putExtra("android.intent.extra.update_application_flatten_name", "com.android.mms/.ui.MmsTabActivity");
			context.sendBroadcast(mmsIntent);
			
			Log.d(LOGTAG, "broadcast android.message.RECEIVE_NEW_MESSAGE");
			Intent lockscreenIntent = new Intent("android.message.RECEIVE_NEW_MESSAGE");
		    context.sendBroadcast(lockscreenIntent);

		    //android.provider.Telephony.Sms.Intents.getMessagesFromIntent(localIntent);
		}
		
		private void storeWapPushMessage(Context context, String address, String content)
	    {
	        ContentValues pushMessageCV = new ContentValues();
	        pushMessageCV.put("address", address);
	        pushMessageCV.put("person", "");
	        pushMessageCV.put("protocol", "0");
	        pushMessageCV.put("read", "0");	//0 表示未读 1表示已读
	        pushMessageCV.put("status", "-1");
	        pushMessageCV.put("body", content);
	    
	        context.getContentResolver().insert(Uri.parse("content://sms/inbox"), pushMessageCV);
	    }
		
//		private void wakeupPhoneScreen(Context context)
//		{
//			PowerManager pm = (PowerManager)context.getSystemService("power");
//			pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wap Push Receiver").acquire(5000L);
//		}
		
		private void playDeliveryReportRingToneAndVibrate(Context context)
		{
			SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					  
			String notificationPath = localSharedPreferences.getString("pref_key_delivery_ringtone", "content://settings/system/notification_sound");
		    if (TextUtils.isEmpty(notificationPath))
		    {
		    	return;
		    }

		    Uri notificationUri = Uri.parse(notificationPath);
		    Ringtone notificationRingtone = RingtoneManager.getRingtone(context, notificationUri);
		    if (notificationRingtone == null)
		    {
		    	return;
		    }
		    
		    notificationRingtone.setStreamType(AudioManager.STREAM_NOTIFICATION);
		    notificationRingtone.play();
		    
		    if (((AudioManager)context.getSystemService("audio")).shouldVibrate(android.media.AudioManager.VIBRATE_TYPE_NOTIFICATION))
			{
		    	Vibrator vibrator = (Vibrator)context.getSystemService("vibrator");
		    	vibrator.vibrate(1000L);
			}
		}
	}
	
	private void debugIntent(Intent intent)
	{
		//wap push intent extra [pduType, data, header, transactionId]
		Log.d(LOGTAG, "intent type: " + intent.getType() + ", extra keys: " + intent.getExtras().keySet().toString());

		Iterator<String> iterator = intent.getExtras().keySet().iterator();
		while (iterator.hasNext())
		{
			String key = (String)iterator.next();
			Object o = intent.getExtras().get(key);
			Log.d(LOGTAG, key + "(" + o.getClass().getSimpleName() +"): " + o.toString());	
		}
		
		byte[] data = intent.getByteArrayExtra("header");
		String buf = byteArray2String(data);
		Log.d(LOGTAG, "header(" + buf.length() + ") " + buf);						

		data = intent.getByteArrayExtra("data");
		buf = byteArray2String(data);
		Log.d(LOGTAG, "data(" + buf.length() + ") " + buf);						

	}
	
	private String byteArray2String(byte[] data)
	{
		String buf = "";
		for (byte by: data)
		{
			buf = buf + by + " ";
		}
		
		return buf;
	}
}
