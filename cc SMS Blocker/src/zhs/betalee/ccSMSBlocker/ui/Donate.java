package zhs.betalee.ccSMSBlocker.ui;

import zhs.betalee.ccSMSBlocker.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Donate extends Activity{

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.donate);
		final Button donateButton=(Button)findViewById(R.id.DonateButton);
		donateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://me.alipay.com/betalee")));
			}
		});
		
		
		
		super.onCreate(savedInstanceState);
	}

}
