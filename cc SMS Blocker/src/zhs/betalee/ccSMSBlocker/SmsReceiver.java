package zhs.betalee.ccSMSBlocker;
/*
 * Copyright (C) 2007-2008 Esmertec AG.
 * Copyright (C) 2007-2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.database.ReadRules;
import zhs.betalee.ccSMSBlocker.ui.Main;
import zhs.betalee.ccSMSBlocker.ui.Settings;
import zhs.betalee.ccSMSBlocker.util.JsonParse;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;




import android.R.integer;
import android.R.raw;
import android.R.string;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.text.InputFilter.AllCaps;
import android.util.Log;
import android.widget.Toast;


/**
 * Handle incoming SMSes.  Just dispatches the work off to a Service.
 */
public class SmsReceiver extends BroadcastReceiver {
	private StringBuilder msgbody=new StringBuilder();
	private String formaddress;
	private long fromtime;
	
	private DbAdapter mDbAdapter;
//	private ReadRules readRules;
	
    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;
    private static SmsReceiver sInstance;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static SmsReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new SmsReceiver();
        }
        return sInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent){

//    	¿ªÆôCC¶ÌÐÅ¹ýÂË
    	if (!Settings.getBoolean(context, "enablesmsblocker")) {
    		return;
		}    	
    	if (intent.getAction().equals(SMS_RECEIVED)) 
    	{
    		Object[] pdus = (Object[])intent.getExtras().get("pdus");
    		SmsMessage[] messages = new SmsMessage[pdus.length];
    		for (int i = 0; i < pdus.length; i++){
    			messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
    		}

    		for (SmsMessage message : messages) 
    		{
    			msgbody.append(message.getMessageBody());
    			formaddress = message.getOriginatingAddress();
    			fromtime=message.getTimestampMillis();
    		}
    	}

    	 mDbAdapter = new DbAdapter(context);
         mDbAdapter.open();
         String addressnumber=formaddress.replaceFirst("\\+86", "");
         String msgbodyString=msgbody.toString();
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
         //¿Û·ÑºÅÂë
         if (Pattern.matches("1062.*", addressnumber)||Pattern.matches("1066.*", addressnumber)) {
        	 blockMessage(context,"[»Ø¸´¿Û·ÑºÅÂë]");
        	 return;
         }

         //¿Û·ÑºÅÂë end    	
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////         
         
         
         
//    	²»À¹½ØÁªÏµÈË||½ö½ÓÊÜÁªÏµÈË°×Ãûµ¥
    	if (Settings.getBoolean(context, "phonecontact") || Settings.getBoolean(context, "onlycontactwhite")) {

    		ArrayList<String> listphoneNumber=ReadRules.getPhoneContacts(context);
        	String[] phoneNumbers = listphoneNumber.toArray(new String[listphoneNumber.size()]);
        	
        	int size=phoneNumbers.length;
        	for (int i=0;i<size;i++) {
        		try {
    				if (Pattern.matches(phoneNumbers[i], addressnumber)) {

    					mDbAdapter.close();
    					return;
    				}
    			} catch (RuntimeException e) {
    				
    			}
    		}
        	//°×Ãûµ¥
        	//°×ºÅÂë
        	ArrayList<String> listWhitePhoneNumber=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_TRUSTED_NUMBER + "'");
        	try {
        		ArrayList<String> jsonWhitePhoneNumber=JsonParse.stringBuilderToArray(JsonParse.jsonToStringBuilder(context,zhs.betalee.ccSMSBlocker.R.raw.white_num_list),null);
        		listWhitePhoneNumber.addAll(jsonWhitePhoneNumber);
        	} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	String[] whitePhoneNumbers = listWhitePhoneNumber.toArray(new String[listWhitePhoneNumber.size()]);
        	size=whitePhoneNumbers.length;
        	for (int i=0;i<size;i++) {
        		whitePhoneNumbers[i]=whitePhoneNumbers[i].replaceAll("\\?", ".").replaceAll("\\*", ".*");
        		try {
        			
    				if (Pattern.matches(whitePhoneNumbers[i], addressnumber)) {
    					mDbAdapter.close();
    					return;
    				}
    			} catch (RuntimeException e) {
    				
    			}
    		}
        	//°×¹Ø¼ü´Ê
        	ArrayList<String> listWhiteWordNumber=ReadRules.whiteKeyWordRules(mDbAdapter);
        	String[] whiteWordNumbers = listWhiteWordNumber.toArray(new String[listWhiteWordNumber.size()]);
        	size=whiteWordNumbers.length;
        	
        	for (int i=0;i<size;i++) {
        		try {
    				if (Pattern.matches(whiteWordNumbers[i], msgbodyString)) {
    					mDbAdapter.close();
    					return;
    				}
    			} catch (RuntimeException e) {
    				
    			}
    		}
        	//°×Ãûµ¥end
        	
        	if (Settings.getBoolean(context, "onlycontactwhite")) {
        		blockMessage(context,"[½ö½ÓÊÜÁªÏµÈË°×Ãûµ¥]");
        		
        		return;
    		}
		}//    	²»À¹½ØÁªÏµÈË||½ö½ÓÊÜÁªÏµÈË°×Ãûµ¥ end
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
//    	ºÚÃûµ¥
    	
    	//ºÚºÅÂë
    	ArrayList<String> listPhoneNumber=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_NUMBER + "' or type='"+Constants.TYPE_BLOCKED_BEGINNING_OF_NUMBER+"'");
    	try {
    		ArrayList<String> jsonPhoneNumber=JsonParse.stringBuilderToArray(JsonParse.jsonToStringBuilder(context,zhs.betalee.ccSMSBlocker.R.raw.black_num_list),null);
    		listPhoneNumber.addAll(jsonPhoneNumber);
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	String[] phoneNumbers = listPhoneNumber.toArray(new String[listPhoneNumber.size()]);
    	int size=phoneNumbers.length;
    	StringBuilder tempNum=new StringBuilder();
    	for (int i=0;i<size;i++) {
    		tempNum.append(phoneNumbers[i].replaceAll("\\?", ".").replaceAll("\\*", ".*"));
    		try {
    			
				if (Pattern.matches(tempNum.toString(), addressnumber)) {
					blockMessage(context, "[ºÚºÅÂë]"+phoneNumbers[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
			tempNum.delete(0, tempNum.length());
			
		}
    	//ºÚºÅÂë end
    	//Æ¥ÅäÎ»Êý
    	ArrayList<String> listCountNumber=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_COUNT_NUMBER + "'");
    	String[] conutNumbers = listCountNumber.toArray(new String[listCountNumber.size()]);
    	size=conutNumbers.length;
    	int count=addressnumber.length();
    	int tempCount;
    	for (int i=0;i<size;i++) {
    		tempCount=Integer.parseInt(conutNumbers[i]);
    		try {	
				if (count == tempCount) {
					blockMessage(context, "[Æ¥ÅäÎ»ÊýºÅÂë]"+conutNumbers[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
		}
    	//Æ¥ÅäÎ»Êý end
    	

    	//可疑诈骗///////////////////
    	final String[] zapianStrings=new String[] {".*账号.*",".*账户.*",".*汇[^\\p{P}]*钱.*",".*钱[^\\p{P}]*汇.*",".*打[^\\p{P}]*钱.*",".*钱[^\\p{P}]*打.*",".*汇[^\\p{P}]*款.*",
    			".*款[^\\p{P}]*汇.*",".*打[^\\p{P}]*款.*",".*款[^\\p{P}]*打.*",".*存[^\\p{P}]*款.*",".*款[^\\p{P}]*存.*",".*邮政.*包裹.*",
    			".*包裹.*邮政.*",".*机.*幸运.*码.*",".*机号.*幸运.*",".*通知.*违章.*联系.*",
    			".*银行[】\\]\\.\\。]*\\w{0,3}",".*[【\\[].?行[】\\]]*\\w{0,3}"};
    	size=zapianStrings.length;

    	for (int i=0;i<size;i++) {
    		try {
    			if (patternMatches(zapianStrings[i], msgbodyMatcher)&&addressnumber.length()==11) {
    				blockMessage(mContext, "[可疑诈骗]");
    				return;
    			}
    		} catch (RuntimeException e) {
    		}
    	}

    	
    	
//可疑诈骗 end
/////////////////////
    	

    	
    	
    	
    	//ºÚ¹Ø¼ü´Ê 
    	ArrayList<String> listKeyWord=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_KEYWORD +"'");
    	try {
    		ArrayList<String> jsonKeyWord=JsonParse.stringBuilderToArray(JsonParse.jsonToStringBuilder(context,zhs.betalee.ccSMSBlocker.R.raw.black_keyword_list),"KEYWORD");
    		listKeyWord.addAll(jsonKeyWord);
    	} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	String[] keyWords = listKeyWord.toArray(new String[listKeyWord.size()]);
    	size=keyWords.length;

    	for (int i=0;i<size;i++) {
			StringBuilder mKeyWord=new StringBuilder(keyWords[i]);
			mKeyWord.insert(0, ".*");
			mKeyWord.insert(mKeyWord.length(), ".*");
    		try {
    			
				if (Pattern.matches(mKeyWord.toString(), msgbodyString)) {
					blockMessage(context, "[¹Ø¼ü´Ê]"+keyWords[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
			
		}
    	//ºÚ¹Ø¼ü´Ê end
    	//ÕýÔò±í´ïÊ½ 
    	ArrayList<String> listRegexp=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_KEYWORD_REGEXP + "'");
    	String[] regexpKeyWords = listRegexp.toArray(new String[listRegexp.size()]);
    	size=regexpKeyWords.length;
    	for (int i=0;i<size;i++) {

    		try {	
				if (Pattern.matches(regexpKeyWords[i], msgbodyString)) {
					blockMessage(context, "[ÕýÔò±í´ïÊ½ ]"+regexpKeyWords[i]);
					return;
				}
			} catch (RuntimeException e) {
			}
		}
    	//ÕýÔò±í´ïÊ½ end
    	
////////////////////////////////////////////////////////////////////////////
//    	All End
    	mDbAdapter.close();
    }

    private void blockMessage(Context mcontext,String blockString) {
		// TODO Auto-generated method stub
    	abortBroadcast();
        mDbAdapter.createOne(formaddress, msgbody.toString(),fromtime,blockString);
        mDbAdapter.close();
        MessageUtils.updateNotifications(mcontext,msgbody.toString());
       
	}


    
    
	protected void onReceiveWithPrivilege(Context context, Intent intent, boolean privileged) {
        // If 'privileged' is false, it means that the intent was delivered to the base
        // no-permissions receiver class.  If we get an SMS_RECEIVED message that way, it
        // means someone has tried to spoof the message by delivering it outside the normal
        // permission-checked route, so we just ignore it.
//        if (!privileged && intent.getAction().equals(Intents.SMS_RECEIVED_ACTION)) {
//            return;
//        }

        intent.setClass(context, SmsReceiverService.class);
        intent.putExtra("result", getResultCode());
//        beginStartingService(context, intent);
        context.startService(intent);
    }

    // N.B.: <code>beginStartingService</code> and
    // <code>finishStartingService</code> were copied from
    // <code>com.android.calendar.AlertReceiver</code>.  We should
    // factor them out or, even better, improve the API for starting
    // services under wake locks.

    /**
     * Start the service to process the current event notifications, acquiring
     * the wake lock before returning to ensure that the service will run.
     */
    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            if (mStartingService == null) {
                PowerManager pm =
                    (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "StartingAlertService");
                mStartingService.setReferenceCounted(false);
            }
            mStartingService.acquire();
            context.startService(intent);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishStartingService(Service service, int startId) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    mStartingService.release();
                }
            }
        }
    }
    
    public void finishBlockSms(Service service, int startId) {
    	if (service.stopSelfResult(startId)) {
    		abortBroadcast();
                   }
                }
    public boolean patternMatches(String regexpstr, String string) {
		String regexp = regexpstr.replaceAll("\\?", ".").replaceAll("\\*", ".*");
		return Pattern.matches(regexp, string);
	}
}