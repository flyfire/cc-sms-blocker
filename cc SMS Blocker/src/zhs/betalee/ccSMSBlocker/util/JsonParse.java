package zhs.betalee.ccSMSBlocker.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import zhs.betalee.ccSMSBlocker.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class JsonParse {

	public static String jsonToStringBuilder(Context mContext,int RawResource,boolean isUTF8){
		// **********解析txt
		InputStream inputStream = mContext.getResources().openRawResource(RawResource);

		InputStreamReader inputStreamReader = null;

		try {
			if (isUTF8) {
				inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
			}else {
				inputStreamReader = new InputStreamReader(inputStream);
			}

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// >>>>>>>>>>>>>>>>>>>>>>>>>完成读取TXT,变量sb
//		Log.e("sb.toString",sb.toString());
		return sb.toString();
		
	}

	public static String[] stringBuilderToArray(String lrc1,String objectString)
	throws Exception {
		if (objectString==null) {
			objectString="NUM";
		}
//		ArrayList<String> mlists = new ArrayList<String>();


		// JSONArray contentArray1=getJSONArray(lrc1);
		JSONArray ja = new JSONArray(lrc1);
		int jalength=ja.length();
//		System.out.println("一共有对象的个数" + jalength);
		String[] mlists=new String[jalength];
		
		for (int j = 0; j < jalength; j++) {

			// JSONObject jsonObject=new JSONObject(lrc1);
			// //返回的数据形式是一个Object类型，所以可以直接转换成一个Object
			JSONObject jsonObject = (JSONObject) ja.get(j);

//			String type = jsonObject.getString("TYPE");
			String number = jsonObject.getString(objectString);
			if (TextUtils.isEmpty(number))
				continue;
			
//				number.replaceAll("\\?", ".").replaceAll("\\*", ".*");
				mlists[j]=number;
			

		}
		return mlists;
	}
//	// Query the API for content
//    String content = getUrlContent(String.format(WIKTIONARY_PAGE,
//            encodedTitle, expandClause));
//    try {
//        // Drill into the JSON response to find the content body
//        JSONObject response = new JSONObject(content);
//        JSONObject query = response.getJSONObject("query");
//        JSONObject pages = query.getJSONObject("pages");
//        JSONObject page = pages.getJSONObject((String) pages.keys().next());
//        JSONArray revisions = page.getJSONArray("revisions");
//        JSONObject revision = revisions.getJSONObject(0);
//        return revision.getString("*");
//    } catch (JSONException e) {
//        throw new ParseException("Problem parsing API response", e);
//    }
	 /**
     * Convert the RawContact object into a JSON string.  From the
     * JSONString interface.
     * @return a JSON string representation of the object
     */
//    public JSONObject toJSONObject() {
//        JSONObject json = new JSONObject();
//
//        try {
//            if (!TextUtils.isEmpty(mFirstName)) {
//                json.put("f", mFirstName);
//            }
//            if (!TextUtils.isEmpty(mLastName)) {
//                json.put("l", mLastName);
//            }
//            if (!TextUtils.isEmpty(mCellPhone)) {
//                json.put("m", mCellPhone);
//            }
//            if (!TextUtils.isEmpty(mOfficePhone)) {
//                json.put("o", mOfficePhone);
//            }
//            if (!TextUtils.isEmpty(mHomePhone)) {
//                json.put("h", mHomePhone);
//            }
//            if (!TextUtils.isEmpty(mEmail)) {
//                json.put("e", mEmail);
//            }
//            if (mServerContactId > 0) {
//                json.put("i", mServerContactId);
//            }
//            if (mRawContactId > 0) {
//                json.put("c", mRawContactId);
//            }
//            if (mDeleted) {
//                json.put("d", mDeleted);
//            }
//        } catch (final Exception ex) {
//            Log.i(TAG, "Error converting RawContact to JSONObject" + ex.toString());
//        }
//
//        return json;
//    }
//	// >>>>>>>>>>>>>>>>>>>>>>>>>网络取json
//	/**
//	 * 解析Json数据
//	 * 
//	 * @param urlPath
//	 * @return mlists
//	 * @throws Exception
//	 */
//	public static List<Person> getListPerson(String urlPath) throws Exception {
//		List<Person> mlists = new ArrayList<Person>();
//		byte[] data = readParse(urlPath);
//		JSONArray array = new JSONArray(new String(data));
//		for (int i = 0; i < array.length(); i++) {
//			JSONObject item = array.getJSONObject(i);
//			String name = item.getString("name");
//			String address = item.getString("address");
//			int age = item.getInt("age");
//			mlists.add(new Person(name, address, age));
//		}
//		return mlists;
//	}
//
//	/**
//	 * 从指定的url中获取字节数组
//	 * 
//	 * @param urlPath
//	 * @return 字节数组
//	 * @throws Exception
//	 */
//	public static byte[] readParse(String urlPath) throws Exception {
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		byte[] data = new byte[1024];
//		int len = 0;
//		URL url = new URL(urlPath);
//		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//		InputStream inStream = conn.getInputStream();
//
//		while ((len = inStream.read(data)) != -1) {
//			outStream.write(data, 0, len);
//
//		}
//		inStream.close();
//		return outStream.toByteArray();
//
//	}
}
