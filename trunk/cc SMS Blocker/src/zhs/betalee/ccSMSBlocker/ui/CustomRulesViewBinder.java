package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.Constants;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class CustomRulesViewBinder implements ViewBinder{

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		// TODO Auto-generated method stub
		if(view.getId()==android.R.id.text2){
			TextView textView=(TextView)view;

			int ruleType = cursor.getInt(2);
			switch (ruleType) {
			case Constants.CUSTOM_BLOCKED_NUMBER:
				textView.setText(R.string.custom_blocked_number);
				break;
			case Constants.CUSTOM_TRUSTED_NUMBER:
				textView.setText(R.string.custom_trusted_number);
				break;	
			case Constants.MMS_TYPE_BLOCKED_NUMBER:
				textView.setText(R.string.mms_custom_blocked_number);
				break;	
			case Constants.IN_BLOCKED_KEYWORD:
				textView.setText(R.string.in_blocked_keyword);
				break;
			case Constants.CUSTOM_TRUSTED_KEYWORD:
				textView.setText(R.string.custom_trusted_keyword);
				break;
			case Constants.CUSTOM_BLOCKED_COUNT_NUMBER:
				textView.setText(R.string.custom_blocked_count_number);
				break;
			case Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP:
				textView.setText(R.string.custom_blocked_keyword_regexp);
				break;
			case Constants.IN_TRUSTED_NUMBER:
				textView.setText(R.string.in_trusted_number);
				break;
			case Constants.CUSTOM_BLOCKED_KEYWORD:
				textView.setText(R.string.custom_blocked_keyword);
				break;	
			case Constants.PHONE_CUSTOM_BLOCKED_NUMBER:
				textView.setText(R.string.custom_blocked_number);
				break;
			}
			return true;

		}

		return false;
	}

}
