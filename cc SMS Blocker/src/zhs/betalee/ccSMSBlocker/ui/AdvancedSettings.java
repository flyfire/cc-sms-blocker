package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class AdvancedSettings extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.advancedsettings);
	}
	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onPreferenceTreeClick(android.preference.PreferenceScreen, android.preference.Preference)
	 */
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub

		
		
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	
//	public static boolean getBoolean(Context context,String mstring) {
////		if (mstring.equals("onlycontactwhite")||mstring.equals("builtinblacklist")) {
////			return PreferenceManager.getDefaultSharedPreferences(context)
////			.getBoolean(mstring, false);
////		}
//		return PreferenceManager.getDefaultSharedPreferences(context)
//				.getBoolean(mstring, true);
//	}


}
