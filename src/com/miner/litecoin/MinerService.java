package com.miner.litecoin;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MinerService extends Service {

	Miner miner;

	private final IBinder mBinder = (IBinder) new LocalBinder();
	
	public class LocalBinder extends Binder {
        MinerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MinerService.this;
        }
        
	}
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	public void setMiner(Miner m) { miner = m; }
	public void startMiner () { 
			Log.i("LC", "Service: startMiner");
			miner.start(); }
	public void stopMiner() { 
			Log.i("LC", "Service: stopMiner");
			miner.stop();}


	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i("LC", "Service: onStart");
		super.onStart(intent, startId);
		
	}
	
	
}
