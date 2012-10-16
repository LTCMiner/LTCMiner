package com.ltcminer.miner;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.ltcminer.miner.SettingsActivity;
import com.ltcminer.miner.MainActivity;
import com.ltcminer.miner.R;

// Top menu selection SpinnerListener
public class SpinnerListener implements OnItemSelectedListener {

	int curScreen;
	
	public SpinnerListener(int curScreen) {
		super();
		this.curScreen = curScreen;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
		Intent intent;
		Log.i("LC", "Spinner Listener: On Item Selected : "+pos);
		// TODO Auto-generated method stub
		
		if(pos!=curScreen)
		{
			if(pos==0)
			{
				//Launch Status Activity
				Log.i("LC", "SpinnerListener:onItemSelected: Status Selected");
				intent = new Intent(parent.getContext(), StatusActivity.class);
				parent.getContext().startActivity(intent);
				
			}
			if(pos==1)
			{
				//Launch Settings Activity
				Log.i("LC", "SpinnerListener:onItemSelected: Settings Selected");
				intent = new Intent(parent.getContext(), SettingsActivity.class);
				parent.getContext().startActivity(intent);
			}
			if(pos==2)
			{
				//Launch News Activity
				Log.i("LC", "SpinnerListener:onItemSelected: News Selected");
				intent = new Intent(parent.getContext(), NewsActivity.class);
				parent.getContext().startActivity(intent);
			}
		}
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
