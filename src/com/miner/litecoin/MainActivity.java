package com.miner.litecoin;

import android.os.Bundle;
import com.miner.litecoin.MinerService;
import com.miner.litecoin.MinerService.LocalBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.raad287.LTCMiner.R;
import android.view.Menu;
import android.util.Log;

public class MainActivity extends Activity {
    boolean mBound = false;
	MinerService mService;

	public int curScreenPos=0;
	
    private ServiceConnection mConnection = new ServiceConnection() {

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
    	Intent intent = new Intent(getApplicationContext(), MinerService.class);
    	startService(intent);
    	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    	Log.i("LC", "Main: in onCreate()");
    	setTitle("LTCMiner"); 	
		
    }

    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
    	Log.i("LC", "Main: in onStop()");
    	try {
    		unbindService(mConnection); 
    	} catch (RuntimeException e) {
    		Log.i("LC", "RuntimeException:"+e.getMessage());
    		//unbindService generates a runtime exception sometimes
    		//the service is getting unbound before unBindService is called
    		//when the window is dismissed by the user, this is the fix
    	}
    	
		super.onStop();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        return true;
    }
    

    
}
