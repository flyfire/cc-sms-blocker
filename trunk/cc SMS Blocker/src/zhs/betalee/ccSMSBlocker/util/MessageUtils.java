package zhs.betalee.ccSMSBlocker.util;


import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.ui.Main;
import zhs.betalee.ccSMSBlocker.ui.Settings;
import zhs.betalee.ccSMSBlocker.ui.sms.SmsBlockedLog;
import android.R.bool;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class MessageUtils {
	

	public static void updateNotifications(Context mContext,String number,String body){

		if (!Settings.getBoolean(mContext, "enablenotification")) {	
			return;
		}

		boolean led=Settings.getBoolean(mContext, "notifyled");

		if (Build.VERSION.SDK_INT >= 11) {
			//Android 3.0 = 11
			//声明通知（消息）管理器 
			NotificationManager mNotificationManager;
			Intent  mIntent;
			PendingIntent mPendingIntent;
			//声明Notification对象
			Notification  mNotification;

			//初始化NotificationManager对象 
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			mIntent=new Intent("zhs.betalee.ccSMSBlocker.SmsBlockedLog");
			mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			/** 设置 */

			//主要是设置点击通知时显示内容的类 
			mPendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT); //如果转移内容则用m_Intent();
			//构造Notification对象 
			if (Build.VERSION.SDK_INT >= 16){
				mNotification = new Notification.Builder(mContext)
				.setTicker("已拦截:"+body)
				.setContentTitle("已拦截:"+number)
				.setContentText(body)
				.setSmallIcon(R.drawable.ic_stat_name)
				//	         .setLargeIcon(aBitmap)
				.setContentIntent(mPendingIntent)
				.setNumber(readUnreadCountSharedPreferences(mContext))
				.setAutoCancel(true)
				.build();
			}else {
				mNotification = new Notification.Builder(mContext)
				.setTicker("已拦截:"+body)
				.setContentTitle("已拦截:"+number)
				.setContentText(body)
				.setSmallIcon(R.drawable.ic_stat_name)
				//		         .setLargeIcon(aBitmap)
				.setContentIntent(mPendingIntent)
				.setNumber(readUnreadCountSharedPreferences(mContext))
				.setAutoCancel(true)
				.getNotification();
			}
			if (led) {
				mNotification.defaults = Notification.DEFAULT_LIGHTS;
				mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
			}
			mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(R.string.app_name, mNotification); 

		}else  {

			//声明通知（消息）管理器 
			NotificationManager mNotificationManager;
			Intent  mIntent;
			PendingIntent mPendingIntent;
			//声明Notification对象
			Notification  mNotification;

			//初始化NotificationManager对象 
			mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			mIntent=new Intent("zhs.betalee.ccSMSBlocker.SmsBlockedLog");
			mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			/** 设置 */

			//主要是设置点击通知时显示内容的类 
			mPendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT); //如果转移内容则用m_Intent();
			//构造Notification对象 
			mNotification = new Notification(); 
			//设置通知在状态栏显示的图标 
			mNotification.icon = R.drawable.ic_stat_name;
			//当我们点击通知时显示的内容 
			mNotification.tickerText = "已拦截:"+body; 
			//通知时发出默认的LED
			if (led) {
				mNotification.defaults = Notification.DEFAULT_LIGHTS;
				mNotification.flags |= Notification.FLAG_SHOW_LIGHTS;
//				Log.e("LED", "true");
			} 
			mNotification.flags |= Notification.FLAG_AUTO_CANCEL;

			mNotification.number=readUnreadCountSharedPreferences(mContext);
			//设置通知显示的参数 
			mNotification.setLatestEventInfo(mContext, "已拦截:"+number, body, mPendingIntent); 
			//可以理解为执行这个通知 
			mNotificationManager.notify(R.string.app_name, mNotification); 

		}
	}

	public static void sendBlockMessageToMe(Context mContext,String msg){
		try
        {
          Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:ccsmsblocker@gmail.com,abuse@12321.cn"));
          localIntent.putExtra("android.intent.extra.TEXT", "您的举报将造福所有用户!\n 12321.cn网络不良与垃圾信息举报受理中心\n\n"+msg );
          localIntent.putExtra("android.intent.extra.SUBJECT", "[Report]<CC短信拦截>"+mContext.getString(R.string.app_ver) );
          mContext.startActivity(localIntent);
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Toast.makeText(mContext, localActivityNotFoundException.getMessage(), 1).show();
        }
	}
	
	public static String fetchMCC(Context context)
	{
		Context mContext=context;
		context=null;
		String MCCMNC = ((TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
		if (MCCMNC != null){
			if (MCCMNC.startsWith("460")){//大陆
				return "+86";
			}else if (MCCMNC.startsWith("454")){//香港
				return "+852";
			}else if (MCCMNC.startsWith("455")){//澳门
				return "+853";
			}else if (MCCMNC.startsWith("466")){//台湾
				return "+886";
			}
		}
		return "+86";
	}
	
	
	
	public static int  readUnreadCountSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getInt("UnreadNotifi",0);
	}
	public static void  writeUnreadCountSharedPreferences(Context context,int count)
	{
		Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putInt("UnreadNotifi",count);
		editor.commit();
	}
	
	public static String readAppVerSharedPreferences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString("AppVer","0.0.2.2");
	}

	public static void  writeAppVerSharedPreferences(Context context,String appver)
	{
		Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString("AppVer",appver);
		editor.commit();
	}

	public static String readStringSharedPreferences(Context context,String key)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getString(key,null);
	}

	public static void  writeStringSharedPreferences(Context context,String key,String value)
	{
		Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(key,value);
		editor.commit();
	}
	
	
    /**
     * getDefaultSharedPreferences(context).getInt(key,0)
     *
     * @param context 
     * @param key 
     *                 
     */
	public static int readIntSharedPreferences(Context context,String key)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(key,0);
	}

	public static void  writeIntSharedPreferences(Context context,String key,int value)
	{
		Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putInt(key,value);
		editor.commit();
	}
    /**
     * getDefaultSharedPreferences(context).getBoolean(key,false)
     *
     * @param context 
     * @param key 
     *                 
     */
	public static Boolean readBooleanSharedPreferences(Context context,String key)
	{
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key,false);
	}

	public static void  writeBooleanSharedPreferences(Context context,String key,Boolean value)
	{
		Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean(key,value);
		editor.commit();
	}

	
	public static String formatTimeStampString(Context context, long when, boolean fullFormat) {
        Time then = new Time();
        then.set(when);
        Time now = new Time();
        now.setToNow();
        // Basic settings for formatDateTime() we want for all cases.
        int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT |
                           DateUtils.FORMAT_ABBREV_ALL |
                           DateUtils.FORMAT_CAP_AMPM;
        // If the message is from a different year, show the date and year.
        if (then.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (then.yearDay != now.yearDay) {
            // If it is from a different day than today, show only the date.
            format_flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            // Otherwise, if the message is from today, show the time.
            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        // If the caller has asked for full details, make sure to show the date
        // and time no matter what we've determined above (but still make showing
        // the year only happen if it is a different year from today).
        if (fullFormat) {
            format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
        }
        return DateUtils.formatDateTime(context, when, format_flags);
    }

}
