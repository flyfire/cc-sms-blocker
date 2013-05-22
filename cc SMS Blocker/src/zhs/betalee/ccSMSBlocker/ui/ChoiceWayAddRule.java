package zhs.betalee.ccSMSBlocker.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChoiceWayAddRule extends ListActivity{
	private ArrayList<String> array;
	private ArrayAdapter<String> adapter;
    private final static int addMode=0;
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ListView listView = getListView();
		array = new ArrayList<String>();
		array.add("从收件箱添加");
		array.add("手动添加");
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, array);
		listView.setAdapter(adapter);

	}
	
	
	
	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (position == 0) {
			startActivity(new Intent(getApplicationContext(), AddressFormInbox.class));
		}else {
			Intent localIntent = new Intent(getApplicationContext(), EditRules.class);
			Bundle bundle=new Bundle();
			bundle.putInt("Mode", addMode);
			localIntent.putExtras(bundle);
			startActivityForResult(localIntent,addMode);
		}
		finish();
	}



	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
