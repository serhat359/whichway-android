package com.akifbatur.whichway;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyCompassListener implements SensorEventListener{

	public void onAccuracyChanged(Sensor sensor, int accuracy){
	}

	public void onSensorChanged(SensorEvent event){
		// get the angle around the z-axis rotated
		int degree = (int)(event.values[0]);
		int angle = HelloAndroidActivity.angle;
		int newAngle = degree - angle;
		
		HelloAndroidActivity.tvHeading.setText("Heading: " + degree + " degrees");
		HelloAndroidActivity.angleDiff.setText("AngleDiff: " + angle + " degrees");
		HelloAndroidActivity.distance.setText(HelloAndroidActivity.dist);
		
		if(HelloAndroidActivity.geoSet){
			HelloAndroidActivity.geoLat.setText(Functions.convertSeconds(HelloAndroidActivity.lat));
			HelloAndroidActivity.geoLong.setText(Functions.convertSeconds(HelloAndroidActivity.lng));
		}

		// create a rotation animation (reverse turn degree degrees)
		RotateAnimation ra = new RotateAnimation(HelloAndroidActivity.currentDegree, -newAngle,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

		// how long the animation will take place
		ra.setDuration(210);

		// set the animation after the end of the reservation status
		ra.setFillAfter(true);

		// Start the animation
		HelloAndroidActivity.needle.startAnimation(ra);
		HelloAndroidActivity.currentDegree = -newAngle;
	}
}
