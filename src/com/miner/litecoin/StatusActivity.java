package com.miner.litecoin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.raad287.LTCMiner.R;

public class StatusActivity extends MainActivity {
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i("LC", "Status: in onPause");
		if(super.getRunning()==true)
		{
			stopMining();
		}
		super.onPause();
	}
	
	@Override 
	protected void onResume() {
		Spinner spn_nav = (Spinner) findViewById(R.id.status_spinner_nav);
		spn_nav.setSelection(0);
		super.onResume();
	}

	@Override
	public void onUpdate() {
		
		Log.i("LC","Status: onUpdate");
			Log.i("LC", "Status:Miner running true");
			TextView txt_speed = (TextView) findViewById(R.id.status_textView_speed);
			TextView txt_status = (TextView) findViewById(R.id.status_textView_status);
			TextView txt_accepted = (TextView) findViewById(R.id.status_textView_accepted);
			TextView txt_rejected = (TextView) findViewById(R.id.status_textView_rejected);
			TextView txt_console = (TextView) findViewById(R.id.status_textView_console);
			
			txt_speed.setText(String.valueOf(super.getSpeed()*1000)+ " h/s");
			txt_status.setText(super.getStatus());
			txt_accepted.setText( String.valueOf(super.getAccepted()) );
			txt_rejected.setText( String.valueOf(super.getRejected()) );
			txt_console.setText( super.getConsole());
			Log.i("LC", "Status: super.getConsole: "+super.getConsole());

		
		Button btn = (Button) findViewById(R.id.status_button_startstop);
		if(super.getRunning()==false)
		{
			btn.setText(R.string.status_button_start);
		}
		else
		{
			btn.setText(R.string.main_button_stop);
		}
		
		Log.i("LC","Status: onUpdate complete");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_status);
    	Log.i("LC", "Status: onCreate");
    	
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
				
				if (b.getText()==getString(R.string.status_button_start)){
					startMining();
					b.setText(getString(R.string.main_button_stop));
				}
				else{
					stopMining();
					b.setText(getString(R.string.status_button_start));
				}
			}

        });
        
        onUpdate();
	}
	
}
