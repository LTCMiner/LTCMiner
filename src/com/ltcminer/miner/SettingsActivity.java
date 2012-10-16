package com.ltcminer.miner;

import static com.ltcminer.miner.Constants.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ltcminer.miner.SpinnerListener;
import com.ltcminer.miner.R;


public class SettingsActivity extends MainActivity {
	EditText et_serv;
	EditText et_user;
	EditText et_pass;
	EditText et_thread;
	EditText et_scanTime;
	EditText et_retryPause;
	SeekBar sb_throttle;
	TextView tv_throttle;
	CheckBox cb_service;
	CheckBox cb_donate;
	Button btn_default;
	Button btn_save;
	Button btn_load;
	Button btn_contact;
	Spinner spn_priority;
	boolean fresh_run=true;
	
	public void loadDefault() {
		StringBuilder sb=new StringBuilder();
		et_serv.setText(sb.append(DEFAULT_URL).toString());
		sb.setLength(0);
		et_user.setText(sb.append(DEFAULT_USER).toString());
		sb.setLength(0);
		et_pass.setText(sb.append(DEFAULT_PASS).toString());
		sb.setLength(0);
		et_thread.setText(sb.append(DEFAULT_THREAD).toString());
		sb.setLength(0);
		et_scanTime.setText(sb.append(DEFAULT_SCANTIME).toString());
		sb.setLength(0);
		et_retryPause.setText(sb.append(DEFAULT_RETRYPAUSE).toString());
		sb.setLength(0);
		sb_throttle.setProgress((int) (DEFAULT_THROTTLE*100));
		spn_priority.setSelection(0);	// min priority
		
		cb_service.setChecked(DEFAULT_BACKGROUND);
		cb_donate.setChecked(DEFAULT_DONATE);
		Log.i("LC", "Settings: defaults loaded");
		
		Toast.makeText(getBaseContext(), "Defaults Loaded", Toast.LENGTH_SHORT).show();
		
	}
	
	public void saveSettings()
	{
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		SharedPreferences.Editor editor = settings.edit();
		StringBuilder sb= new StringBuilder();
		
		if (verify()==true)
		{
			sb = new StringBuilder();
			
			String url = sb.append(et_serv.getText()).toString();
			sb.setLength(0);
			String user = sb.append(et_user.getText()).toString();
			sb.setLength(0);
			String pass = sb.append(et_pass.getText()).toString();
			sb.setLength(0);
			int threads = Integer.parseInt(sb.append(et_thread.getText()).toString());
			sb.setLength(0);
			float throttle = (float) sb_throttle.getProgress()/ 100;
			sb.setLength(0);
			long scantime = Long.parseLong(sb.append(et_scanTime.getText()).toString());
			sb.setLength(0);
			long retrypause = Long.parseLong(sb.append(et_retryPause.getText()).toString());
			
			settings = getSharedPreferences(PREF_TITLE, 0);
			editor = settings.edit();
			editor.putString(PREF_URL, url);
			editor.putString(PREF_USER, user);
			editor.putString(PREF_PASS, pass);
			editor.putInt(PREF_THREAD, threads);
			Log.i("LC","Settings: Throttle: "+throttle);
			editor.putFloat(PREF_THROTTLE, throttle);
			editor.putLong(PREF_SCANTIME, scantime);
			editor.putLong(PREF_RETRYPAUSE, retrypause);
			editor.putBoolean(PREF_BACKGROUND, cb_service.isChecked());
			editor.putBoolean(PREF_DONATE, cb_donate.isChecked());
			
			Log.i("LC", "Settings: Pri "+(String)spn_priority.getSelectedItem());
			if (spn_priority.getSelectedItemPosition()==0) {editor.putInt(PREF_PRIORITY, Thread.MIN_PRIORITY); }
			if (spn_priority.getSelectedItemPosition()==1) {editor.putInt(PREF_PRIORITY, Thread.NORM_PRIORITY); }
			if (spn_priority.getSelectedItemPosition()==2) {editor.putInt(PREF_PRIORITY, Thread.MAX_PRIORITY); }
			Log.i("LC", "Settings: Settings saved");
			editor.commit();
			Toast.makeText(getBaseContext(), "Settings Saved", Toast.LENGTH_SHORT).show();
			}
		else
		{
			Log.i("LC", "Settings: Invalid Input");
			Toast.makeText(getBaseContext(), "Settings: Errors changed to red", Toast.LENGTH_SHORT).show();
		}	
			editor.commit();
	}

	

	public void loadSettings()
	{
		// if does not exist load default
		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		StringBuilder sb= new StringBuilder();
		et_serv.setText(settings.getString(PREF_URL, DEFAULT_URL));
		et_user.setText(settings.getString(PREF_USER, DEFAULT_USER));
		et_pass.setText(settings.getString(PREF_PASS, DEFAULT_PASS));
		et_thread.setText( sb.append( settings.getInt(PREF_THREAD, DEFAULT_THREAD)).toString());
		sb.setLength(0);
		sb_throttle.setProgress(  (int) (settings.getFloat(PREF_THROTTLE, DEFAULT_THROTTLE)*100));
		et_scanTime.setText( sb.append( settings.getLong(PREF_SCANTIME, DEFAULT_SCANTIME)).toString());
		sb.setLength(0);
		et_retryPause.setText( sb.append( settings.getLong(PREF_RETRYPAUSE, DEFAULT_RETRYPAUSE)).toString());
		cb_service.setChecked( settings.getBoolean(PREF_BACKGROUND, DEFAULT_BACKGROUND));
		cb_donate.setChecked( settings.getBoolean(PREF_DONATE, DEFAULT_DONATE));
		
		if(settings.getInt(PREF_PRIORITY, DEFAULT_PRIORITY)==Thread.MIN_PRIORITY) {spn_priority.setSelection(0);}
		if(settings.getInt(PREF_PRIORITY, DEFAULT_PRIORITY)==Thread.NORM_PRIORITY) {spn_priority.setSelection(1);}
		if(settings.getInt(PREF_PRIORITY, DEFAULT_PRIORITY)==Thread.MAX_PRIORITY) {spn_priority.setSelection(2);}
		
		
		Toast.makeText(getBaseContext(), "Settings Loaded", Toast.LENGTH_SHORT).show();
	}

	public boolean verify()
	{
		boolean success=true;
		StringBuilder sb=new StringBuilder();
		//Attempt to verify url
		if(et_serv.getText()!=null) {
			sb.append(et_serv.getText());
			try {
			URL url = new URL(sb.toString());
			et_serv.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
			}
			catch (MalformedURLException e) {
				Log.i("LC","Settings Error: Invalid URL");
				et_serv.setShadowLayer(1, 1, 1, Color.RED);
				success=false;
			}
		}
		
		//Verify user
		if(et_user.getText()==null) {
			Log.i("LC", "Settings Error: no username");
			et_user.setShadowLayer(1, 1, 1, Color.RED);
			success=false;
		}
		else { et_user.setShadowLayer(0, 0, 0, Color.TRANSPARENT); }
		
		//Verify password
		if(et_pass.getText()==null) {
			Log.i("LC", "Settings Error: no password");
			et_pass.setShadowLayer(1, 1, 1, Color.RED);
			success=false;
		}
		else { et_user.setShadowLayer(0, 0, 0, Color.TRANSPARENT); }
		
		//Verify thread field
		if(et_thread.getText()==null) {
			Log.i("LC", "Settings Error: no threads");
			et_thread.setShadowLayer(1, 1, 1, Color.RED);
			success=false;
		}
		else {
			sb=new StringBuilder();
			sb.append(et_thread.getText());
			int thread=Integer.parseInt(sb.toString());
			if (thread<1) {
				Log.i("LC", "Settings Error: must have atleast 1 thread");
				et_thread.setShadowLayer(1, 1, 1, Color.RED);
				success=false;
			}
			else { et_thread.setShadowLayer(0, 0, 0, Color.TRANSPARENT); }
		}
		
		// Verify non-0 throttle
		if(sb_throttle.getProgress()==0) {sb_throttle.setProgress(1);}
		
		// Verify scantime
		if(et_scanTime.getText()==null) {
			Log.i("LC", "Settings Error: no scan time");
			et_scanTime.setShadowLayer(1, 1, 1, Color.RED);
			success = false;
		}
		else {
			sb=new StringBuilder();
			sb.append(et_scanTime.getText());
			int scanTime=Integer.parseInt(sb.toString());
			if (scanTime<1)
			{
				Log.i("LC", "Settings Error: scantime must be greater than 1ms");
				et_scanTime.setShadowLayer(1, 1, 1, Color.RED);
				success=false;
			}
			else { et_scanTime.setShadowLayer(0, 0, 0, Color.TRANSPARENT); }
		}
		
		// Verify retry pause
		if(et_retryPause.getText()==null) {
			Log.i("LC", "Settings Error: no retry pause");
			et_retryPause.setShadowLayer(1, 1, 1, Color.RED);
			success=false;
		}
		else {
			sb=new StringBuilder();
			sb.append(et_retryPause.getText());
			int scanTime=Integer.parseInt(sb.toString());
			if (scanTime<1)
			{
				Log.i("LC", "Settings Error: retry pause must be greater than 1ms");
				et_retryPause.setShadowLayer(1, 1, 1, Color.RED);
				success=false;
			}
			else { et_retryPause.setShadowLayer(0, 0, 0, Color.TRANSPARENT); }
		}
		Log.i("LC", "Settings: Settings Verified");
		
		// warn about max priority threads
		if (spn_priority.getSelectedItemPosition()==2)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Warning");
			builder.setMessage("Setting to Thread.MAX_PRIORITY may cause instability");
			builder.setCancelable(false);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // do nothing
			           }
			       });
			builder.show();       
		}
		
		return success;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("LC", "Settings: onCreate");
		setContentView(R.layout.activity_settings);
		super.onCreate(savedInstanceState);
		
		et_serv =(EditText) findViewById(R.id.settings_editText_server);
		et_user =(EditText) findViewById(R.id.settings_editText_user);
		et_pass =(EditText) findViewById(R.id.settings_editText_pass);
		et_thread = (EditText) findViewById(R.id.settings_editText_threads);
		et_scanTime = (EditText) findViewById(R.id.settings_editText_scantime);
		et_retryPause= (EditText) findViewById(R.id.settings_editText_retrypause);
		sb_throttle = (SeekBar) findViewById(R.id.settings_seekBar_throttle);
		cb_service = (CheckBox) findViewById(R.id.settings_checkBox_background);
		cb_donate = (CheckBox) findViewById(R.id.settings_checkBox_donate);
		btn_default = (Button) findViewById(R.id.settings_btn_default);
		btn_save = (Button) findViewById(R.id.settings_btn_save);
		btn_load = (Button) findViewById(R.id.settings_btn_load);
		tv_throttle=(TextView) findViewById(R.id.settings_textView_throttle_lbl);
		spn_priority = (Spinner) findViewById(R.id.settings_spinner_priority);
		//Setup throttle seek bar
		
		sb_throttle.setMax(100);
		sb_throttle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (seekBar.getProgress()<1) {
					seekBar.setProgress(1);
				}
				tv_throttle.setText("Throttle: "+progress+"%");
			}

		});
		
		// Setup buttons
		btn_default.setOnClickListener( new Button.OnClickListener() {
			public void onClick(View v) {
				loadDefault();
			}
		});
		btn_save.setOnClickListener( new Button.OnClickListener() {
			public void onClick(View v) {
				saveSettings();
			}
			
		});
		btn_load.setOnClickListener( new Button.OnClickListener() {
			public void onClick(View v) {
				loadSettings();
			}
		});
		
		//Setup nav spinner
		Spinner spn_nav = (Spinner) findViewById(R.id.settings_spinner_nav);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this,
		        R.array.nav, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spn_nav.setAdapter(adapter);
		spn_nav.setSelection(1);
		spn_nav.setOnItemSelectedListener(new SpinnerListener(1));
		
		
		//Setup thread spinner
		Spinner spn_pri = (Spinner) findViewById(R.id.settings_spinner_priority);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<?> adapter2 = ArrayAdapter.createFromResource(this,
				        R.array.priority, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spn_pri.setAdapter(adapter2);
		spn_pri.setSelection(1);
		
		//Load Defaults
		if (fresh_run==true) {loadSettings();}
		fresh_run=false;
		
	}
}

	

