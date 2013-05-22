package zhs.betalee.ccSMSBlocker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences  mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
		if (mSharedPreferences.getBoolean("enableservice", false)&&mSharedPreferences.getBoolean("enablesmsblocker", true)) {
			context.startService(new Intent(context, CCBlockerService.class));
		}

		
	}

}
