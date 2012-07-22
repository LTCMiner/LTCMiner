package com.miner.litecoin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.miner.litecoin.MainActivity;
import com.miner.litecoin.SettingsActivity;
import com.raad287.LTCMiner.R;

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
				Log.i("LC", "Spinner: Status Selected");
				intent = new Intent(parent.getContext(), StatusActivity.class);
				parent.getContext().startActivity(intent);
				//Launch Status Activity
				
			}
			if(pos==1)
			{
				//Launch Settings Activity
				Log.i("LC", "Spinner: Settings Selected");
				intent = new Intent(parent.getContext(), SettingsActivity.class);
				parent.getContext().startActivity(intent);
				Log.i("LC", "Spinner: Settings Launched?");
			}
		}
		
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
