package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
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
	        try
	        {
	          Intent localIntent = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:ccsmsblocker@gmail.com"));
	          localIntent.putExtra("android.intent.extra.SUBJECT", "CC SMS Blocker "+getString(R.string.app_ver) );
	          startActivity(localIntent);
	        }
	        catch (ActivityNotFoundException localActivityNotFoundException)
	        {
	          Toast.makeText(this, localActivityNotFoundException.getMessage(), 1).show();
	        }
	      }else if (preference.getKey().equals("ruleshelp")) {
			// TODO
	    	  startActivity(new Intent(getApplicationContext(), RulesHelp.class));
		}
		
		
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	
	public static boolean getBoolean(Context context,String mstring) {
		if (mstring.equals("onlycontactwhite")) {
			return PreferenceManager.getDefaultSharedPreferences(context)
			.getBoolean(mstring, false);
		}
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(mstring, true);
	}
	
}
