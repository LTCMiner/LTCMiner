package com.ltcminer.miner;
import static com.ltcminer.miner.Constants.*;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.ltcminer.miner.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class NewsActivity extends MainActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		TextView tv = (TextView) findViewById(R.id.news_textView_news);
		tv.setText("");
		tv=(TextView) findViewById(R.id.news_textView_status);
		
		// Check for network
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		// On Network
		if (networkInfo != null && networkInfo.isConnected()) {
	        tv.setText("Downloading");
	        //tv=(TextView) findViewById(R.id.news_textView_news);
			//tv.setText(downloadURL(DEFAULT_NEWS_URL));
			new DownloadNewsTask().execute(null,null,null);
	    } 
		
		// Off Network
		else {
	        tv = (TextView) findViewById(R.id.news_textView_status);
	        tv.setText("No Network");
	    }
		
		//Setup nav spinner
		Spinner spn_nav = (Spinner) findViewById(R.id.news_spinner_nav);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this,
				       	R.array.nav, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spn_nav.setAdapter(adapter);
		spn_nav.setSelection(2);
		spn_nav.setOnItemSelectedListener(new SpinnerListener(2));
		
		if(settings.getBoolean(PREF_NEWS_RUN_ONCE, false)==false)
		{
			// news has been run once, turn goto news on first app run off
			editor.putBoolean(PREF_NEWS_RUN_ONCE, true);
			editor.commit();
		}
	}
	
	private String downloadURL(String url_string)  {
		Log.i("LC", "in downloadURL");
	    InputStream is = null;
	    InputStreamReader isr=null;
	    BufferedReader br = null;
	    String response=null;
	   
	    char[] buffer = new char[DEFAULT_NEWS_DL_BUFFER];
	    
	    try {
	    	URL url = new URL(url_string);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
	        int response_code = conn.getResponseCode();
	        Log.i("LC", "NewsActivity:downloadURL:response"+response_code);
	        
	        if(response_code==200) // OK
	        {
	        	is = conn.getInputStream();
	 	        Reader reader = new InputStreamReader(is, "UTF-8");        
	 	        reader.read(buffer);
	 	        response = new String(buffer);
	 	        
	 	        //find "EOF" and remove anything after, (including "EOF")
	 	        int EOF = response.indexOf("EOF");
	 	        response=String.valueOf(response.toCharArray(), 0, EOF);
	 	        
	 	        Log.i("LC","Response="+response);
	        }
	        else // something has gone wrong
	        {
	        	response="ERROR";
	        }

	    } catch (Exception e) {
	    Log.i("LC", "NewsActivity:downloadURL:Exception1: "+e.toString());
	    e.printStackTrace();
	    
	    
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            try {
					is.close();
				} catch (IOException e) {
					Log.i("LC", "NewsActivity:downloadURL:Exception2: "+e.getMessage());
				}
	        } 
	    }
	    return response;
	}
	
	private class DownloadNewsTask extends AsyncTask<Object, String, String> {

		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return downloadURL(DEFAULT_NEWS_URL);
		}

		
		protected void onPostExecute(String result) {
			SharedPreferences settings = getSharedPreferences(PREF_TITLE, 0);
			SharedPreferences.Editor editor = settings.edit();
			TextView tv_status = (TextView) findViewById(R.id.news_textView_status);
			TextView tv_news = (TextView) findViewById(R.id.news_textView_news);
			String saved_news = settings.getString("PREF_NEWS", "");
			
			// Can't retrieve news 
			if (result.equals("ERROR")==true) {	
				tv_status.setText("Unable to update"); // Set status
				
				// No news saved, set news
				if(saved_news.equals("")==true) { 
					tv_news.setText("No saved news"); 
				} 
				
				else { tv_news.setText(saved_news); }	 // Load the saved news
			}
			
			// News downloaded, if different from saved, update
			else 
			{
				tv_status.setText("Up to date");
				tv_news.setText(result);
			
			if(saved_news.equals(result)==false) {
				editor.putString(PREF_NEWS, result); }
			
			}
			super.onPostExecute(result);
		}
	}

}
