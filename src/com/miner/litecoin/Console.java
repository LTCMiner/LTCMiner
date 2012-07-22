package com.miner.litecoin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.raad287.LTCMiner.R;

public class Console {
	final int MSG_UIUPDATE = 1;
	final int MSG_STATUPDATE = 2;
	final int MSG_CONSOLE_UPDATE = 7;
	FileReader fr;
	BufferedReader br;
	FileWriter fw;
	BufferedWriter bw;
	File logFile=new File("logfile.txt");

	
	public Console()
	 {
		//Initialize line_array
		
		 try {
			logFile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(logFile));
		 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i("LC", "Console: IOException"+e.getMessage());
			e.printStackTrace();
		} 
			
	 }

	public void write (String string)
	{
		Calendar c = Calendar.getInstance();
		Date d = c.getTime();
		String dateTag = Integer.toString(c.get(Calendar.MONTH))+Integer.toString(c.get(Calendar.DAY_OF_WEEK_IN_MONTH))+
						 Integer.toString(c.get(Calendar.HOUR_OF_DAY))+Integer.toString(c.get(Calendar.MINUTE))+
						 Integer.toString(c.get(Calendar.SECOND));
			try {
				//insert timestamp code here
				bw.append(dateTag+":"+string);
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public String last20() 
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(logFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		StringBuilder sb = new StringBuilder();
		int count=0;
		String line="";
		while (line!=null && count<20)
		{
			try {
				line=br.readLine();
				sb.append(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
		}
		return sb.toString();
	
	}
		
	}

