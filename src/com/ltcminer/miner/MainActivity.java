package com.ltcminer.miner;

import static com.ltcminer.miner.Constants.*;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.ltcminer.miner.R;
import com.ltcminer.miner.MinerService.LocalBinder;

import android.view.Menu;
import android.util.Log;

public class MainActivity extends Activity {
    boolean mBound = false;
	MinerService mService;

	public int curScreenPos=0;
	
    public ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i("LC", "Main: onServiceConnected()");
			LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound=true;
            Log.i("LC", "Main: Service Connected");
		}

		public void onServiceDisconnected(ComponentName name) {  mBound=false;   }
    };
    

	public void startMining() {
		Log.i("LC", "Main: startMining()");
		mService.startMiner();
	}
	
	public void stopMining() 
	{
		Log.i("LC", "Main: stopMining()");
		mService.stopMiner();
		
	}
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Log.i("LC", "Main: in onCreate()");
    	setTitle("LTCMiner"); 	
    	
    }

    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
    	super.onStop();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        return true;
    }
    

    
}
