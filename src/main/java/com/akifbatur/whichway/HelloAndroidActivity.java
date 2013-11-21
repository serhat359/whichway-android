package com.akifbatur.whichway;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;
import android.widget.ImageView;

public class HelloAndroidActivity extends Activity{
	public static TextView textLat;
	public static TextView textLong;
	public static TextView magX;
	public static TextView magY;
	public static TextView magZ;
	public static ImageView needle;
	public static float currentDegree = 0f;
	public static TextView tvHeading;
	SensorManager sm;
	MyMagnetoListener ml = new MyMagnetoListener();
	MyCompassListener cl = new MyCompassListener();
	Sensor magneto; //Manyetometre sensörü
	Sensor compass; //Yön sensörü

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textLat = (TextView) findViewById(R.id.textLat);
		textLong = (TextView) findViewById(R.id.textLong);
		
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		//GPS acik mi degil mi?
		if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		
		LocationListener ll = new MyLocationListener();
		//GPS'i dinlemeye basla
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		
		//Manyetik alan ölçme
		magX = (TextView) findViewById(R.id.textX);
		magY = (TextView) findViewById(R.id.textY);
		magZ = (TextView) findViewById(R.id.textZ);
		
		//Pusula ekranı
		needle = (ImageView) findViewById(R.id.needle);
		tvHeading = (TextView) findViewById(R.id.tvHeading);
		
		//Sensör kurulumları
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		magneto = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		compass = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.akifbatur.whichway.R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		sm.registerListener(ml, magneto, SensorManager.SENSOR_DELAY_NORMAL);
		sm.registerListener(cl, compass, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause(){
		super.onPause();
		sm.unregisterListener(ml);
		sm.unregisterListener(cl);
	}
}
