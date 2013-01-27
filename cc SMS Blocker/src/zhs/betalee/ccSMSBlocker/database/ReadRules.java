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
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;

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
	public static ArrayList<String> getPhoneContacts(Context mContext) {
		//  	ContentResolver resolver = mContext.getContentResolver();
		ArrayList<String> listPhoneNumber = new ArrayList<String>();
		// 获取手机联系人
		Cursor phoneCursor = mContext.getContentResolver().query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);


		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				//得到手机号码
				String phoneNumber = phoneCursor.getString(0);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				if (phoneNumber.startsWith("\\+86")) {
					phoneNumber.replaceFirst("\\+86","");
				}
				phoneNumber.replaceAll("\\D", "");
				//    			phoneNumber.replaceAll("[\\+86\\D]", "");
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
				listPhoneNumber.add(phoneNumber);
			}

			phoneCursor.close();
			//  		resolver=null;
		}
		return listPhoneNumber;
	}

	public static ArrayList<String> getNumberRules(DbAdapter mDbAdapter,String selection){
		ArrayList<String> listPhoneNumber = new ArrayList<String>();
		// 获取
		Cursor phoneCursor = mDbAdapter.fetchAllRulesType(selection);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				//得到手机号码
				String phoneNumber = phoneCursor.getString(1);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				if (phoneNumber.startsWith("\\+86")) {
					phoneNumber.replaceFirst("\\+86","");
				}

				listPhoneNumber.add(phoneNumber);
			}

			phoneCursor.close();

		}
		
		
		
		return listPhoneNumber;

	}
	public static ArrayList<String> whiteKeyWordRules(DbAdapter mDbAdapter){
		ArrayList<String> listWordNumber = new ArrayList<String>();
		// 获取
		Cursor wordCursor = mDbAdapter.fetchAllRulesType("type='"+ Constants.TYPE_TRUSTED_KEYWORD + "'");

		if (wordCursor != null) {
			while (wordCursor.moveToNext()) {

				//得到手机号码
				String keyWord = wordCursor.getString(1);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(keyWord))
					continue;
//				keyWord=".*"+keyWord+".*";	
				listWordNumber.add(keyWord);
			}

			wordCursor.close();

		}

		return listWordNumber;

	}


	
	
}
