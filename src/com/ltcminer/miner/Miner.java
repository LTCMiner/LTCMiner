package com.ltcminer.miner;
import java.net.MalformedURLException;
import com.ltcminer.miner.MainActivity;
import com.ltcminer.miner.Console;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.ltcminer.miner.R;

public class Miner implements Observer {
	private static final long DEFAULT_SCAN_TIME = 5000;
	private static final long DEFAULT_RETRY_PAUSE = 30000;

	private Worker worker;
	private long lastWorkTime;
	private long lastWorkHashes;
	private float speed=0;			//khash/s
	public long accepted=0;
	public long rejected=0;
	public int priority=1;
	private Handler mainHandler;
	private Console console;
	private Thread t;
	
	public String status="Not Mining";
	final int MSG_UIUPDATE = 1;
	final int MSG_TERMINATED = 2;
	final int MSG_SPEED_UPDATE = 3;
	final int MSG_STATUS_UPDATE = 4;
	final int MSG_ACCEPTED_UPDATE = 5;
	final int MSG_REJECTED_UPDATE = 6;
	final int MSG_CONSOLE_UPDATE = 7;

	
	public Miner(String url, String auth, long scanTime, long retryPause, 
			     int nThread, double throttle, int pri, Handler h, Console c) {
		Log.i("LC", "Miner:Miner()");
		status="Connecting";
		speed=0.0f;
		mainHandler=h;
		console = c;
		priority=pri;

		if (nThread < 1) {
			Log.i("LC", "Invalid number of threads:"+nThread);
			console.write("Miner: Invalid number of threads");
			}
		if (throttle <= 0.0 || throttle > 1.0) {
			Log.i("LC", "Invalid throttle:"+ throttle);
			console.write("Miner:Invalid throttle");
			}
		if (scanTime < 1L) {
			Log.i("LC", "Invalid scan time");
			console.write("Miner:Invalid scan time");
			 }
		if (retryPause < 0L) {
			Log.i("LC", "Invalid retry pause:"+retryPause);
			console.write("Miner: Invalid retry pause");
		}
		
		try {
			worker = new Worker(new URL(url), auth, scanTime, retryPause, nThread, throttle, console);
			console.write("Miner: Worker created");
		} catch (MalformedURLException e) {
			Log.i("LC", "Invalid URL:");
			console.write("Miner: Invalid url");
		}

	}
	
	public void start()
	{
		Log.i("LC", "Miner:start()");
		
		t = new Thread(worker);
		worker.addObserver(this);
		t.setPriority(priority);
		Log.i("LC", "Starting Worker Thread");
		console.write("Miner: Starting worker thread, priority: "+priority);
		t.start();
	}
	
	
	public void stop () {
		Log.i("LC", "Miner:stop()");
		console.write("Miner: Worker stopping...");
		worker.stop();
		t.interrupt();
		speed=0;
	}
		
	public void update(Observable o, Object arg) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		msg.setData(bundle); // ensure msg has something
		
		Worker.Notification n = (Worker.Notification) arg;
		if (n == Worker.Notification.SYSTEM_ERROR) {
			Log.i("LC", "system error");
			android.util.Log.i("LC","System error");
			console.write("Miner: System error");
			
			status="Error";
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.PERMISSION_ERROR) {
			Log.i("LC", "permission error");
			android.util.Log.i("LC","Permission error");
			console.write("Miner: Permission error");
			
			status="Error";
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n== Worker.Notification.TERMINATED) {
			Log.i("LC", "Miner: Worker terminated");
			console.write("Miner: Worker terminated");
			status="Terminated";
			
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
			
			msg = new Message();
			msg.arg1=MSG_TERMINATED;
			mainHandler.sendMessage(msg);
		}
		else if (n == Worker.Notification.AUTHENTICATION_ERROR) {
			android.util.Log.i("LC", "Invalid worker username or password");
			status="Error";
			console.write("Miner: Authenticaion error");
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.CONNECTION_ERROR) {
			android.util.Log.i("LC", "Connection error, retrying in " + worker.getRetryPause()/1000L + " seconds");
			status="Error";
			console.write("Miner: Connection error");
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.COMMUNICATION_ERROR) {
			android.util.Log.i("LC", "Communication error");
			status="Error";
			console.write("Miner: Communication error");
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.LONG_POLLING_FAILED) {
			android.util.Log.i("LC", "Long polling failed");
			status="Not Mining";
			console.write("Miner: Long polling failed");
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.LONG_POLLING_ENABLED) {
			android.util.Log.i("LC", "Long polling enabled");
			status="Mining";
			console.write("Miner: Long polling enabled");
			console.write("Miner: Speed updates as work is completed");
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.NEW_BLOCK_DETECTED) {
			android.util.Log.i("LC", "LONGPOLL detected new block");
			status="Mining";
			console.write("Miner: LONGPOLL detected new block");
			
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
		} 
		else if (n == Worker.Notification.POW_TRUE) {
			android.util.Log.i("LC", "PROOF OF WORK RESULT: true");
			status="Mining";
			console.write("Miner: PROOF OF WORK RESULT: true");
			accepted+=1;
			
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
			
			msg= new Message();
			bundle = new Bundle();
			msg.arg1=MSG_ACCEPTED_UPDATE;
			bundle.putLong("accepted", accepted);
			mainHandler.sendMessage(msg);
			
		} 
		else if (n == Worker.Notification.POW_FALSE) {
			android.util.Log.i("LC", "PROOF OF WORK RESULT: false");
			status="Mining";
			rejected+=1;
			bundle.putString("status", status);
			msg.arg1=MSG_STATUS_UPDATE;
			msg.setData(bundle);
			mainHandler.sendMessage(msg);
			
			msg= new Message();
			bundle = new Bundle();
			msg.arg1=MSG_REJECTED_UPDATE;
			bundle.putLong("rejected", rejected);
		} 
		else if (n == Worker.Notification.NEW_WORK) {
			if (lastWorkTime > 0L) {
				long hashes = worker.getHashes() - lastWorkHashes;
				speed = (float) hashes / Math.max(1, System.currentTimeMillis() - lastWorkTime);
				android.util.Log.i("LC", String.format("%d hashes, %.6f khash/s", hashes, speed));
				status="Mining";
				console.write("Miner: "+String.format("%d hashes, %.6f khash/s", hashes, speed));
				
				bundle.putString("status", status);
				msg.arg1=MSG_STATUS_UPDATE;
				msg.setData(bundle);
				mainHandler.sendMessage(msg);
				
				msg = new Message();
				bundle = new Bundle();
				bundle.putFloat("speed", speed);
				msg.arg1=MSG_SPEED_UPDATE;
				msg.setData(bundle);
				mainHandler.sendMessage(msg);
			}
			lastWorkTime = System.currentTimeMillis();
			lastWorkHashes = worker.getHashes();
			
		}
	}
}
