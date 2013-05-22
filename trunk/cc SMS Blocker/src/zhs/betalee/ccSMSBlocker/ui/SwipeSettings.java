package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import zhs.betalee.ccSMSBlocker.util.MessageUtils;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

public class SwipeSettings extends Activity{
	public final static String SWIPE_ACTION_SMS="swipe_action_sms";
	public final static String SWIPE_ACTION_RULES="swipe_action_rules";
	
	public final static String SWIPE_ACTION_LEFT="action_left";
	public final static String SWIPE_ACTION_RIGHT="action_right";
	public final static String SWIPE_ACTION_RULES_LEFT="rules_action_left";
	public final static String SWIPE_ACTION_RULES_RIGHT="rules_action_right";
	
	
	private RadioButton rbActionLeftRecover;
	private RadioButton rbActionLeftDel;
	private RadioButton rbActionRightRecover;
	private RadioButton rbActionRightDel;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.swipesettings);
		

        rbActionLeftRecover = (RadioButton) findViewById(R.id.action_left_recover);
        rbActionLeftRecover.setOnCheckedChangeListener(radiosListener);

        rbActionLeftDel = (RadioButton) findViewById(R.id.action_left_del);
        rbActionLeftDel.setOnCheckedChangeListener(radiosListener);

        if (MessageUtils.readIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_LEFT) == 0) {
        	rbActionLeftRecover.setChecked(true);
        } else {
        	rbActionLeftDel.setChecked(true);
        }    
        
       
        rbActionRightRecover = (RadioButton) findViewById(R.id.action_right_recover);
        rbActionRightRecover.setOnCheckedChangeListener(radiosListener);
        
        rbActionRightDel = (RadioButton) findViewById(R.id.action_right_del);
        rbActionRightDel.setOnCheckedChangeListener(radiosListener);

        if (MessageUtils.readIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RIGHT) == 0) {
        	rbActionRightRecover.setChecked(true);
        } else {
        	rbActionRightDel.setChecked(true);
        }
        

        CheckBox cbSwipeActionSMS = (CheckBox) findViewById(R.id.cb_swipe_action_sms);
        cbSwipeActionSMS.setOnCheckedChangeListener(new OnCheckedChangeListener() {

        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		if (isChecked) {
        			MessageUtils.writeBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_SMS, true);
        			rbActionLeftRecover.setEnabled(true);
        			rbActionLeftDel.setEnabled(true);
        			rbActionRightRecover.setEnabled(true);
        			rbActionRightDel.setEnabled(true);  
        		}else {
        			MessageUtils.writeBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_SMS, false);
        			rbActionLeftRecover.setEnabled(false);
        			rbActionLeftDel.setEnabled(false);
        			rbActionRightRecover.setEnabled(false);
        			rbActionRightDel.setEnabled(false);
        		}
        	}
        });
        if (!MessageUtils.readBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_SMS)) {
        	rbActionLeftRecover.setEnabled(false);
        	rbActionLeftDel.setEnabled(false);
        	rbActionRightRecover.setEnabled(false);
        	rbActionRightDel.setEnabled(false);
        }else {
        	cbSwipeActionSMS.setChecked(true);
        }
       //==============================================================================================
        

        final CompoundButton rbRulesActionLeftRecover = (RadioButton) findViewById(R.id.rule_action_left_edit);
        rbRulesActionLeftRecover.setOnCheckedChangeListener(radiosListener);

        final CompoundButton rbRulesActionLeftDel = (RadioButton) findViewById(R.id.rule_action_left_del);
        rbRulesActionLeftDel.setOnCheckedChangeListener(radiosListener);

        if (MessageUtils.readIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_LEFT) == 0) {
        	rbRulesActionLeftRecover.setChecked(true);
        } else {
        	rbRulesActionLeftDel.setChecked(true);
        }    
        
       
        final CompoundButton rbRulesActionRightRecover = (RadioButton) findViewById(R.id.rule_action_right_edit);
        rbRulesActionRightRecover.setOnCheckedChangeListener(radiosListener);
        
        final CompoundButton rbRulesActionRightDel = (RadioButton) findViewById(R.id.rule_action_right_del);
        rbRulesActionRightDel.setOnCheckedChangeListener(radiosListener);

        if (MessageUtils.readIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_RIGHT) == 0) {
        	rbRulesActionRightRecover.setChecked(true);
        } else {
        	rbRulesActionRightDel.setChecked(true);
        }
        

        CheckBox cbSwipeActionRULES = (CheckBox) findViewById(R.id.cb_swipe_action_rules);
        cbSwipeActionRULES.setOnCheckedChangeListener(new OnCheckedChangeListener() {

        	@Override
        	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        		if (isChecked) {
        			MessageUtils.writeBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES, true);
        			rbRulesActionLeftRecover.setEnabled(true);
        			rbRulesActionLeftDel.setEnabled(true);
        			rbRulesActionRightRecover.setEnabled(true);
        			rbRulesActionRightDel.setEnabled(true);  
        		}else {
        			MessageUtils.writeBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES, false);
        			rbRulesActionLeftRecover.setEnabled(false);
        			rbRulesActionLeftDel.setEnabled(false);
        			rbRulesActionRightRecover.setEnabled(false);
        			rbRulesActionRightDel.setEnabled(false);
        		}
        	}
        });
        if (!MessageUtils.readBooleanSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES)) {
        	rbRulesActionLeftRecover.setEnabled(false);
        	rbRulesActionLeftDel.setEnabled(false);
        	rbRulesActionRightRecover.setEnabled(false);
        	rbRulesActionRightDel.setEnabled(false);
        }else {
        	cbSwipeActionRULES.setChecked(true);
        }
        
           
		super.onCreate(savedInstanceState);
	}
	
	CompoundButton.OnCheckedChangeListener radiosListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) {
				switch (buttonView.getId()) {
				case R.id.action_left_recover:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_LEFT, 0);
					break;
				case R.id.action_left_del:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_LEFT, 1);
					break;
				case R.id.action_right_recover:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RIGHT, 0);
					break;
				case R.id.action_right_del:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RIGHT, 1);
					break;
				case R.id.rule_action_left_edit:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_LEFT, 0);
					break;
				case R.id.rule_action_left_del:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_LEFT, 1);
					break;
				case R.id.rule_action_right_edit:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_RIGHT, 0);
					break;
				case R.id.rule_action_right_del:
					MessageUtils.writeIntSharedPreferences(getApplicationContext(), SWIPE_ACTION_RULES_RIGHT, 1);
					break;
					
					
				}
			}
		}
	};
}
