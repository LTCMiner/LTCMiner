package com.miner.litecoin;

import android.os.Bundle;
import com.miner.litecoin.Console;
import com.miner.litecoin.MinerService.LocalBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.RemoteException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.miner.litecoin.Miner;
import com.raad287.LTCMiner.R;
import android.app.Activity;
import android.app.Service;
import android.view.Menu;
import android.util.Log;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
	IRemoteService mService = null;
    boolean mBound = false;
	Miner miner;
	
	private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	Log.i("LC", "ServiceConnection: Connected to Service");
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
        	Log.i("LC", "ServiceConnection: Disconnected from Service");
            mBound = false;
        }
    };

	final int MSG_UIUPDATE = 1;
	final int MSG_TERMINATED = 2;
	final int MSG_SPEED_UPDATE = 3;
	final int MSG_STATUS_UPDATE = 4;
	final int MSG_ACCEPTED_UPDATE = 5;
	final int MSG_REJECTED_UPDATE = 6;
	final int MSG_CONSOLE_UPDATE = 7;
	
	public static final String PREF_URL="URL";
	public static final String PREF_USER= "USER";
	public static final String PREF_PASS= "PASS";
	public static final String PREF_THREAD= "THREAD";
	public static final String PREF_THROTTLE = "THROTTLE";
	public static final String PREF_SCANTIME = "SCANTIME";
	public static final String PREF_RETRYPAUSE = "RETRYPAUSE";
	public static final String PREF_DONATE = "DONATE";
	public static final String PREF_SERVICE = "SERVICE";
	public static final String PREF_TITLE="SETTINGS"; 
	public static final String PREF_PRIORITY="PRIORITY";
	public static final String DEFAULT_URL="http://litecoinpool.org:9332";
	public static final String DEFAULT_USER="Username";
	public static final String DEFAULT_PASS="Password";
	public static final String DONATE_URL="http://litecoinpool.org:9332";
	public static final String DONATE_USER="raad287.3";
	public static final String DONATE_PASS="3";
	public static final int DEFAULT_PRIORITY=1;
	public static final int DEFAULT_THREAD=1;
	public static final long DEFAULT_SCANTIME=500;
	public static final long DEFAULT_RETRYPAUSE=500;
	public static final float DEFAULT_THROTTLE=1;
	public static final boolean DEFAULT_DONATE = false;
	public static final boolean DEFAULT_SERVICE = false;
	

	public int curScreenPos=0;
	public String miner_status="Not Mining";
	public float miner_speed=0;
	public boolean miner_running = false;
	public long accepted = 0;
	public long rejected = 0;
	public boolean donate = false;
	public String consoleString = "";
	
	public String getStatus() { return miner_status; }
	public Boolean getRunning() {return miner_running; }
	public float getSpeed() { return miner_speed; }
	public float getSpeed() { return miner_speed; }
	public Long getAccepted() { return accepted; }
	public Long getRejected() { return rejected; }
	public String getConsole() { return consoleString; }
	
	@Override
	protected void onPause() {
		Log.i("LC", "Main: in onPause");

		if(miner_running==true)
		{
			stopMining();
		}
		super.onPause();
	}
	
	
	Handler mainHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		Bundle bundle=msg.getData();
		Log.i("LC", "In handler: handle Message "+msg.arg1);
		
		if(msg.arg1==MSG_CONSOLE_UPDATE) { consoleString = bundle.getString("console"); }
		else if(msg.arg1==MSG_SPEED_UPDATE) { miner_speed = bundle.getFloat("speed"); }
		else if(msg.arg1==MSG_STATUS_UPDATE) { miner_status = bundle.getString("status"); }
		else if(msg.arg1==MSG_ACCEPTED_UPDATE) { accepted = bundle.getLong("accepted"); }
		else if(msg.arg1==MSG_REJECTED_UPDATE) { rejected = bundle.getLong("rejected"); }
		else if(msg.arg1==MSG_TERMINATED)
		{
			miner_running=false;
		}
		
		onUpdate();
		super.handleMessage(msg);
	}	
	};
	
	public Console console = new Console();
	
	public void startMining() {
		Log.i("LC", "Main: startMining");
		String url;
		String user;
		String pass;
		
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		
		console.write("Main: startMining");
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

		miner_running=true;
		
		
		miner = new Miner(url, 
						  user+":"+
						  pass, 
						  settings.getLong(PREF_SCANTIME, DEFAULT_SCANTIME),
						  settings.getLong(PREF_RETRYPAUSE, DEFAULT_RETRYPAUSE),
						  settings.getInt(PREF_THREAD, DEFAULT_THREAD),
						  settings.getFloat(PREF_THROTTLE, DEFAULT_THROTTLE), 
						  settings.getInt(PREF_PRIORITY, DEFAULT_PRIORITY),
						  mainHandler, console);
		
		Intent intent = new Intent(this, MinerService.class);
		startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mService.setMiner(miner);
        mService.startMiner();
		
	}
	
	public void stopMining() 
	{
		Log.i("LC", "Main: Mining stopping...");
		console.write("Main: Mining stopping...");
		miner_running=false;
		mService.stopMiner();
		
	}
	
	public void onUpdate() {
		Log.i("LC", "Main: onUpdate");
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    
    	  Log.i("LC", "Main: in onCreate()");
    	  setTitle("LTCMiner");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        
        return true;
    }
    

    
}
