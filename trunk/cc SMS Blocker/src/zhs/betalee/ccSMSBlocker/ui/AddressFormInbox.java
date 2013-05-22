package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class AddressFormInbox extends InboxRead{

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		cursor.moveToPosition(position);
		final Context mContext=v.getContext();
		final DbAdapter mDbAdapter = new DbAdapter(mContext);

		if (mDbAdapter.createOne(cursor.getString(1).replaceAll("\\+86", ""), 0) == -2) {
			Toast.makeText(getApplication(), "已存在相同规则", Toast.LENGTH_SHORT).show();
		}

		new AlertDialog.Builder(mContext)
		.setTitle("支持关键词补全计划")//设置标题
		.setMessage("反馈无法拦截的短信")//设置提示消息
		.setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定的按键
			public void onClick(DialogInterface dialog, int which) {
				//do something 1
				String tempAddString=cursor.getString(1);
				String tempMSGString=cursor.getString(2);
				MessageUtils.sendBlockMessageToMe(mContext, tempAddString +"] "+
						tempMSGString );
				finish();
				//do something 1
			}
		})
		.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置按键
			public void onClick(DialogInterface dialog, int which) {
				//do something 1
				finish();
			}
		})
		.show();


	}

}
