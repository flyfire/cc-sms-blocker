package zhs.betalee.ccSMSBlocker.database;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import zhs.betalee.ccSMSBlocker.util.JsonParse;

import android.R;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Log;

public class ReadRules {
	//	private static final String[] PHONES_PROJECTION = new String[] {  
	//	       Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  
	private static final String[] PHONES_PROJECTION = new String[] {Phone.NUMBER};
	//	/**联系人显示名称**/  
	//    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
	//      
	//    /**电话号码**/  
	//    private static final int PHONES_NUMBER_INDEX = 1;  
	//      
	//    /**头像ID**/  
	//    private static final int PHONES_PHOTO_ID_INDEX = 2;  
	//     
	//    /**联系人的ID**/  
	//    private static final int PHONES_CONTACT_ID_INDEX = 3;  
	//	


	/**得到手机通讯录联系人信息
	 * @return **/
	public static String[] getPhoneContacts(Context context,String MCC) {
		//  	ContentResolver resolver = mContext.getContentResolver();
//		ArrayList<String> listPhoneNumber = new ArrayList<String>();
		Context mContext=context;

		int MCClength = MCC.length();
		// 获取手机联系人
		Cursor phoneCursor=null ;
		try {
			phoneCursor = mContext.getContentResolver().query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String[] listPhoneNumber = new String[phoneCursor.getCount()];
		int i=0;
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				//得到手机号码
				String phoneNumber = phoneCursor.getString(0);

				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				
				if (phoneNumber.startsWith(MCC)) {
		        	phoneNumber=phoneNumber.substring(MCClength);
				}				
				phoneNumber=phoneNumber.replaceAll("\\D", "");

								
				//得到联系人名称
				//		String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

				//得到联系人ID
				//		Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				//得到联系人头像ID
				//		Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				//得到联系人头像Bitamp
				//		Bitmap contactPhoto = null;

				//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				//		if(photoid > 0 ) {
				//		    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactid);
				//		    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
				//		    contactPhoto = BitmapFactory.decodeStream(input);
				//		}else {
				//		    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
				//		}

				//		mContactsName.add(contactName);
				//		mContactsNumber.add(phoneNumber);
				//		mContactsPhonto.add(contactPhoto);
				
				listPhoneNumber[i]=phoneNumber;
				i++;
			}


			//  		resolver=null;
		}
		phoneCursor.close();
		phoneCursor=null;
		return listPhoneNumber;
	}
	// 通过address手机号关联Contacts联系人的显示名字  
	public static String getPeopleNameFromPerson(Context context,String address){  
        if(address == null || address == ""){  
            return "( no address )\n";  
        }  
          
        String strPerson = null;  
        String[] projection = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};  
          
        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤  
        Cursor cursor = context.getContentResolver().query(uri_Person, projection, null, null, null);  
          
        if(cursor.moveToFirst()){  
            int index_PeopleName = cursor.getColumnIndex(Phone.DISPLAY_NAME);  
            String strPeopleName = cursor.getString(index_PeopleName);  
            strPerson = strPeopleName;  
        }  
        cursor.close();  
        cursor=null; 
        return strPerson;  
    }  
	public static boolean contactExists(Context context, String number) {
		/// number is the phone number
		Uri lookupUri = Uri.withAppendedPath(
				PhoneLookup.CONTENT_FILTER_URI, 
				Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return true;
			}
		} finally {
			if (cur != null){
				cur.close();
				cur=null;
			}
		}
		return false;
	}
	
	
	
	public static String[] getRulesNumbers(DbAdapter dbAdapter,String selection,String MCC){
//		ArrayList<String> listPhoneNumber = new ArrayList<String>();
		DbAdapter mDbAdapter=dbAdapter;
		dbAdapter=null;
		
		int MCClength = MCC.length();
		// 获取
		Cursor phoneCursor = mDbAdapter.fetchAllRulesType(selection);
		final int cursorSiez=phoneCursor.getCount();
		String[] listPhoneNumber=new String[cursorSiez];
		int i=0;
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				//得到手机号码
				String phoneNumber = phoneCursor.getString(1);
				
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				
				
				if (phoneNumber.startsWith(MCC)) {
		        	phoneNumber=phoneNumber.substring(MCClength);
				}
				phoneNumber=phoneNumber.replaceAll("[^\\d?*]", "");

//		        	else if (phoneNumber.startsWith("85") && phoneNumber.length() > 12) {
//					phoneNumber=phoneNumber.replaceFirst("85\\.","");
//				}else if (condition) {
//					
//				}
//				if (phoneNumber.contains("-") || phoneNumber.contains(" ")) {
//					Log.e("-", phoneNumber);
//					phoneNumber=phoneNumber.replaceAll("[^\\d?*]", "");
//				}


				listPhoneNumber[i]=phoneNumber;
//				System.out.println(listPhoneNumber[i]);
				i++;
			}

		}

		phoneCursor.close();
		phoneCursor=null;
		return listPhoneNumber;

	}
	
	public static String[] getRulesStrings(DbAdapter dbAdapter,String selection){
		DbAdapter mDbAdapter=dbAdapter;
		dbAdapter=null;
		// 获取
		Cursor wordCursor = mDbAdapter.fetchAllRulesType(selection);
		final int cursorSiez=wordCursor.getCount();
		String[] keyWoeds=new String[cursorSiez];
		int i=0;
		if (wordCursor != null) {
			while (wordCursor.moveToNext()) {

				
				String keyWoed = wordCursor.getString(1);
				
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(keyWoed))
					continue;

				keyWoeds[i]=keyWoed;
//				System.out.println(keyWoeds[i]);
				i++;
			}


		}
		wordCursor.close();
		wordCursor=null;
		return keyWoeds;

	}
	
	


	
	
}
