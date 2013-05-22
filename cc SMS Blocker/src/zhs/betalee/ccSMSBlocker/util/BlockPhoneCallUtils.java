package zhs.betalee.ccSMSBlocker.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;

public class BlockPhoneCallUtils {

	public static void changeBlockPhoneClass(Context context,String blockphoneclass)
	{
		Context mContext=context;
		context=null;
		String MCCMNC = ((TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimOperator();
		
		if (MCCMNC != null)
			if ((MCCMNC.equals("46000")) || (MCCMNC.equals("46002")))//移动
			{
				mmiGSMCall(mContext, blockphoneclass);
			}else if (MCCMNC.equals("46001"))//联通
			{
				mmiCDMACall(mContext, blockphoneclass);
			}else if (MCCMNC.equals("46003"))//电信
			{
				
			}else{

			}

	}
	
	private static void mmiGSMCall(Context context,String blockphoneclass){
		int intblockphoneclass=Integer.parseInt(blockphoneclass);
		Context mContext=context;
		String MMI_NUM;
		switch (intblockphoneclass) {
		case 1:
			MMI_NUM="tel:**67*13810538911%23";
			break;
		case 2:
			MMI_NUM="tel:**67*13701110216%23";
			break;
		case 4:
			MMI_NUM="tel:**67*1381234567%23";
			break;
		case 8:
			MMI_NUM="tel:%23%2367%23";
			break;
		default:
			MMI_NUM="tel:%23%2367%23";
			break;
		}
		
		Intent i = new Intent(Intent.ACTION_CALL);  
        i.setData(Uri.parse(MMI_NUM));  
        mContext.startActivity(i); 
//		System.out.println(MMI_NUM);
		
	}
	private static void mmiCDMACall(Context context,String blockphoneclass){
		int intblockphoneclass=Integer.parseInt(blockphoneclass);
		Context mContext=context;
		String MMI_NUM;
		switch (intblockphoneclass) {
		case 1:
			MMI_NUM="tel:*9013641244138";
			break;
		case 2:
			MMI_NUM="tel:*9013641244026";
			break;
		case 4:
			MMI_NUM="tel:*901381234567";
			break;
		case 8:
			MMI_NUM="tel:*730";
			break;
		default:
			MMI_NUM="tel:*730";
			break;
		}
		
		Intent i = new Intent(Intent.ACTION_CALL);  
        i.setData(Uri.parse(MMI_NUM));  
        mContext.startActivity(i); 
//		System.out.println(MMI_NUM);
		
	}
	
}
