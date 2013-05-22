package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AllowedMsgListView extends Activity{
	private ListView listView;
	private DbAdapter mDbAdapter;
	private Cursor mAllowMsgCursor;
	private SimpleCursorAdapter allowMsgSCAdapter;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_list_item);
		
		
		listView = (ListView) findViewById(android.R.id.list);
		TextView tvEmpty=(TextView)findViewById(android.R.id.empty);

		listView.setEmptyView(tvEmpty);

        mDbAdapter = new DbAdapter(getApplicationContext());

		createAllowMsgView();
		listView.setOnItemClickListener(new AllowedMsgListViewItemClickListener());
		super.onCreate(savedInstanceState);
	}
	
	
    private void refreshAllowMsgSCAdapter(){
    	mAllowMsgCursor=mDbAdapter.fetchAllowMsgAll();
		allowMsgSCAdapter.changeCursor(mAllowMsgCursor);
		allowMsgSCAdapter.notifyDataSetChanged();
    }
    
    private void createAllowMsgView(){
    	mAllowMsgCursor=mDbAdapter.fetchAllowMsgAll();
    	allowMsgSCAdapter=newCustomAllowMsgSimCurAdap();
    	allowMsgSCAdapter.setViewBinder(new CustomMsgViewBinder(getApplicationContext()));
    	
    	listView.setAdapter(allowMsgSCAdapter);

    }
    
    private SimpleCursorAdapter newCustomAllowMsgSimCurAdap(){
    	if (Build.VERSION.SDK_INT >= 11){
    		return new SimpleCursorAdapter(listView.getContext(), R.layout.blockedphone_list_item_2, mAllowMsgCursor, 
    				new String[] { "number",DbAdapter.FORMTIME, DbAdapter.ALLOWEDRULE}, 
    				new int[] { R.id.list_item_text1, R.id.list_item_text4,R.id.list_item_text3},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    	}
		return new SimpleCursorAdapter(listView.getContext(), R.layout.blockedphone_list_item_2, mAllowMsgCursor, new String[] { "number",DbAdapter.FORMTIME, DbAdapter.ALLOWEDRULE}, new int[] { R.id.list_item_text1, R.id.list_item_text4,R.id.list_item_text3});
//    	return new SimpleCursorAdapter(this, R.layout.blockedmsg_list_item_2, mAllowMsgCursor, new String[] { "number", "blockedrule","msgbody",DbAdapter.FORMTIME}, new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3,R.id.list_item_text4});
    }
	
    private class AllowedMsgListViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			// TODO Auto-generated method stub

			final long rowId=id;
			
			mAllowMsgCursor.moveToPosition(position);
			//更新已读
			int status=mAllowMsgCursor.getInt(4) | 1;
			mDbAdapter.updateStatus(DbAdapter.ALLOW_MESSAGES_DB_TABLE,rowId, status);
			//
			handler.sendEmptyMessage(0);
//			refreshBlockedmsgSCAdapter();
			final Context mContext=arg1.getContext();
			//

			new AlertDialog.Builder(mContext)
			.setTitle(mAllowMsgCursor.getString(1))//设置标题
			.setMessage(mAllowMsgCursor.getString(3))//设置提示消息
			.setPositiveButton("删除",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					mDbAdapter.deleteOne(DbAdapter.ALLOW_MESSAGES_DB_TABLE,rowId);
//					refreshBlockedmsgView();
					refreshAllowMsgSCAdapter();
				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();
			
		}
    	
    	
    }
    
    //定义Handler对象
    final Handler handler =new Handler(){
      	@Override
      	public void handleMessage(Message msg){
      		switch (msg.what) {   
      		case 0:   
      			refreshAllowMsgSCAdapter();
      			break;
      		default:
      			break;
      		}
      	}
      };

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mAllowMsgCursor.close();
		mDbAdapter=null;
		super.onDestroy();
	}
}
