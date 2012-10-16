package com.ltcminer.miner;
import static com.ltcminer.miner.Constants.*;

import java.util.Observable;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MinerService extends Service {

	Miner miner;
	Console console;
	String news=null;
	Boolean running=false;
	float speed=0;
	int accepted=0;
	int rejected=0;
	String status="Not Mining";
	String cString="";
	
   Handler serviceHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle bundle=msg.getData();
			Log.i("LC", "Service: handleMessage() "+msg.arg1);
			
			if(msg.arg1==MSG_CONSOLE_UPDATE) { cString = bundle.getString("console"); }
			else if(msg.arg1==MSG_SPEED_UPDATE) { speed = bundle.getFloat("speed"); }
			else if(msg.arg1==MSG_STATUS_UPDATE) { status = bundle.getString("status"); }
			else if(msg.arg1==MSG_ACCEPTED_UPDATE) { accepted = (int) bundle.getLong("accepted"); }
			else if(msg.arg1==MSG_REJECTED_UPDATE) { rejected = (int) bundle.getLong("rejected"); }
			else if(msg.arg1==MSG_TERMINATED) {	running=false; }
			super.handleMessage(msg);
		}	
		};
	// Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    
    public class LocalBinder extends Binder {
        MinerService getService() {
            return MinerService.this;
        }
    }
    
    public MinerService() {
		Log.i("LC", "Service: MinerService()");
	}
    
    
    public void startMiner()
    {
    	console = new Console(serviceHandler);
    	Log.i("LC", "MinerService:startMiner()");
    	SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		String url, user, pass;
		speed=0;
		accepted=0;
		rejected=0;
    	
		console.write("Service: Starting mining");
		if (settings.getBoolean(PREF_DONATE, DEFAULT_DONATE)==true) 
		{ 
			console.write("Main: Donate mode");
			url=DONATE_URL;
			user=DONATE_USER;
			pass=DONATE_PASS;
		}
		else
		{
			url = settings.getString(PREF_URL, DEFAULT_URL);
			user = settings.getString(PREF_USER, DEFAULT_USER);
			pass = settings.getString(PREF_PASS, DEFAULT_PASS);
		}
		
		miner = new Miner(url, 
						  user+":"+
						  pass, 
						  settings.getLong(PREF_SCANTIME, DEFAULT_SCANTIME),
						  settings.getLong(PREF_RETRYPAUSE, DEFAULT_RETRYPAUSE),
						  settings.getInt(PREF_THREAD, DEFAULT_THREAD),
						  settings.getFloat(PREF_THROTTLE, DEFAULT_THROTTLE), 
						  settings.getInt(PREF_PRIORITY, DEFAULT_PRIORITY),
						  serviceHandler, console);
    	miner.start();
    	running =true;
    }
    
    public void stopMiner()
    {
    	Log.i("LC", "Service: onBind()");
    	console.write("Service: Stopping mining");
    	running=false;
    	miner.stop();
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i("LC", "Service: onBind()");
	
		return mBinder;
	}
	
	
	
}
