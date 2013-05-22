package zhs.betalee.ccSMSBlocker;

import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.Time;

public class CCBlockerService extends Service{
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"; 
	private static final String  MMS_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";
	private static final String  PHONE_RECEIVED_ACTION = "android.intent.action.PHONE_STATE";
	private static final String  WAP_PUSH_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";
	private SmsReceiver smsReceiver=null;
	private MmsReceiver mmsReceiver=null;
	private WapPushReceiver wapReceiver=null;;
//	private PhoneReceiver phoneReceiver=null;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {

		super.onCreate();
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
/*//		SharedPreferences  mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		if (mSharedPreferences.getBoolean("enablephoneservice", false)&&mSharedPreferences.getBoolean("enablephoneblocker", true)) {
//			phoneReceiver=new PhoneReceiver();
//			IntentFilter intentFilter = new IntentFilter(PHONE_RECEIVED_ACTION);  
//			intentFilter.setPriority(Integer.MAX_VALUE);  
//			registerReceiver(phoneReceiver, intentFilter);  
//		}else {
//			if (phoneReceiver!=null) {
//				unregisterReceiver(phoneReceiver);
//			}
//		}*/
		
		
		SharedPreferences  mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if (mSharedPreferences.getBoolean("enableservice", false)&&mSharedPreferences.getBoolean("enablesmsblocker", true)) {
			
			smsReceiver=new SmsReceiver();
			
			IntentFilter intentFilter = new IntentFilter(SMS_RECEIVED_ACTION);  
			intentFilter.setPriority(Integer.MAX_VALUE);  
			registerReceiver(smsReceiver, intentFilter);  
		}
		if (mSharedPreferences.getBoolean("enableservice", false)&&mSharedPreferences.getBoolean("enablemmsblocker", false)) {
			
			mmsReceiver=new MmsReceiver();

			IntentFilter intentFilter = new IntentFilter(MMS_RECEIVED_ACTION);  
			intentFilter.setPriority(Integer.MAX_VALUE);  
			registerReceiver(mmsReceiver, intentFilter);  
		}
		if (mSharedPreferences.getBoolean("enableservice", false)&&mSharedPreferences.getBoolean("enablewappushblocker", true)) {
			
			wapReceiver=new WapPushReceiver();
			
			IntentFilter intentFilter = new IntentFilter(WAP_PUSH_RECEIVED_ACTION);  
			intentFilter.setPriority(Integer.MAX_VALUE);  
			registerReceiver(wapReceiver, intentFilter);  
		}
		Time now = new Time();
		now.setToNow();
		MessageUtils.updateNotifications(getApplicationContext(), "Service", "服务启动时间: "+now.hour+":"+now.minute);
		
//		Notification notification = new Notification(R.drawable.ic_stat_name,"服务已启动",System.currentTimeMillis());
		Notification notification = new Notification();
		Intent notificationIntent = new Intent("zhs.betalee.ccSMSBlocker.SmsBlockedLog");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "CC SMS Blocker",
				"Service is running...", pendingIntent);
		/*使用 startForeground ，如果 id 为 0 ，那么 notification 将不会显示。*/
		startForeground(R.string.app_ver, notification);
//		return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			unregisterReceiver(smsReceiver);
			unregisterReceiver(mmsReceiver);
			unregisterReceiver(wapReceiver);
			stopForeground(false);
//			unregisterReceiver(phoneReceiver);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		super.onDestroy();
	}
}
