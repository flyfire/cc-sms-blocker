package zhs.betalee.ccSMSBlocker.util;


import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.ui.Main;
import zhs.betalee.ccSMSBlocker.ui.Settings;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.widget.Toast;

public class MessageUtils {
	
	public static void updateNotifications(Context context,String body){

		if (!Settings.getBoolean(context, "enablenotification")) {	
			return;
		}

		//声明通知（消息）管理器 
		NotificationManager mNotificationManager;
		Intent  mIntent;
		PendingIntent mPendingIntent;
		//声明Notification对象
		Notification  mNotification;

		//初始化NotificationManager对象 
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mIntent=new Intent(context, Main.class);
		/** 设置 */

		//点击通知时转移内容 
		//Intent intent = new Intent(this, this.getClass());
		//intent.addCategory(WINDOW_SERVICE);
		//主要是设置点击通知时显示内容的类 
		mPendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0); //如果转移内容则用m_Intent();
		//构造Notification对象 
		mNotification = new Notification(); 
		//设置通知在状态栏显示的图标 
		mNotification.icon = R.drawable.noti;
		//当我们点击通知时显示的内容 
		mNotification.tickerText = "已过滤短信:"+body; 
		//通知时发出默认的声音 
		//		  mNotification.defaults = Notification.DEFAULT_SOUND;

		//设置通知显示的参数 
		mNotification.setLatestEventInfo(context, context.getString(R.string.app_name), body, mPendingIntent); 
		//可以理解为执行这个通知 
		mNotificationManager.notify(R.string.app_name, mNotification); 

		/** 取消 */

		//		  mNotificationManager.cancelAll();

	}
	
	public static void sendBlockMessageToMe(Context context,String msg){
		try
        {
          Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:godwasdog@gmail.com"));
          localIntent.putExtra("android.intent.extra.TEXT", "您的举报将造福所有用户!\n\n"+msg );
          localIntent.putExtra("android.intent.extra.SUBJECT", "[Report]感谢您的举报" );
          context.startActivity(localIntent);
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Toast.makeText(context, localActivityNotFoundException.getMessage(), 1).show();
        }
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
