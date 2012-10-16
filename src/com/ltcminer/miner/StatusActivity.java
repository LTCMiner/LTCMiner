package com.ltcminer.miner;

import static com.ltcminer.miner.Constants.*;

import java.text.DecimalFormat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.ltcminer.miner.R;

public class StatusActivity extends MainActivity {
	private static int updateDelay=1000;
	String unit = " h/s";
	
	Handler statusHandler = new Handler() { };
	
	final Runnable rConsole = new Runnable() {
        public void run() { 
        	//Log.i("LC", "StatusActivity:updateConsole:"+mService.console.getConsole());
        	TextView txt_console = (TextView) findViewById(R.id.status_textView_console);
        	txt_console.setText(mService.cString);
        	txt_console.invalidate();
        } 
    };
        
    final Runnable rSpeed = new Runnable() {
       public void run() { 
    	   // Log.i("LC", "StatusActivity:updateSpeed");
           	TextView tv_speed = (TextView) findViewById(R.id.status_textView_speed);
           	DecimalFormat df = new DecimalFormat("#.##");
           	tv_speed.setText(df.format(mService.speed*1000)+unit);
       } 
    };
    final Runnable rAccepted = new Runnable() {
       public void run() {
    	   // Log.i("LC", "StatusActivity:updateAccepted");
            TextView txt_accepted = (TextView) findViewById(R.id.status_textView_accepted);
            txt_accepted.setText(String.valueOf(mService.accepted));
       } 
    };
    final Runnable rRejected = new Runnable() {
        public void run() { 
        	// Log.i("LC", "StatusActivity:updateRejected");
             TextView txt_rejected = (TextView) findViewById(R.id.status_textView_rejected);
             txt_rejected.setText(String.valueOf(mService.rejected));
        } 
     };
     final Runnable rStatus = new Runnable() {
         public void run() {
        	//  Log.i("LC", "StatusActivity:updateStatus");
              TextView txt_status = (TextView) findViewById(R.id.status_textView_status);
              txt_status.setText(mService.status);
         } 
      };
      final Runnable rBtnStart= new Runnable() { 
    	  public void run() { 
    		 // Log.i("LC", "StatusActivity: Miner stopped, changing button to start");
    		  Button b = (Button) findViewById(R.id.status_button_startstop);
    		  b.setText(getString(R.string.main_button_start));
    		  b.setEnabled(true);
    	  }
      };
      final Runnable rBtnStop= new Runnable() { 
    	  public void run() { 
    		  //Log.i("LC", "StatusActivity: Miner stopped, changing button to stop");
    		  Button b = (Button) findViewById(R.id.status_button_startstop);
    		  b.setText(getString(R.string.main_button_stop));
    		  b.setEnabled(true);
    	  }
      };
       
       

	Thread updateThread = new Thread () { 
		public void run() {	
			Log.i("LC", "StatusActivity: Update thread started");
			// wait for service to bind
			while (mBound==false)
			{
				try {
					sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}
			}
			
			// If the service is running make sure the button is changed to "Stop Mining" and vice versa
			if(mService.running==true) { statusHandler.post(rBtnStop); }
			else { statusHandler.post(rBtnStart); }

			while (mBound==true)	{	
				try {
					sleep(updateDelay);
				} catch (InterruptedException e) {
					Log.i("LC", "StatusActivity:updateThread: Interrupted");
				}
				
			statusHandler.post(rConsole);
			statusHandler.post(rSpeed);
			statusHandler.post(rAccepted);
			statusHandler.post(rRejected);
			statusHandler.post(rStatus);
			if(mService.running==true) { statusHandler.post(rBtnStop); }
			else {statusHandler.post(rBtnStart);
			}
		} }
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
    	setContentView(R.layout.activity_status);
    	Log.i("LC", "Status: onCreate");
    	
    	Intent intent = new Intent(getApplicationContext(), MinerService.class);
    	startService(intent);
    	bindService(intent, super.mConnection, Context.BIND_AUTO_CREATE);
    	
    	Button btn_startStop = (Button) findViewById(R.id.status_button_startstop);
    	
    	//Setup nav spinner
    	Spinner spn_nav = (Spinner) findViewById(R.id.status_spinner_nav);
    	ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.nav, android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spn_nav.setAdapter(adapter);
    	spn_nav.setSelection(0);
    	spn_nav.setOnItemSelectedListener(new SpinnerListener(0));
    	
    		
    	// Set Button Click Listener
        btn_startStop.setOnClickListener(new Button.OnClickListener() {
    
			public void onClick(View v) {
				Button b = (Button) v;
				
				if (b.getText().equals(getString(R.string.status_button_start))==true){
					startMining();
					b.setText(getString(R.string.main_button_stop));
				}
				else{
					stopMining();
					b.setText(getString(R.string.status_button_start));
				}
			}

        });
        

        updateThread.start();
        
        // Launch news on first run
    	if(settings.getBoolean(PREF_NEWS_RUN_ONCE, false)==false)
    	{
    		intent = new Intent(getApplicationContext(), NewsActivity.class);
			startActivity(intent);
    	}
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		super.onPause();
	}


	@Override
	protected void onResume() {
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
        if (settings.getBoolean(PREF_BACKGROUND, DEFAULT_BACKGROUND)==true)
        {
        	TextView tv_background = (TextView) findViewById(R.id.status_textView_background);
        	tv_background.setText("RUN IN BACKGROUND");
        }
		super.onResume();
	}



	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		if(updateThread.isAlive()==true) { updateThread.interrupt(); }
		
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
    	if(settings.getBoolean(PREF_BACKGROUND,DEFAULT_BACKGROUND )==false)
    	{
    		if (mService.running==true) { stopMining(); }
    		Intent intent = new Intent(getApplicationContext(), MinerService.class);
    		stopService(intent);
    	}
		
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
	
}
