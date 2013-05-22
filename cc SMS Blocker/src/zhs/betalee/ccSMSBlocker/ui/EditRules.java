package zhs.betalee.ccSMSBlocker.ui;

import java.util.regex.Pattern;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.database.Constants;
import zhs.betalee.ccSMSBlocker.database.DbAdapter;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditRules extends Activity{
	private DbAdapter mDbAdapter=null;
	

	private EditText inputEditText;
    private TextView inputTipsText;
    private Spinner spinner;

    private final static int addMode=0;
    private  final static int editMode=1;
//    private  final static int builtInMode=2;
    private int mode=0;
    
    private String inputRuleString;
    private long rowId=0;


	private String isEnabled;
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_rule_dialog);
		Context mContext=getApplicationContext();
		
		Bundle bundle=getIntent().getExtras();
		mode=bundle.getInt("Mode",0);
		
		mDbAdapter = new DbAdapter(mContext);

		inputEditText=(EditText)findViewById(R.id.inputTxtKeyWord);
		inputTipsText=(TextView)findViewById(R.id.lblRuleTypeTipe);
		ArrayAdapter<CharSequence> spinnerAdapter = null;
//		if (Build.VERSION.SDK_INT >= 14) {
//			spinnerAdapter = ArrayAdapter.createFromResource(
//					getApplicationContext(), R.array.rule_type, android.R.layout.simple_spinner_item);
//		}else {
			spinnerAdapter = ArrayAdapter.createFromResource(
					mContext, R.array.rule_type, android.R.layout.simple_spinner_item);
			spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		}

		
		spinner = (Spinner) findViewById(R.id.spinRuleType);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new typeSelectedListener());
		
		spinner.setSelection(MessageUtils.readIntSharedPreferences(mContext, "smsrulespinnerposition"));
		
		Button okButton=(Button)findViewById(R.id.edit_btn_ok);
		okButton.setOnClickListener(new addRuleButtonOnClickListener());
		
		Button cancelButton=(Button)findViewById(R.id.edit_btn_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		
		
		if (mode==editMode) {

			inputRuleString=bundle.getString("inputEditText");
			int spinnerSelection=bundle.getInt("spinner");
			rowId=bundle.getLong("rowId");
			
			
			Button delButton=(Button)findViewById(R.id.edit_btn_del);
			delButton.setVisibility(0);
			delButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDbAdapter.deleteOne("rules",rowId);
					setResult(RESULT_OK);
					finish();
				}
			});
			
			switch (spinnerSelection) {
			case Constants.CUSTOM_BLOCKED_NUMBER:
				spinnerSelection=0;
				break;
			case Constants.CUSTOM_TRUSTED_NUMBER:
				spinnerSelection=1;
				break;
			case Constants.IN_BLOCKED_KEYWORD:
				spinnerSelection=2;
				break;
			case Constants.CUSTOM_TRUSTED_KEYWORD:
				spinnerSelection=3;
				break;
			case Constants.CUSTOM_BLOCKED_COUNT_NUMBER:
				spinnerSelection=4;
				break;
			case Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP:
				spinnerSelection=5;
				break;
			case Constants.IN_TRUSTED_NUMBER:
				spinnerSelection=6;
				break;
			case Constants.CUSTOM_BLOCKED_KEYWORD:
				spinnerSelection=7;
				break;
			default:
				return;
			}
			
			
    		inputEditText.setText(inputRuleString);
    		spinner.setSelection(spinnerSelection);

		}


//==========================================================================
		
		
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		mDbAdapter.close();
//		mDbAdapter=null;
		super.onDestroy();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		if (mDbAdapter==null) {
			mDbAdapter = new DbAdapter(getApplicationContext());
		}
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		mDbAdapter=null;
		super.onStop();
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	





	private class addRuleButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int intRuleType= spinner.getSelectedItemPosition();
			
			MessageUtils.writeIntSharedPreferences(v.getContext(), "smsrulespinnerposition", intRuleType);
			
			switch (intRuleType) {
			case 0:
				intRuleType=Constants.CUSTOM_BLOCKED_NUMBER;
				break;
			case 1:
				intRuleType=Constants.CUSTOM_TRUSTED_NUMBER;
				break;
			case 2:
				intRuleType=Constants.IN_BLOCKED_KEYWORD;
				break;
			case 3:
				intRuleType=Constants.CUSTOM_TRUSTED_KEYWORD;
				break;
			case 4:
				intRuleType=Constants.CUSTOM_BLOCKED_COUNT_NUMBER;
				break;
			case 5:
				intRuleType=Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP;
				break;
			case 6:
				intRuleType=Constants.IN_TRUSTED_NUMBER;
				break;
			case 7:
				intRuleType=Constants.CUSTOM_BLOCKED_KEYWORD;
				break;
			default:
				break;
			}
			
			
			switch (mode) {
			case addMode:
				addRuleToDatabaes(intRuleType,addMode);
				break;
			case editMode:
				addRuleToDatabaes(intRuleType,editMode);
				break;
			default:
				break;
			}			
//			if (mode==addMode) {
//				addRuleToDatabaes(intRuleType,addMode);
//			}else {
//				addRuleToDatabaes(intRuleType,editMode);
//			}

			
			setResult(addMode);
			finish();
	
		}
	}
//	  
	   private void addRuleToDatabaes(int type,int mode)
	    {
	        
	    	switch (type) {
			case Constants.CUSTOM_BLOCKED_NUMBER:
				inputRuleString=inputEditText.getText().toString().replaceAll("[^\\d?*]", "");
				break;	
			case Constants.CUSTOM_TRUSTED_NUMBER:
				inputRuleString=inputEditText.getText().toString()
						.replaceAll("[^\\d?*]", "");
				break;
//			case Constants.MMS_TYPE_BLOCKED_NUMBER:	//彩信号码拦截=2
//				inputRuleString=inputEditText.getText().toString().replaceAll("[^\\d?*]", "");
//				break;	
//			case Constants.TYPE_BLOCKED_BEGINNING_OF_NUMBER:
//				inputRuleString=inputEditText.getText().toString().replaceAll("\\D", "");
//				if (inputRuleString.length()<4) {
//					Toast.makeText(getApplication(),
//							"不能少于4位",Toast.LENGTH_LONG).show();
////					inputrule.delete(0, inputrule.length());
//					return;
//				}
//				inputRuleString += "*";
//				break;
			case Constants.IN_BLOCKED_KEYWORD:
				inputRuleString=inputEditText.getText().toString().replaceAll("[\\p{P}\\p{S}\\s]", "");
				if (inputRuleString.matches("^[A-Za-z0-9]+$")) {
					StringBuilder inputRuleStringBuilder=new StringBuilder(inputRuleString);
					if (!inputRuleString.startsWith(".*")) {
						
						inputRuleStringBuilder.insert(0, ".*");
					}
					if (!inputRuleString.endsWith(".*")) {
						inputRuleStringBuilder.insert(inputRuleStringBuilder.length(), ".*");
					}
					inputRuleString=inputRuleStringBuilder.toString();
					type=Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP;
				}
				
				break;
			case Constants.CUSTOM_TRUSTED_KEYWORD:
				inputRuleString=inputEditText.getText().toString().replaceAll("[\\p{P}\\p{S}\\s]", "");
				if (inputRuleString.indexOf("*")!=0 && inputRuleString.length() > 0) {
					inputRuleString = "*"+inputRuleString;
				}
				if (inputRuleString.lastIndexOf("*")!=inputRuleString.length()-1 && inputRuleString.length() > 1) {
					inputRuleString += "*";
				}

				break;
			case Constants.CUSTOM_BLOCKED_COUNT_NUMBER:
				inputRuleString=inputEditText.getText().toString().replaceAll("\\D", "");
				if (inputRuleString.equals("11")) {
					Toast.makeText(getApplication(),
							"不能拦截11位",Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP:
				inputRuleString=inputEditText.getText().toString().replaceAll("\\s", "");
				StringBuilder inputRuleStringBuilder=new StringBuilder(inputRuleString);
				if (!inputRuleString.startsWith(".*")) {
					
					inputRuleStringBuilder.insert(0, ".*");
				}
				if (!inputRuleString.endsWith(".*")) {
					inputRuleStringBuilder.insert(inputRuleStringBuilder.length(), ".*");
				}
				if (inputRuleStringBuilder.length()>4) {
					inputRuleString=inputRuleStringBuilder.toString();
				}
				break;	
			case Constants.IN_TRUSTED_NUMBER:
				inputRuleString=inputEditText.getText().toString()
						.replaceAll("[^\\d?*]", "");
				break;
				
			case Constants.CUSTOM_BLOCKED_KEYWORD:
				inputRuleString=inputEditText.getText().toString().replaceAll("[\\p{P}\\p{S}\\s]", "");
				if (inputRuleString.matches("^[A-Za-z0-9]+$")) {
					StringBuilder inputRuleStrBuilder=new StringBuilder(inputRuleString);
					if (!inputRuleString.startsWith(".*")) {
						
						inputRuleStrBuilder.insert(0, ".*");
					}
					if (!inputRuleString.endsWith(".*")) {
						inputRuleStrBuilder.insert(inputRuleStrBuilder.length(), ".*");
					}
					inputRuleString=inputRuleStrBuilder.toString();
					type=Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP;
				}
				break;	
			}
	    		
			if (type == Constants.CUSTOM_BLOCKED_KEYWORD_REGEXP || type == Constants.IN_BLOCKED_KEYWORD
					 || type == Constants.CUSTOM_BLOCKED_KEYWORD	) {
				try {
					Pattern.compile(inputRuleString);
				} catch (RuntimeException e) {
					Toast.makeText(getApplication(),
							getString(R.string.error_regexp),
							Toast.LENGTH_SHORT).show();
//					inputrule.delete(0, inputrule.length());
					return;
				}
			} else {
				String regexp = inputRuleString.replaceAll("\\?", ".").replaceAll("\\*",".*");
				try {
					Pattern.compile(regexp);
				} catch (RuntimeException e) {
					Toast.makeText(getApplication(),
							getString(R.string.error_regexp),
							Toast.LENGTH_SHORT).show();
//					inputrule.delete(0, inputrule.length());
					return;
				}
			}

			if (inputRuleString.length() > 0) {			

				if (mode == addMode) {
					if (mDbAdapter.createOne(inputRuleString.toLowerCase(), type)==-2) {
						Toast.makeText(getApplication(),
								"已存在相同规则",Toast.LENGTH_SHORT).show();
					}else {
						Toast.makeText(getApplication(),
								"成功添加规则",Toast.LENGTH_SHORT).show();
					}
				}else if (mode == editMode) {
					mDbAdapter.updateOneId(rowId, inputRuleString.toLowerCase(), type);
				}

			}else {
//				inputrule.delete(0, inputrule.length());
				mDbAdapter.deleteOne("rules",rowId);
				return;
			}     	
//			inputrule.delete(0, inputrule.length());
	    }
//	    
	    private class typeSelectedListener implements OnItemSelectedListener {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				switch (arg2) {
				case 0:
					inputTipsText.setText(R.string.custom_number_tips);
					break;
				case 1:
					inputTipsText.setText(R.string.custom_number_tips);
					break;
//				case 2:
//					inputTipsText.setText(R.string.custom_mms_number_tips);
//					break;
				case 2:
					inputTipsText.setText(R.string.in_blocked_keyword_tips);
					break;
				case 3:
					inputTipsText.setText(R.string.custom_trusted_keyword_tips);
					break;
				case 4:
					inputTipsText.setText(R.string.custom_blocked_count_number_tips);
					break;
				case 5:
					inputTipsText.setText(R.string.custom_blocked_keyword_regexp_tips);
					break;
				case 6:
					inputTipsText.setText(R.string.in_trusted_number_tips);
					break;
				case 7:
					inputTipsText.setText(R.string.custom_blocked_keyword_tips);
					break;	
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}		
		}
	    
}
