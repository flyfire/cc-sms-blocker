package zhs.betalee.ccSMSBlocker.ui;


import java.util.Locale;
import java.util.regex.Pattern;

import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.database.UpdataVerDatabase;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import zhs.betalee.ccSMSBlocker.util.RecoverAllMsgThread;
import zhs.betalee.ccSMSBlocker.util.SmsManager;
import zhs.betalee.ccSMSBlocker.R;

import com.github.ysamlan.horizontalpager.HorizontalPager;

public class Main extends Activity {

//	private ViewFlow viewFlow;
	private ListView blockedmsg_view_listView;
	private ListView rules_view_listView;
	private DbAdapter mDbAdapter;
	private Cursor mRulesCursor;
	private Cursor mBlockedMsgCursor;
	private SimpleCursorAdapter blockedmsgSCAdapter;
	private SimpleCursorAdapter rulesSCAdaper;
	
	
	private HorizontalPager mPager;
//	private Button btnNotifyAd;
	private Button btnBlockedMsg;
    private Button btnRules;
    private View viewLeftclor;
//    private View viewMiddleclor;
    private View viewRightcolor;
	
    
    private final static int addMode=0;
    private  final static int editMode=1;

    private ProgressDialog builtIndialog;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTitle(R.string.diff_title);
        setContentView(R.layout.main);

        if ((System.currentTimeMillis()&2048)==0) {
        	//			ff008270
        	//        	3aaa98
        	if ((System.currentTimeMillis()&1024)==0) {
        		View leftcolor=(View)findViewById(R.id.leftcolor);
        		View rightcolor=(View)findViewById(R.id.rightcolor);
        		View linecolor=(View)findViewById(R.id.linecolor);
        		leftcolor.setBackgroundColor(0xff33B5E5);
        		rightcolor.setBackgroundColor(0xff33B5E5);
        		linecolor.setBackgroundColor(0xff33B5E5);
        	}else {
        		View leftcolor=(View)findViewById(R.id.leftcolor);
        		View rightcolor=(View)findViewById(R.id.rightcolor);
        		View linecolor=(View)findViewById(R.id.linecolor);
        		leftcolor.setBackgroundColor(0xffFF8800);
        		rightcolor.setBackgroundColor(0xffFF8800);
        		linecolor.setBackgroundColor(0xffFF8800);
        	}
        }
        
        
        mPager = (HorizontalPager) findViewById(R.id.horizontal_pager);
        mPager.setOnScreenSwitchListener(onScreenSwitchListener);
        btnBlockedMsg = (Button) findViewById(R.id.BtnBlockedMsg);
        btnRules = (Button) findViewById(R.id.BtnRules);
        viewLeftclor=(View)findViewById(R.id.leftcolor);
        viewRightcolor=(View)findViewById(R.id.rightcolor);
        
        mPager.setCurrentScreen(0, false);
        
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

        mDbAdapter = new DbAdapter(getApplicationContext());
        
        
        
        /** To populate ListView in BlockedMSG_VIEW.xml */

		final Button delAllMsgButton=(Button)findViewById(R.id.btn_del_all);
		final Button readAllMsgButton=(Button)findViewById(R.id.btn_read_all);
		final Button recoverAllMsgButton=(Button)findViewById(R.id.btn_recover_all);
		delAllMsgButton.setOnClickListener(new delAllMsgButtonOnClickListener());
		readAllMsgButton.setOnClickListener(new readAllMsgButtonOnClickListener());
		recoverAllMsgButton.setOnClickListener(new recoverAllMsgButtonOnClickListener());
		
		
		
        /** To populate ListView in BlockedMSG_VIEW.xml */

        
		/** To populate ListView in RULE_VIEW.xml */
		
		rules_view_listView = (ListView) findViewById(R.id.ruleslist);
		blockedmsg_view_listView = (ListView) findViewById(R.id.blockedmsglist);
		
//		refreshBlockedmsgView(0);
//		refreshRulesView();
		TextView tvEmpty=(TextView)findViewById(R.id.rules_empty);
		rules_view_listView.setEmptyView(tvEmpty);
		TextView tvmEmpty=(TextView)findViewById(R.id.msg_empty);
		blockedmsg_view_listView.setEmptyView(tvmEmpty);
		
		rules_view_listView.setOnItemClickListener(new rulesViewItemClickListener());
		blockedmsg_view_listView.setOnItemClickListener(new blockedmsgViewItemClickListener());
		// Create an ArrayAdapter, that will actually make the Strings above
		// appear in the ListView

		/////////////////////////
		final Button addRuleButton=(Button)findViewById(R.id.btn_add_rule);
		addRuleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
//				Bundle bundle=new Bundle();
//				bundle.putInt("Mode", addMode);
//				localIntent.putExtras(bundle);
//				startActivityForResult(localIntent,addMode);
				startActivity(new Intent(getApplicationContext(), ChoiceWayAddRule.class));
			}
		});

		/** To populate ListView in RULE_VIEW.xml */
		

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(R.string.app_name);
		
		
//      mPager.setCurrentScreen(1, false);

		if (!MessageUtils.readAppVerSharedPreferences(getApplicationContext()).equals(getString(R.string.app_ver))) {
			
			createBlockedmsgView();
			createRulesView();
			
			int appVer=Integer.parseInt(MessageUtils.readAppVerSharedPreferences(getApplicationContext()).replaceAll("\\D", ""));
			System.out.println(appVer);
			if (appVer >= 30 && appVer <= 33) {
				mDbAdapter.deleteAll(DbAdapter.DB_TABLE, "rule='订车' or rule='订票' or rule='机票'");
			}
			if (appVer > 22 && appVer <= 36) {
				mDbAdapter.createOne(".*(http://|www|wap)[\\./#$%+-_?=&a-z\\d]+.*", Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP);
			}
			if (appVer<30) {
//		        Toast.makeText(getApplication(),
//						"正在初始化内置数据库...",Toast.LENGTH_LONG).show();
				
		        builtIndialog=ProgressDialog.show(this, // context 
		        	    "", // title 
		        	    "正在初始化内置数据库...", // message 
		        	    true);
		        
		        UpdataVerDatabase updataVerDatabase=new UpdataVerDatabase(getApplicationContext(), mDbAdapter,handler);
		        updataVerDatabase.start();
		        
			}else {
				//			测试短信拦截
				SharedPreferences  mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				if (mSharedPreferences.getInt("TestSMSBlocker",0) == 0) {
					startActivity(new Intent(getApplicationContext(), TestSMSBlocker.class));
				}
			}
			MessageUtils.writeAppVerSharedPreferences(getApplicationContext(),getString(R.string.app_ver));
	        Toast.makeText(getApplication(),
	        		"安装更新后请手动 重启手机 一次！",Toast.LENGTH_LONG).show();
	        Toast.makeText(getApplication(),
	    			"安装更新后请手动 重启手机 一次！",Toast.LENGTH_LONG).show();
		}
		
		long weektime=System.currentTimeMillis()-604800000;
		mDbAdapter.deleteAll(DbAdapter.BLOCKED_PHONE_DB_TABLE, "timestamp <'"+weektime+"'");
		mDbAdapter.deleteAll(DbAdapter.ALLOW_MESSAGES_DB_TABLE, "timestamp <'"+weektime+"'");
		
    }
    

    private void refreshBlockedmsgSCAdapter(){
    	mBlockedMsgCursor=mDbAdapter.fetchBlockedMSGAll();
		blockedmsgSCAdapter.changeCursor(mBlockedMsgCursor);
		blockedmsgSCAdapter.notifyDataSetChanged();
    }
    private void createBlockedmsgView(){

    	mBlockedMsgCursor=mDbAdapter.fetchBlockedMSGAll();
    	blockedmsgSCAdapter=newCustomBlockedMsgSimCurAdap();
    	blockedmsgSCAdapter.setViewBinder(new CustomMsgViewBinder(getApplicationContext()));
    	
		blockedmsg_view_listView.setAdapter(blockedmsgSCAdapter);
//		blockedmsgSCAdapter.notifyDataSetChanged();
    }
    
    private SimpleCursorAdapter newCustomBlockedMsgSimCurAdap(){
    	if (Build.VERSION.SDK_INT >= 11){
    		return new SimpleCursorAdapter(blockedmsg_view_listView.getContext(), R.layout.blockedmsg_list_item_2, mBlockedMsgCursor, 
    				new String[] { "number", "blockedrule","msgbody",DbAdapter.FORMTIME}, 
    				new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3,R.id.list_item_text4},CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    	}
		return new SimpleCursorAdapter(blockedmsg_view_listView.getContext(), R.layout.blockedmsg_list_item_2, mBlockedMsgCursor, new String[] { "number", "blockedrule","msgbody",DbAdapter.FORMTIME}, new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3,R.id.list_item_text4});
//    	return new SimpleCursorAdapter(this, R.layout.blockedmsg_list_item_2, mBlockedMsgCursor, new String[] { "number", "blockedrule","msgbody",DbAdapter.FORMTIME}, new int[] { R.id.list_item_text1, R.id.list_item_text2,R.id.list_item_text3,R.id.list_item_text4});
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
    
    private class blockedmsgViewItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
			// TODO Auto-generated method stub
			
//			TextView numberTextView=(TextView)arg1.findViewById(R.id.list_item_text1);
//			numberTextView.setTextColor(0xff000000);


			final long rowId=id;
			final int thePosition=position;
			mBlockedMsgCursor.moveToPosition(position);
			//更新已读
			int status=mBlockedMsgCursor.getInt(5) | 1;
			mDbAdapter.updateMSGStatus(rowId, status);
			//
			handler.sendEmptyMessage(0);
//			refreshBlockedmsgSCAdapter();
			final Context mContext=arg1.getContext();
			//

			new AlertDialog.Builder(mContext)
			.setTitle(mBlockedMsgCursor.getString(1))//设置标题
			.setMessage(mBlockedMsgCursor.getString(2))//设置提示消息
			.setPositiveButton("删除",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					mDbAdapter.deleteOne(DbAdapter.BLOCKED_MESSAGES_DB_TABLE,rowId);
//					refreshBlockedmsgView();
					refreshBlockedmsgSCAdapter();
				}
			})
			.setNeutralButton("反馈",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					mBlockedMsgCursor.moveToPosition(thePosition);
					String tempAddString=mBlockedMsgCursor.getString(1);
					String tempMSGString=mBlockedMsgCursor.getString(2);
					String tempRuleString=mBlockedMsgCursor.getString(4);
					MessageUtils.sendBlockMessageToMe(mContext, tempAddString +"] "+
							tempMSGString + tempRuleString );
				}
			})
			.setNegativeButton("恢复",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					synchronized (this) {
						mBlockedMsgCursor.moveToPosition(thePosition);
						String tempAddString=mBlockedMsgCursor.getString(1);
						String tempMSGString=mBlockedMsgCursor.getString(2);
						long tempTimeString=mBlockedMsgCursor.getLong(3);	
						if (SmsManager.createSms(getApplicationContext(), tempAddString, tempMSGString, tempTimeString)) {
							mDbAdapter.deleteOne(DbAdapter.BLOCKED_MESSAGES_DB_TABLE,rowId);
							Toast.makeText(getApplication(),
									"已恢复短信到收件箱",Toast.LENGTH_LONG).show();
							//						refreshBlockedmsgView();
							refreshBlockedmsgSCAdapter();
						}
					}
				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();
			
		}
    	
    	
    }
    
    private class delAllMsgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final Context mContext=v.getContext();
			new AlertDialog.Builder(mContext)
			.setTitle("删除全部已读短信(黑色)")//设置标题
			.setMessage("删除已读短信?")//设置提示消息
			.setPositiveButton("删除",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something 1
							mDbAdapter.deleteAll(DbAdapter.BLOCKED_MESSAGES_DB_TABLE,DbAdapter.STATUS + "= 1");
//							refreshBlockedmsgView();
							refreshBlockedmsgSCAdapter();
							Toast.makeText(getApplication(),
									"已删除短信",Toast.LENGTH_SHORT).show();
					//do something 1
				}
			})
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something 1
				}
			})
			.show();
		}
    	
    }
    
    private class readAllMsgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			new AlertDialog.Builder(v.getContext())
			.setTitle("警告")//设置标题
			.setMessage("是否标记全部短信为已读?")//设置提示消息
			.setPositiveButton("确定",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					//更新已读
					mDbAdapter.updateMSGStatus(1, DbAdapter.STATUS + "= 0");
//					refreshBlockedmsgView();
					refreshBlockedmsgSCAdapter();
				}
			})
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something
					return;
				}
			})
			.show();

		}
    	
    }
    
    private class recoverAllMsgButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
				
			final Context mContext=v.getContext();
			new AlertDialog.Builder(mContext)
			.setTitle("警告")//设置标题
			.setMessage("是否批量恢复短信到收件箱?")//设置提示消息
			.setPositiveButton("分类恢复",new DialogInterface.OnClickListener() {//设置确定的按键
				public void onClick(DialogInterface dialog, int which) {
					//do something 1
					////////////////////////////////
					new AlertDialog.Builder(mContext)
					.setTitle("警告")//设置标题
					.setMessage("恢复哪类过滤短信?")//设置提示消息
					.setPositiveButton("已读(黑色)",new DialogInterface.OnClickListener() {//设置确定的按键
						public void onClick(DialogInterface dialog, int which) {
							//do something 2
							
							//已读 == 1
							RecoverAllMsgThread recoverAllMsgThread = new RecoverAllMsgThread(getApplicationContext(), mDbAdapter,1,handler);
							recoverAllMsgThread.start();
							builtIndialog=ProgressDialog.show(mContext, // context 
					        	    "", // title 
					        	    "正在…恢复短信到收件箱...", // message 
					        	    true);
//							Toast.makeText(getApplication(),
//									"正在…恢复短信到收件箱",Toast.LENGTH_SHORT).show();
						}
					})
					.setNegativeButton("未读(橙色)",new DialogInterface.OnClickListener() {//设置按键
						public void onClick(DialogInterface dialog, int which) {
							//do something 2
							//未读 == 0
							
							RecoverAllMsgThread recoverAllMsgThread = new RecoverAllMsgThread(getApplicationContext(), mDbAdapter,0,handler);
							recoverAllMsgThread.start();
							builtIndialog=ProgressDialog.show(mContext, // context 
					        	    "", // title 
					        	    "正在…恢复短信到收件箱...", // message 
					        	    true);
//							Toast.makeText(getApplication(),
//									"正在…恢复短信到收件箱",Toast.LENGTH_SHORT).show();
							
						}
					})
					.show();
				/////////////////////////
				//do something 1
				}
			})
			.setNegativeButton("取消",new DialogInterface.OnClickListener() {//设置按键
				public void onClick(DialogInterface dialog, int which) {
					//do something 1
					return;
				}
			})
			//     .setCancelable(false)//设置按返回键是否响应返回，这是是不响应
			.show();
			
		}
    }
    

//    @Override
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		if (resultCode != RESULT_OK) {
//			return;
//		}
//		if (requestCode == editMode||requestCode == addMode) {
//			if (resultCode == RESULT_OK) {
//				refreshRulesSCAdaper();
//			}
//		}
//	}
    

    
 

    private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener =
        new HorizontalPager.OnScreenSwitchListener() {
            @Override
            public void onScreenSwitched(final int screen) {
                // Check the appropriate button when the user swipes screens.
//            	setHeaderLocation();
            	switch (screen) {
                    case 0:
                    	btnRules.setTextColor(0xFF999999);
                    	viewRightcolor.setVisibility(4);
                    	btnBlockedMsg.setTextColor(0xFFffffff);
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
//        private void setHeaderLocation() {
//            final View pager = findViewById(R.id.horizontal_pager);
//            final View homeNav = findViewById(R.id.HomeNav);
//            final int btnWidth = btnBlockedMsg.getMeasuredWidth();
//            final MarginLayoutParams params = new MarginLayoutParams(homeNav.getLayoutParams());
//            params.setMargins(
//                    (pager.getMeasuredWidth() - btnWidth) / 2 - pager.getScrollX() * btnWidth / pager.getMeasuredWidth(),
//                    0, 0, 0);
//            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(params);
//            homeNav.setLayoutParams(layoutParams);
//        }

        //定义Handler对象
      final Handler handler =new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		switch (msg.what) {   
        		case 0:   
        			//        			refreshBlockedmsgView();  
        			refreshBlockedmsgSCAdapter();
        			break;
        		case 1:   
        			//        			refreshRulesView();
        			refreshRulesSCAdaper();
        			break;
        		case 2:   
        			builtIndialog.dismiss();
        			Toast.makeText(getApplication(),
        					"恢复短信成功",Toast.LENGTH_LONG).show();
        			refreshBlockedmsgSCAdapter();
        			break;
        		case 3:
        			builtIndialog.dismiss();
        			Toast.makeText(getApplication(),
        					"恢复短信失败",Toast.LENGTH_LONG).show();
        			break;
        		case 4:
        			builtIndialog.dismiss();
//      			测试短信拦截
        			SharedPreferences  mSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        			if (mSharedPreferences.getInt("TestSMSBlocker",0) == 0) {
        				startActivity(new Intent(getApplicationContext(), TestSMSBlocker.class));
        			}
        			break;
        		default:
        			break;
        		}
        	}
        };

        

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			startActivity(new Intent(getApplicationContext(), Settings.class));
		}
		return super.onKeyDown(keyCode, event);
	}
	
	//按下键盘上返回按钮 
	private long exitTime = 0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if((System.currentTimeMillis() - exitTime) > 1200){
			Toast.makeText(getApplication(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		}else{
			finish();
		}

//		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mBlockedMsgCursor.close();
		mRulesCursor.close();
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
//		MessageUtils.writeUnreadCountSharedPreferences(this, 0);
//		createBlockedmsgView();
//		createRulesView();
		refreshBlockedmsgSCAdapter();
		refreshRulesSCAdaper();
		super.onResume();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		MessageUtils.writeUnreadCountSharedPreferences(getApplicationContext(),0);
		createBlockedmsgView();
		createRulesView();
		super.onStart();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mBlockedMsgCursor.close();
		mRulesCursor.close();
		super.onStop();
	}


}
