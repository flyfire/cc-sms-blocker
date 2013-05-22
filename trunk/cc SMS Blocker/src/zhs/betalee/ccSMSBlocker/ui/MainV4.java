package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.database.UpdataVerDatabase;
import zhs.betalee.ccSMSBlocker.ui.sms.SmsBlockedLog;
import zhs.betalee.ccSMSBlocker.ui.sms.SmsRulesListView;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainV4 extends Activity{

	private ProgressDialog builtIndialog;
//	private DbAdapter mDbAdapter;



	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.main_v4);

		LinearLayout btn_smsblockedlog = (LinearLayout)findViewById(R.id.main4_btn_smsblockedlog);
		LinearLayout btn_smsrules = (LinearLayout)findViewById(R.id.main4_btn_smsrules);
		
		LinearLayout btn_settings = (LinearLayout)findViewById(R.id.main4_btn_settings);
		LinearLayout btn_help = (LinearLayout)findViewById(R.id.main4_btn_help);
		
		btn_smsblockedlog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), SmsBlockedLog.class));
			}
		});
		btn_smsrules.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), SmsRulesListView.class));
			}
		});
		
		btn_settings.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), Settings.class));
			}
		});
		btn_help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), RulesHelp.class));
			}
		});
//		================================================
		DbAdapter mDbAdapter = new DbAdapter(getApplicationContext());
        
        Cursor c = mDbAdapter.fetchBlockedMSGAll();
        
        TextView textView=(TextView)findViewById(R.id.mainv4_TextView);
        textView.setText("为您拦截了短信："+(c.moveToFirst()?c.getLong(0):0)+"条");
        c.close();c=null;


		long weektime=System.currentTimeMillis()-604800000;
		mDbAdapter.deleteAll(DbAdapter.ALLOW_MESSAGES_DB_TABLE, "timestamp <'"+weektime+"'");
		mDbAdapter=null;
		MessageUtils.writeUnreadCountSharedPreferences(getApplicationContext(),0);
		try {

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(R.string.app_name);
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (!MessageUtils.readAppVerSharedPreferences(getApplicationContext()).equals(getString(R.string.app_ver))) {

			int appVer=Integer.parseInt(MessageUtils.readAppVerSharedPreferences(getApplicationContext()).replaceAll("\\D", ""));
			System.out.println(appVer);
			if (appVer >= 30 && appVer <= 33) {
				mDbAdapter = new DbAdapter(getApplicationContext());

				mDbAdapter.deleteAll(DbAdapter.DB_TABLE, "rule='订车' or rule='订票' or rule='机票'");

			}
			if (appVer > 22 && appVer <= 36) {
				mDbAdapter = new DbAdapter(getApplicationContext());

				mDbAdapter.createOne(".*(http://|www|wap)[\\./#$%+-_?=&a-z\\d]+.*", Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP);

			}
			if (appVer<30) {
//		        Toast.makeText(getApplication(),
//						"正在初始化内置数据库...",Toast.LENGTH_LONG).show();
				
		        builtIndialog=ProgressDialog.show(this, // context 
		        	    "", // title 
		        	    "正在初始化内置数据库...", // message 
		        	    true);
		        
		        UpdataVerDatabase updataVerDatabase=new UpdataVerDatabase(getApplicationContext(),mDbAdapter,handler);
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

		super.onCreate(savedInstanceState);
	}


    //定义Handler对象
  final Handler handler =new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		switch (msg.what) {   
    		case 0:   
    			//        			refreshBlockedmsgView();  
//    			refreshBlockedmsgSCAdapter();
    			break;
    		case 1:   
    			//        			refreshRulesView();
//    			refreshRulesSCAdaper();
    			break;
    		case 2:   
    			break;
    		case 3:
    			break;
    		case 4:
    			builtIndialog.dismiss();
//  			测试短信拦截
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

}
