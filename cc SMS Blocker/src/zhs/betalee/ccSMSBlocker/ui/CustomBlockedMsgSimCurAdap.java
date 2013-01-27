package zhs.betalee.ccSMSBlocker.ui;


import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import zhs.betalee.ccSMSBlocker.R;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomBlockedMsgSimCurAdap extends SimpleCursorAdapter{
	private static TextView mText1;
	private static TextView mText2;
	private static TextView mText3;
	private static TextView mText4;
	
	public CustomBlockedMsgSimCurAdap(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub

		 mText1 = (TextView)view.findViewById(R.id.list_item_text1);
		 mText2 = (TextView)view.findViewById(R.id.list_item_text2);
		 mText3 = (TextView)view.findViewById(R.id.list_item_text3);
		 mText4 = (TextView)view.findViewById(R.id.list_item_text4);
		String num = cursor.getString(1);
		long time = cursor.getLong(3);
		String body = cursor.getString(2);
		String rules = cursor.getString(4);
		int status=cursor.getInt(5);
		
		mText4.setText(MessageUtils.formatTimeStampString(context, time, true));
		
		mText1.setText(num);
		mText2.setText(rules);
		mText3.setText(body);
		
//		Isread
//		Unread =#fe4902
//		char a='0',b='1';
		if ((status & 1) == 0 ) {
			mText1.setTextColor(0xfffe4902);
		}

	}
	
}
