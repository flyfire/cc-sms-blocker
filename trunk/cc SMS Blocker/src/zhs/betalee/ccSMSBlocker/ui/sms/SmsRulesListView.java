package zhs.betalee.ccSMSBlocker.ui.sms;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.ui.ChoiceWayAddRule;
import zhs.betalee.ccSMSBlocker.ui.CustomRulesViewBinder;
import zhs.betalee.ccSMSBlocker.ui.EditRules;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SmsRulesListView extends Activity{

	private ListView rules_view_listView;
	private DbAdapter mDbAdapter=null;
	private Cursor mRulesCursor;
	private SimpleCursorAdapter rulesSCAdaper;


	private final static int addMode=0;
	private  final static int editMode=1;

	private ProgressDialog builtIndialog;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.rules_view);
		
		mDbAdapter = new DbAdapter(getApplicationContext());

		
		TextView tvEmpty=(TextView)findViewById(android.R.id.empty);
		rules_view_listView = (ListView) findViewById(android.R.id.list);
		rules_view_listView.setEmptyView(tvEmpty);
		
		rules_view_listView.setOnItemClickListener(new rulesViewItemClickListener());
		final ImageButton addRuleButton=(ImageButton)findViewById(R.id.btn_add_rule);
		addRuleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), ChoiceWayAddRule.class));
			}
		});
        final ImageButton searchRuleButton=(ImageButton)findViewById(R.id.btn_search_rule);
        searchRuleButton.setOnClickListener(new OnClickListener() {
        	private EditText inpuText;
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
        		
        		new AlertDialog.Builder(v.getContext())
        	 	.setTitle("搜索规则")
        	 	.setView(inpuText=new EditText(v.getContext()))
        	 	.setPositiveButton("确定", new DialogInterface.OnClickListener() {//设置确定的按键
    				public void onClick(DialogInterface dialog, int which) {
    					//do something
    					Cursor cursor=mDbAdapter.fetchAllRulesType("rule='"+inpuText.getText().toString()+"'");
    					if (cursor.getCount()==0) {
    						Toast.makeText(getApplication(),
									"找不到规则",Toast.LENGTH_LONG).show();
						}else {
							cursor.moveToFirst();
							
							Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
				    		Bundle bundle=new Bundle();
				    		bundle.putString("inputEditText", cursor.getString(1));
				    		bundle.putInt("spinner", cursor.getInt(2));
				    		bundle.putLong("rowId", cursor.getLong(0));
				    		bundle.putInt("Mode", editMode);
				    		localIntent.putExtras(bundle);
				    		startActivityForResult(localIntent,editMode); 
						}
    				}
    			})
        	 	.setNegativeButton("取消", null)
        	 	.show();
        	}
        });

		
		
		
		
		super.onCreate(savedInstanceState);
	}
    
    private void refreshRulesSCAdaper(){
//    	mRulesCursor = mDbAdapter.fetchAllRulesType("type=0 or type=1 or type=2 or type=4 or type=5 or type=6");
    	mRulesCursor = mDbAdapter.fetchAllRulesType("type=0 or type=1 or type=2 or type=4 or type=5 or type=6 or type=8");
    	
    	rulesSCAdaper.changeCursor(mRulesCursor);
    	rulesSCAdaper.notifyDataSetChanged();
    }
    private void createRulesView(){

    	mRulesCursor = mDbAdapter.fetchAllRulesType("type=0 or type=1 or type=2 or type=4 or type=5 or type=6 or type=8");
    	
    	rulesSCAdaper=newCustomRulesSimCurAdap();
    	rulesSCAdaper.setViewBinder(new CustomRulesViewBinder());
    	
		rules_view_listView.setAdapter(rulesSCAdaper);

    }
    private SimpleCursorAdapter newCustomRulesSimCurAdap(){
    	if (Build.VERSION.SDK_INT >= 11){
    		return new SimpleCursorAdapter(rules_view_listView.getContext(), android.R.layout.simple_list_item_2, mRulesCursor, 
    				new String[] { "rule", "type"}, 
    				new int[] { android.R.id.text1, android.R.id.text2},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    	}
		return new SimpleCursorAdapter(rules_view_listView.getContext(), android.R.layout.simple_list_item_2, mRulesCursor, 
				new String[] { "rule", "type"}, 
				new int[] { android.R.id.text1, android.R.id.text2});
    	
    }
	
    private class rulesViewItemClickListener implements OnItemClickListener {

    	@Override
    	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
    		// TODO Auto-generated method stub

    		mRulesCursor.moveToPosition(position);

    		Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
    		Bundle bundle=new Bundle();
    		bundle.putString("inputEditText", mRulesCursor.getString(1));
    		bundle.putInt("spinner", mRulesCursor.getInt(2));
    		bundle.putLong("rowId", id);
    		bundle.putInt("Mode", editMode);
    		localIntent.putExtras(bundle);
    		startActivityForResult(localIntent,editMode); 

    	}

    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		mRulesCursor.close();
//		mDbAdapter.close();
//		mDbAdapter=null;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//释放数据库游标
//		mBlockedMsgCursor.close();
//		mRulesCursor.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//获取数据库游标
		refreshRulesSCAdaper();
		super.onResume();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		if (mDbAdapter==null) {
			mDbAdapter = new DbAdapter(getApplicationContext());

		}
		MessageUtils.writeUnreadCountSharedPreferences(getApplicationContext(),0);
		createRulesView();
		super.onStart();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mRulesCursor.close();
	
		mDbAdapter=null;
		super.onStop();
	}


    
    
}
