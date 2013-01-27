package zhs.betalee.ccSMSBlocker;
//
//import static android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
//import android.telephony.SmsMessage;
//import android.provider.Telephony.Sms.Intents;
//
public class SmsReceiverService extends Service{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		
//		int error = intent.getIntExtra("errorCode", 0);
//            handleSmsReceived(intent, error);
//        		
//		
//		return super.onStartCommand(intent, flags, startId);
//	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
//	
//	 private void handleSmsReceived(Intent intent, int error) {
//	        SmsMessage[] msgs = Intents.getMessagesFromIntent(intent);
//	        Uri messageUri = insertMessage(this, msgs, error);
//
//	        if (Log.isLoggable(LogTag.TRANSACTION, Log.VERBOSE)) {
//	            SmsMessage sms = msgs[0];
//	            Log.v(TAG, "handleSmsReceived" + (sms.isReplace() ? "(replace)" : "") +
//	                    " messageUri: " + messageUri +
//	                    ", address: " + sms.getOriginatingAddress() +
//	                    ", body: " + sms.getMessageBody());
//	        }
//
//	        if (messageUri != null) {
//	            // Called off of the UI thread so ok to block.
//	            MessagingNotification.blockingUpdateNewMessageIndicator(this, true, false);
//	        }
//	    }
}
