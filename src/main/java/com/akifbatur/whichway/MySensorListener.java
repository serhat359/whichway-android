package com.akifbatur.whichway;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorListener extends Activity implements SensorEventListener{
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event){
		HelloAndroidActivity.magX.setText(Float.toString(event.values[0]));
		HelloAndroidActivity.magY.setText(Float.toString(event.values[1]));
		HelloAndroidActivity.magZ.setText(Float.toString(event.values[2]));
	}
}
