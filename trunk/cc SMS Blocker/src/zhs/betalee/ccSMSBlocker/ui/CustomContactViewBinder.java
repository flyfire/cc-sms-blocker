package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.database.ReadRules;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class CustomContactViewBinder implements ViewBinder{
	private Context context;
	public CustomContactViewBinder(Context context){
		this.context=context;
		context=null;
	}
	
	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// TODO Auto-generated method stub
		
		if(view.getId()==R.id.list_item_text1){  
			
			int status=cursor.getInt(cursor.getColumnIndex(DbAdapter.STATUS));

			TextView textView=(TextView)view;
			
			String num=ReadRules.getPeopleNameFromPerson(context, cursor.getString(1));		
			textView.setText((num==null)?cursor.getString(1):num);

//			Log.e("text1", cursor.getString(1));
			textView.setTextColor(((status & 1) == 0)?0xfffe4902:0xff333333);
//			if ((status & 1) == 0 ) {
//				textView.setTextColor(0xfffe4902);
//			}else {
//				textView.setTextColor(0xff333333);
//			}

			return true;  
		}else if (view.getId()==R.id.list_item_text4) {
			long time = cursor.getLong(cursor.getColumnIndex(DbAdapter.FORMTIME));
			TextView textView4=(TextView)view;
			textView4.setText(MessageUtils.formatTimeStampString(context, time, true));
//			Log.e("text4",MessageUtils.formatTimeStampString(context, time, true));
			return true;  
		}  
		return false;
	}

}
