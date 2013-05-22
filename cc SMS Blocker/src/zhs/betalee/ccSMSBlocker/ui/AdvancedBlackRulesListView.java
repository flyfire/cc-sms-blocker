package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.database.UpdataVerDatabase;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AdvancedBlackRulesListView extends Activity{
    private final static int addMode=0;
    private  final static int editMode=1;
	
	private DbAdapter mDbAdapter=null;
	private ListView rules_view_listView;
	private Cursor mRulesCursor;
	private SimpleCursorAdapter rulesSCAdaper;
	private SwipeDismissListViewTouchListener touchListener;
	private int mAction_Left;
	private int mAction_Right;
	private static Object lock = new Object();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rules_view);
		
		
		mDbAdapter = new DbAdapter(getApplicationContext());

        rules_view_listView = (ListView) findViewById(android.R.id.list);
		TextView tvEmpty=(TextView)findViewById(android.R.id.empty);
		rules_view_listView.setEmptyView(tvEmpty);
        rules_view_listView.setOnItemClickListener(new rulesViewItemClickListener());
        
        if (MessageUtils.readBooleanSharedPreferences(getApplicationContext(), SwipeSettings.SWIPE_ACTION_RULES)) {
			touchListener =new SwipeDismissListViewTouchListener(
					rules_view_listView,
					new SwipeDismissListViewTouchListener.OnDismissCallback() {
						@Override
						public void onDismiss(ListView listView, int[] reverseSortedPositions,boolean isSwipingRight) {

							for (int position : reverseSortedPositions) {
								if (isSwipingRight) {
									swipeAction(mAction_Right, position);
								}else {
									swipeAction(mAction_Left, position);
								}
							}
							//                            mAdapter.notifyDataSetChanged();
						}
					});
			rules_view_listView.setOnTouchListener(touchListener);
			// Setting this scroll listener is required to ensure that during ListView scrolling,
			// we don't look for swipes.
			rules_view_listView.setOnScrollListener(touchListener.makeScrollListener());
		}
        
        
        
        final ImageButton addRuleButton=(ImageButton)findViewById(R.id.btn_add_rule);
        addRuleButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		// TODO Auto-generated method stub
    			Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
    			Bundle bundle=new Bundle();
    			bundle.putInt("Mode", addMode);
    			localIntent.putExtras(bundle);
    			startActivityForResult(localIntent,addMode);
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
	}

	
    private void refreshRulesSCAdaper(){
    	mRulesCursor = mDbAdapter.fetchAllRulesType("type="+Constants.IN_BLOCKED_KEYWORD);
    	rulesSCAdaper.changeCursor(mRulesCursor);
    	rulesSCAdaper.notifyDataSetChanged();
    }
    private void createRulesView(){

    	mRulesCursor = mDbAdapter.fetchAllRulesType("type="+Constants.IN_BLOCKED_KEYWORD);
    	
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
    private void swipeAction(int mAction,int position){
    	if (mAction == 1) {
    		synchronized (lock) {
    			mRulesCursor.moveToPosition(position);
    			if (mDbAdapter.deleteOne(DbAdapter.DB_TABLE,mRulesCursor.getLong(0))) {
    				refreshRulesSCAdaper();

    				Toast.makeText(getApplication(),
    						"已删除",Toast.LENGTH_SHORT).show();
    			}
    		}
    	}else {
    		synchronized (lock) {
        		mRulesCursor.moveToPosition(position);

        		Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
        		Bundle bundle=new Bundle();
        		bundle.putString("inputEditText", mRulesCursor.getString(1));
        		bundle.putInt("spinner", mRulesCursor.getInt(2));
        		bundle.putLong("rowId", mRulesCursor.getLong(0));
        		bundle.putInt("Mode", editMode);
        		localIntent.putExtras(bundle);
        		startActivityForResult(localIntent,editMode); 
    		}
    	}
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		mRulesCursor.close();
//		mDbAdapter.close();
//		mDbAdapter=null;
		super.onDestroy();
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
		createRulesView();
		 if (touchListener != null) {
			 mAction_Left=MessageUtils.readIntSharedPreferences(getApplicationContext(), SwipeSettings.SWIPE_ACTION_RULES_LEFT);
			 mAction_Right=MessageUtils.readIntSharedPreferences(getApplicationContext(), SwipeSettings.SWIPE_ACTION_RULES_RIGHT);
			 touchListener.setmSwipingLeftColor(mAction_Left==0?0x9999CC00:0x99ff4444);
			 touchListener.setmSwipingRightColor(mAction_Right==0?0x9999CC00:0x99ff4444);
		}
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		refreshRulesSCAdaper();
		super.onResume();
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
