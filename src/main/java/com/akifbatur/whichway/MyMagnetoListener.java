package com.akifbatur.whichway;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MyMagnetoListener implements SensorEventListener{
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event){
		HelloAndroidActivity.magX.setText(Float.toString(event.values[0]));
		HelloAndroidActivity.magY.setText(Float.toString(event.values[1]));
		HelloAndroidActivity.magZ.setText(Float.toString(event.values[2]));
	}
}
