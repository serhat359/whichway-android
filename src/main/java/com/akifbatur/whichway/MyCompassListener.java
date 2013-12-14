package com.akifbatur.whichway;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyCompassListener implements SensorEventListener{
	
	public void onAccuracyChanged(Sensor sensor, int accuracy){
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event){
		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);
		HelloAndroidActivity.tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");
		
		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(
				HelloAndroidActivity.currentDegree, -degree,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		
		// how long the animation will take place
		ra.setDuration(210);
		
		// set the animation after the end of the reservation status
		ra.setFillAfter(true);
		
		// Start the animation
		HelloAndroidActivity.needle.startAnimation(ra);
		HelloAndroidActivity.currentDegree = -degree;
	}
}
