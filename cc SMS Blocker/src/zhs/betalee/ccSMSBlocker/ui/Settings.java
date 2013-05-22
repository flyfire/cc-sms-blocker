package zhs.betalee.ccSMSBlocker.ui;


import zhs.betalee.ccSMSBlocker.CCBlockerService;
import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.ui.sms.SmsRulesListView;
import zhs.betalee.ccSMSBlocker.util.BlockPhoneCallUtils;

import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.TimePicker;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	private Preference startTime;
	private Preference endtTime;


	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals("enablephoneblocker")){
			
	      }

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
//		this.enablenotification.setSummary(this.Refreshrate.getEntry());
//		CheckBoxPreference enableNotification = ((CheckBoxPreference) findPreference("enablenotification"));

		startTime = (Preference)findPreference("starttime");
		endtTime = (Preference)findPreference("endtime");
		SharedPreferences sharedPreferences =PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		
		startTime.setSummary(toFullTimeFormat(sharedPreferences.getInt("startTimeHour", 0))+":"+toFullTimeFormat(sharedPreferences.getInt("startTimeMin", 0)));
		endtTime.setSummary(toFullTimeFormat(sharedPreferences.getInt("endTimeHour", 7))+":"+toFullTimeFormat(sharedPreferences.getInt("endTimeMin", 0)));
		
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
		.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if (preference.getKey().equals("mailto"))
	      {
			try{
				Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:ccsmsblocker@gmail.com"));
				localIntent.putExtra("android.intent.extra.SUBJECT", "CC SMS Blocker "+getString(R.string.app_ver) );
				startActivity(localIntent);
			}catch (ActivityNotFoundException localActivityNotFoundException){
				Toast.makeText(getApplication(), localActivityNotFoundException.getMessage(), 1).show();
			}
	    }else if (preference.getKey().equals("author")) {
	    	startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://me.alipay.com/betalee")));
		}else if (preference.getKey().equals("starttime")) {
			int mHour=preferenceScreen.getSharedPreferences().getInt("startTimeHour", 0);
			int mMinute=preferenceScreen.getSharedPreferences().getInt("startTimeMin", 0);
			TimePickerDialog startTimeDialog=new TimePickerDialog(preferenceScreen.getContext(), mStartTimeSetListener, mHour, mMinute, true);
			startTimeDialog.setTitle("开始时间");
			startTimeDialog.show();
		}else if (preference.getKey().equals("endtime")) {
			int mHour=preferenceScreen.getSharedPreferences().getInt("endTimeHour", 7);
			int mMinute=preferenceScreen.getSharedPreferences().getInt("endTimeMin", 0);
			TimePickerDialog startTimeDialog=new TimePickerDialog(preferenceScreen.getContext(), mEndTimeSetListener, mHour, mMinute, true);
			startTimeDialog.setTitle("结束时间");
			startTimeDialog.show();
		}else if (preference.getKey().equals("shareCC")) {
			Intent shareCC=new Intent(Intent.ACTION_SEND);
	         shareCC.setType("text/plain");   
	         shareCC.putExtra(Intent.EXTRA_SUBJECT, "推荐好用的<CC短信拦截>");   
	         shareCC.putExtra(Intent.EXTRA_TEXT, "<CC短信拦截>小巧强大支持正则表达式的垃圾短信拦截软件,具有8种拦截规则，各种自定义设置。Google play下载最新版本或国内http://t.cn/zYLeoIP");    
	         shareCC.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	         startActivity(shareCC);
		}else if (preference.getKey().equals("sendsmstoemail")) {
			startActivity(new Intent(getApplicationContext(), SendSMSToEmail.class));
		}else if (preference.getKey().equals("AdvancedSettings")) {
			startActivity(new Intent(getApplicationContext(), AdvancedSettings.class));
		}else if (preference.getKey().equals("systemrules")) {
			startActivity(new Intent(getApplicationContext(), SystemRules.class));
		}else if (preference.getKey().equals("allowmsglog")) {
			startActivity(new Intent(getApplicationContext(), AllowedMsgListView.class));
		}else if (preference.getKey().equals("testsmsblocker")) {
			startActivity(new Intent(getApplicationContext(), TestSMSBlocker.class));
		}else if (preference.getKey().equals("smsruleslistview")) {
			startActivity(new Intent(getApplicationContext(), SmsRulesListView.class));
		}else if (preference.getKey().equals("ruleshelp")) {
			startActivity(new Intent(getApplicationContext(), RulesHelp.class));
		}else if (preference.getKey().equals("swpiesettings")) {
			startActivity(new Intent(getApplicationContext(), SwipeSettings.class));
		}else if (preference.getKey().equals("pinfen")) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=zhs.betalee.ccSMSBlocker"));
				startActivity(intent); 
			} catch (Exception e) {
				Toast.makeText(getApplication(), "没找到Google play", 1).show();
			}
		}
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	private TimePickerDialog.OnTimeSetListener mStartTimeSetListener =
		new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Editor editor=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			editor.putInt("startTimeHour",hourOfDay);
			editor.putInt("startTimeMin",minute);
			editor.commit();			
			startTime.setSummary(toFullTimeFormat(hourOfDay)+":"+toFullTimeFormat(minute));
		}

	};
	private TimePickerDialog.OnTimeSetListener mEndTimeSetListener =
		new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Editor editor=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			editor.putInt("endTimeHour",hourOfDay);
			editor.putInt("endTimeMin",minute);
			editor.commit();	
			endtTime.setSummary(toFullTimeFormat(hourOfDay)+":"+toFullTimeFormat(minute));
		}

	};
	private String toFullTimeFormat(int num){
		if (num < 10) {
			return "0"+Integer.toString(num);
		}
		return Integer.toString(num);
	}
	
	public static boolean getBoolean(Context context,String mstring) {
		Context mContext=context;
		context=null;
		if (mstring.equals("onlycontactwhite") || mstring.equals("notifyled") || mstring.equals("enablemmsblocker")
				||mstring.equals("period") || mstring.equals("enablephoneblocker")) {
			return PreferenceManager.getDefaultSharedPreferences(mContext)
			.getBoolean(mstring, false);
		}
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(mstring, true);
	}
	
	public static int getInt(Context context,String mstring) {
		Context mContext=context;
		context=null;
		if (mstring.equals("endTimeHour")) {
			return PreferenceManager.getDefaultSharedPreferences(mContext)
			.getInt(mstring, 7);
		}
		return PreferenceManager.getDefaultSharedPreferences(mContext)
				.getInt(mstring, 0);
	}
	

}
