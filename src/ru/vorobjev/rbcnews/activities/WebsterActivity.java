package ru.vorobjev.rbcnews.activities;

import ru.vorobjev.rbcnews.R;
import ru.vorobjev.rbcnews.servicies.UpdateNewsService;
import ru.vorobjev.rbcnews.utils.PreferencesHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class WebsterActivity extends FragmentActivity {
	
	public static final String UPDATE_INTERVAL = "ru.vorobjev.rbcnews.WebsterActivity.UPDATE_INTERVAL";
	
	private static final int DEFAULT_INTERVAL = 10;
	
	private AlarmManager alarmManager;
	private BroadcastReceiver br;

	
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.main);
		
		initializeAlarmManager();
		initializeBroadcastReceiver();
		startRefreshingService();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(br);
	}

	
	
	private void initializeBroadcastReceiver() {
		br = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				startRefreshingService();
			}
		};
		IntentFilter intentFilter = new IntentFilter(UPDATE_INTERVAL);
		registerReceiver(br, intentFilter);
	}
	
	private void initializeAlarmManager() {
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	}
	
	private void startRefreshingService() {
		Intent intent = new Intent(this, UpdateNewsService.class);
		PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), getIntervalMillis(), pending);
	}

	private long getIntervalMillis() {
		SharedPreferences p = PreferencesHelper.getPreferences(this);
		String defaultInterval = Integer.toString(DEFAULT_INTERVAL);
		String interval = p.getString("interval", defaultInterval);
		return Integer.parseInt(interval) * 1000;
	}

	public void getNews(View v) {
		Intent newsIntent = new Intent(this, NewsActivity.class);
		startActivity(newsIntent);
	}

	public void getPreferences(View v) {
		Intent settingsActivity = new Intent(this, PreferencesActivity.class);
		startActivity(settingsActivity);
	}

}