package com.akifbatur.whichway;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity 
{

	public static TextView textLat;
	public static TextView textLong;
	public static TextView myAddress;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textLat = (TextView) findViewById(R.id.textLat);
		textLong = (TextView) findViewById(R.id.textLong);
		myAddress = (TextView) findViewById(R.id.myAddress);
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//GPS acik mi degil mi?
		boolean enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!enabled) 
		{
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		
		LocationListener ll = new MyLocationListener();
		//GPS'i dinlemeye basla
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.akifbatur.whichway.R.menu.main, menu);
		return true;
	}
}
