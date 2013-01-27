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

//    	开启CC短信过滤
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
         //扣费号码
         if (Pattern.matches("1062.*", addressnumber)||Pattern.matches("1066.*", addressnumber)) {
        	 blockMessage(context,"[回复扣费号码]");
        	 return;
         }

         //扣费号码 end    	
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////         
         
         
         
//    	不拦截联系人||仅接受联系人白名单
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
        	//白名单
        	//白号码
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
        	//白关键词
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
        	//白名单end
        	
        	if (Settings.getBoolean(context, "onlycontactwhite")) {
        		blockMessage(context,"[仅接受联系人白名单]");
        		
        		return;
    		}
		}//    	不拦截联系人||仅接受联系人白名单 end
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////
//    	黑名单
    	
    	//黑号码
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
					blockMessage(context, "[黑号码]"+phoneNumbers[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
			tempNum.delete(0, tempNum.length());
			
		}
    	//黑号码 end
    	//匹配位数
    	ArrayList<String> listCountNumber=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_COUNT_NUMBER + "'");
    	String[] conutNumbers = listCountNumber.toArray(new String[listCountNumber.size()]);
    	size=conutNumbers.length;
    	int count=addressnumber.length();
    	int tempCount;
    	for (int i=0;i<size;i++) {
    		tempCount=Integer.parseInt(conutNumbers[i]);
    		try {	
				if (count == tempCount) {
					blockMessage(context, "[匹配位数号码]"+conutNumbers[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
		}
    	//匹配位数 end
    	
    	//可疑诈骗///////////////////
    	String[] zapianStrings=new String[] {".*汇.*钱.*",".*钱.*汇.*",".*打.*钱.*",".*钱.*打.*",".*汇.*款.*",
    			".*款.*汇.*",".*打.*款.*",".*款.*打.*",".*存.*款.*",".*款.*存.*",".*邮政.*包裹.*",
    			".*包裹.*邮政.*",".*机.*幸运.*码.*",".*机号.*幸运.*",".*通知.*违章.*联系.*",
    			".*银行[】\\]\\.\\。]*[\\w\\s]?",".*[【\\[].\\s*行[】\\]]*[\\w\\s]?"};
    	size=zapianStrings.length;

    	for (int i=0;i<size;i++) {

    		try {

    			//    			Log.e("black_keyword_list", tempNum.toString());
    			if (Pattern.matches(zapianStrings[i], msgbodyString)) {
    				blockMessage(context, "[可疑诈骗]");
    				return;
    			}
    		} catch (RuntimeException e) {

    		}

    	}

    	
    	
//    	可疑诈骗 end
    	/////////////////////
    	
    	
    	
    	//黑关键词 
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
					blockMessage(context, "[关键词]"+keyWords[i]);
					return;
				}
			} catch (RuntimeException e) {
				
			}
			
		}
    	//黑关键词 end
    	//正则表达式 
    	ArrayList<String> listRegexp=ReadRules.getNumberRules(mDbAdapter,"type='"+ Constants.TYPE_BLOCKED_KEYWORD_REGEXP + "'");
    	String[] regexpKeyWords = listRegexp.toArray(new String[listRegexp.size()]);
    	size=regexpKeyWords.length;
    	for (int i=0;i<size;i++) {

    		try {	
				if (Pattern.matches(regexpKeyWords[i], msgbodyString)) {
					blockMessage(context, "[正则表达式 ]"+regexpKeyWords[i]);
					return;
				}
			} catch (RuntimeException e) {
			}
		}
    	//正则表达式 end
    	
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