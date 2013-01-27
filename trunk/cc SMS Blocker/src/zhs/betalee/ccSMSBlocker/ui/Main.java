package zhs.betalee.ccSMSBlocker.ui;


import java.util.regex.Pattern;

import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import zhs.betalee.ccSMSBlocker.util.SmsManager;
import zhs.betalee.ccSMSBlocker.R;

import com.github.ysamlan.horizontalpager.HorizontalPager;

public class Main extends Activity {

	private ListView blockedmsg_view_listView;
	private ListView rules_view_listView;
	private DbAdapter mDbAdapter;
	private Cursor mRulesCursor;
	private Cursor mBlockedMsgCursor;
	
	private HorizontalPager mPager;
	private Button btnBlockedMsg;
    private Button btnRules;
    private View viewLeftclor;
    private View viewRightcolor;
	
    private EditText inputEditText;
    private TextView inputTipsText;
    private Spinner spinner;
	
    private String inputRuleString;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle(R.string.diff_title);
        setContentView(R.layout.main);
        
        mPager = (HorizontalPager) findViewById(R.id.horizontal_pager);
        mPager.setOnScreenSwitchListener(onScreenSwitchListener);
        btnBlockedMsg = (Button) findViewById(R.id.BtnBlockedMsg);
        btnRules = (Button) findViewById(R.id.BtnRules);
        viewLeftclor=(View)findViewById(R.id.leftcolor);
        viewRightcolor=(View)findViewById(R.id.rightcolor);
        
        mPager.setCurrentScreen(0, true);
        
        btnBlockedMsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPager.setCurrentScreen(0, true);
			}
		});
        btnRules.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPager.setCurrentScreen(1, true);
			}
		});
        
        mDbAdapter = new DbAdapter(this);
        mDbAdapter.open();
        
        
        
        /** To populate ListView in BlockedMSG_VIEW.xml */

        
        /** To populate ListView in BlockedMSG_VIEW.xml */
        
        
		/** To populate ListView in RULE_VIEW.xml */
		
		rules_view_listView = (ListView) findViewById(R.id.ruleslist);
		blockedmsg_view_listView = (ListView) findViewById(R.id.blockedmsglist);
		getCursorDatabase();
//		refreshRulesView();
		rules_view_listView.setOnItemClickListener(new rulesViewClickListener());
		blockedmsg_view_listView.setOnItemClickListener(new blockedmsgViewClickListener());
		// Create an ArrayAdapter, that will actually make the Strings above
		// appear in the ListView
		
		

		final Button addRuleButton=(Button)findViewById(R.id.btn_add_rule);
		addRuleButton.setOnClickListener(new OnClickListener() { 
  	        public void onClick(View v) { 
  	        	LayoutInflater inflater = getLayoutInflater();
  	        	View layout = inflater.inflate(R.layout.add_rule_dialog,
  	        			(ViewGroup) findViewById(R.id.dialog));
  	        	inputEditText=(EditText)layout.findViewById(R.id.inputTxtKeyWord);
  	        	inputTipsText=(TextView)layout.findViewById(R.id.lblRuleTypeTipe);
  	        	ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
  	  				getApplicationContext(), R.array.rule_type, android.R.layout.simple_spinner_item);
  	        	spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
  	        	spinner = (Spinner) layout.findViewById(R.id.spinRuleType);
                spinner.setAdapter(spinnerAdapter);
        		spinner.setOnItemSelectedListener(new typeSelectedListener());
                
             	new AlertDialog.Builder(Main.this).setTitle("添加规则").setView(layout)
  	        	.setPositiveButton("确定", new DialogInterface.OnClickListener() { 
  	        		public void onClick(DialogInterface dialog, int id) { 
  	        			// TODO
  	        			int intRuleType= spinner.getSelectedItemPosition();
  	        			addRuleToDatabaes(intRuleType);
  	        			refreshRulesView();
  	        		} 
  	        	}) 
  	        	.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
  	        		public void onClick(DialogInterface dialog, int id) { 
  	        			dialog.cancel(); 
  	        		} 
  	        	}).create().show();

             	
  	        	
  	        } 
  	    });

		/** To populate ListView in RULE_VIEW.xml */
		

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
    }
    
    private void getCursorDatabase(){
    	mBlockedMsgCursor=mDbAdapter.fetchBlockedMSGAll();
        mRulesCursor = mDbAdapter.fetchRulesAll();	
    }
    
    private void refreshBlockedmsgView(){
    	if (mBlockedMsgCursor.isClosed()) {
    		getCursorDatabase();
		}
    	mBlockedMsgCursor.requery();
		blockedmsg_view_listView.setAdapter(new CustomBlockedMsgSimCurAdap(this, R.layout.blockedmsg_list_item_2, mBlockedMsgCursor, new String[] { "number", "blockedrule","msgbody"}, new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3}));
		
    }
    
    private void refreshRulesView(){
    	if (mRulesCursor.isClosed()) {
    		getCursorDatabase();
		}
        mRulesCursor.requery();
		rules_view_listView.setAdapter(new CustomSimpleCursorAdapter(this, android.R.layout.simple_list_item_2, mRulesCursor, new String[] { "rule", "type"}, new int[] { android.R.id.text1, android.R.id.text2}));
		blockedmsg_view_listView.setAdapter(new CustomBlockedMsgSimCurAdap(this, R.layout.blockedmsg_list_item_2, mBlockedMsgCursor, new String[] { "number", "blockedrule","msgbody"}, new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3}));
			
    }
    
    private class rulesViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			// TODO Auto-generated method stub
			final long rowId=id;
			mRulesCursor.moveToPosition(position);

			new AlertDialog.Builder(Main.this)
			.setTitle("	是否删除规则?")//设置标题
			.setMessage(mRulesCursor.getString(1))//设置提示消息
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					mDbAdapter.deleteOne("rules",rowId);
					refreshRulesView();
				}
			})
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置取消按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();
		}
    	
    	
    }
    private class blockedmsgViewClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			// TODO Auto-generated method stub
			final long rowId=id;
			mBlockedMsgCursor.moveToPosition(position);
			//更新已读
			int status=mBlockedMsgCursor.getInt(5) | 1;
			mDbAdapter.updateMSGStatus(rowId, status);

			
			new AlertDialog.Builder(Main.this)
			.setTitle(mBlockedMsgCursor.getString(1))//设置标题
			.setMessage(mBlockedMsgCursor.getString(2))//设置提示消息
			.setPositiveButton("删除",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					mDbAdapter.deleteOne("blockedmessages",rowId);
					refreshBlockedmsgView();
				}
			})
			.setNeutralButton("举报",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					MessageUtils.sendBlockMessageToMe(Main.this, mBlockedMsgCursor.getString(1)+"] "+
							mBlockedMsgCursor.getString(2)+" ["+mBlockedMsgCursor.getString(4)+"]");
				}
			})
			.setNegativeButton("恢复",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					if (SmsManager.createSms(Main.this, mBlockedMsgCursor.getString(1), mBlockedMsgCursor.getString(2), mBlockedMsgCursor.getLong(3))) {
						mDbAdapter.deleteOne("blockedmessages",rowId);
						refreshBlockedmsgView();
					}

				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();

		}
    	
    	
    }
    
    private void addRuleToDatabaes(int type)
    {
        
    	switch (type) {
		case Constants.TYPE_BLOCKED_NUMBER:
			inputRuleString=inputEditText.getText().toString().replaceAll("[^\\d?*]", "");

			break;
		case Constants.TYPE_TRUSTED_NUMBER:
			inputRuleString=inputEditText.getText().toString()
					.replaceAll("[^\\d?*]", "");

			break;
		case Constants.TYPE_BLOCKED_BEGINNING_OF_NUMBER:
			inputRuleString=inputEditText.getText().toString().replaceAll("\\D", "");
			if (inputRuleString.length()<4) {
				Toast.makeText(Main.this,
						"不能少于4位",Toast.LENGTH_LONG).show();
//				inputrule.delete(0, inputrule.length());
				return;
			}
			inputRuleString += "*";
			break;
		case Constants.TYPE_BLOCKED_KEYWORD:
			inputRuleString=inputEditText.getText().toString().replaceAll("[\\p{P}\\p{S}]", "");

			break;
		case Constants.TYPE_TRUSTED_KEYWORD:
			inputRuleString=inputEditText.getText().toString().replaceAll("[\\p{P}\\p{S}]", "");
			if (inputRuleString.indexOf("*")!=0 && inputRuleString.length() > 0) {
				inputRuleString = "*"+inputRuleString;
			}
			if (inputRuleString.lastIndexOf("*")!=inputRuleString.length()-1 && inputRuleString.length() > 1) {
				inputRuleString += "*";
			}

			break;
		case Constants.TYPE_BLOCKED_COUNT_NUMBER:
			inputRuleString=inputEditText.getText().toString().replaceAll("\\D", "");
			if (inputRuleString.equals("11")) {
				Toast.makeText(Main.this,
						"不能拦截11位",Toast.LENGTH_LONG).show();
				return;
			}
			break;
		case Constants.TYPE_BLOCKED_KEYWORD_REGEXP:
			inputRuleString=inputEditText.getText().toString();
			break;	
		}
    		
		if (type == Constants.TYPE_BLOCKED_KEYWORD_REGEXP || type == Constants.TYPE_BLOCKED_KEYWORD
				) {
			try {
				Pattern.compile(inputRuleString);
			} catch (RuntimeException e) {
				Toast.makeText(Main.this,
						getString(R.string.error_regexp),
						Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			String regexp = inputRuleString.replaceAll("\\?", ".").replaceAll("\\*",".*");
			try {
				Pattern.compile(regexp);
			} catch (RuntimeException e) {
				Toast.makeText(Main.this,
						getString(R.string.error_regexp),
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		if (inputRuleString.length() > 0) {			

	        mDbAdapter.createOne(inputRuleString, type);
	       
		}else {
//			inputrule.delete(0, inputrule.length());
			return;
		}     	
//		inputrule.delete(0, inputrule.length());
    }
    
    private class typeSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			switch (arg2) {
			case Constants.TYPE_BLOCKED_NUMBER:
				inputTipsText.setText(R.string.type_blocked_number_tips);
				break;
			case Constants.TYPE_TRUSTED_NUMBER:
				inputTipsText.setText(R.string.type_trusted_number_tips);
				break;	
			case Constants.TYPE_BLOCKED_BEGINNING_OF_NUMBER:
				inputTipsText.setText(R.string.type_blocked_beginning_of_number_tips);
				break;
			case Constants.TYPE_BLOCKED_KEYWORD:
				inputTipsText.setText(R.string.type_blocked_keyword_tips);
				break;
			case Constants.TYPE_TRUSTED_KEYWORD:
				inputTipsText.setText(R.string.type_trusted_keyword_tips);
				break;
			case Constants.TYPE_BLOCKED_COUNT_NUMBER:
				inputTipsText.setText(R.string.type_blocked_count_number_tips);
				break;
			case Constants.TYPE_BLOCKED_KEYWORD_REGEXP:
				inputTipsText.setText(R.string.type_blocked_keyword_regexp_tips);
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}		
	}
    
    private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener =
        new HorizontalPager.OnScreenSwitchListener() {
            @Override
            public void onScreenSwitched(final int screen) {
                // Check the appropriate button when the user swipes screens.
            	switch (screen) {
                    case 0:
                    	btnRules.setTextColor(0xFF999999);
                    	viewRightcolor.setVisibility(4);
                        btnBlockedMsg.setTextColor(0xFFFFFFFF);
                        viewLeftclor.setVisibility(0);
                        break;
                    case 1:
                    	btnBlockedMsg.setTextColor(0xFF999999);
                    	viewLeftclor.setVisibility(4);
                        btnRules.setTextColor(0xFFFFFFFF);
                        viewRightcolor.setVisibility(0);
                        break;
                    
                    default:
                        break;
                }
            }
        };

    
  //按下键盘上返回按钮 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Do something.
			new AlertDialog.Builder(this)
			.setTitle("	是否退出程序?")//设置标题
//			.setMessage("	退出?")//设置提示消息
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					finish();
				}
			})
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置取消按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();//显示
			return true;
		}else if (keyCode == KeyEvent.KEYCODE_MENU) {
			startActivity(new Intent(getApplicationContext(), Settings.class));
			 
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mBlockedMsgCursor.close();
		mRulesCursor.close();
		mDbAdapter.close();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//释放数据库游标
		mBlockedMsgCursor.close();
		mRulesCursor.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//获取数据库游标
		refreshBlockedmsgView();
		refreshRulesView();
		super.onResume();
	}
}
