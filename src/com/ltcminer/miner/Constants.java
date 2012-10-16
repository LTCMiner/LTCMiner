package com.ltcminer.miner;

public class Constants {

	public static final int MSG_UIUPDATE = 1;
	public static final int MSG_TERMINATED = 2;
	public static final int MSG_SPEED_UPDATE = 3;
	public static final int MSG_STATUS_UPDATE = 4;
	public static final int MSG_ACCEPTED_UPDATE = 5;
	public static final int MSG_REJECTED_UPDATE = 6;
	public static final int MSG_CONSOLE_UPDATE = 7;
	
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
	public static final String PREF_BACKGROUND="BACKGROUND";
	public static final String PREF_NEWS="NEWS";
	public static final String PREF_NEWS_RUN_ONCE="NEWS_RUN_ONCE";
	
	public static final String DEFAULT_URL="http://litecoinpool.org:9332";
	public static final String DEFAULT_USER="Username";
	public static final String DEFAULT_PASS="Password";
	
	public static final String DONATE_URL="http://litecoinpool.org:9332";
	public static final String DONATE_USER="raad287.3";
	public static final String DONATE_PASS="3";
	
	public static final int 		DEFAULT_PRIORITY=1;
	public static final int 		DEFAULT_THREAD=1;
	public static final long 	DEFAULT_SCANTIME=500;
	public static final long 	DEFAULT_RETRYPAUSE=500;
	public static final float 	DEFAULT_THROTTLE=1;
	public static final boolean 	DEFAULT_DONATE = false;
	public static final boolean 	DEFAULT_BACKGROUND = false;
	public static final int		DEFAULT_NEWS_DL_BUFFER=20000;
	
	public static final String DEFAULT_NEWS_URL="http://ltcminer.net84.net/NEWS";
	
	
}
