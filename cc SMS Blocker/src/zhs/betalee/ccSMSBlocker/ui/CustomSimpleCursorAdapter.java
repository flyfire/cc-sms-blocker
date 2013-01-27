package zhs.betalee.ccSMSBlocker.ui;


import zhs.betalee.ccSMSBlocker.database.Constants;

import zhs.betalee.ccSMSBlocker.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomSimpleCursorAdapter extends SimpleCursorAdapter{
//	private Cursor m_cursor;  
//    private Context m_context;  
	private static TextView mText1;
	private static TextView mText2;
	
	private LayoutInflater mInflater;
	
	public CustomSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		// TODO Auto-generated constructor stub
//		m_cursor = c;  
//        m_context = context;

	}


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
//		Unread =#fe4902
		 mText1 = (TextView)view.findViewById(android.R.id.text1);
		 mText2 = (TextView)view.findViewById(android.R.id.text2);
		String rule = cursor.getString(1);
		int ruleType = cursor.getInt(2);
		switch (ruleType) {
		case Constants.TYPE_BLOCKED_NUMBER:
			mText2.setText(R.string.type_blocked_number);
			break;
		case Constants.TYPE_TRUSTED_NUMBER:
			mText2.setText(R.string.type_trusted_number);
			break;	
		case Constants.TYPE_BLOCKED_BEGINNING_OF_NUMBER:
			mText2.setText(R.string.type_blocked_beginning_of_number);
			break;
		case Constants.TYPE_BLOCKED_KEYWORD:
			mText2.setText(R.string.type_blocked_keyword);
			break;
		case Constants.TYPE_TRUSTED_KEYWORD:
			mText2.setText(R.string.type_trusted_keyword);
			break;
		case Constants.TYPE_BLOCKED_COUNT_NUMBER:
			mText2.setText(R.string.type_blocked_count_number);
			break;
		case Constants.TYPE_BLOCKED_KEYWORD_REGEXP:
			mText2.setText(R.string.type_blocked_keyword_regexp);
			break;
		}
		
		mText1.setText(rule);
		
	}


}
