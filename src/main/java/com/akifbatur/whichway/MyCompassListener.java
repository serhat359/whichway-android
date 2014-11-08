package com.akifbatur.whichway;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

public class MyCompassListener implements SensorEventListener{

	float[] mGravity;
	float[] mGeomagnetic;
	float toBeIgnored = 0f;

	public void onAccuracyChanged(Sensor sensor, int accuracy){
	}

	public void onSensorChanged(SensorEvent event){
		// get the angle around the z-axis rotated
		float azimut = getAzimut(event);
		if(azimut == toBeIgnored)
			return;

		int degree = (int)azimut;
		int angle = HelloAndroidActivity.angle;
		int newAngle = degree - angle;

		HelloAndroidActivity.tvHeading.setText("Heading: " + degree + " degrees");
		HelloAndroidActivity.angleDiff.setText("AngleDiff: " + angle + " degrees");
		HelloAndroidActivity.distance.setText(HelloAndroidActivity.dist);

		if(HelloAndroidActivity.geoSet){
			HelloAndroidActivity.geoLat.setText(Functions.convertSeconds(HelloAndroidActivity.geo
					.getLat()));
			HelloAndroidActivity.geoLong.setText(Functions.convertSeconds(HelloAndroidActivity.geo
					.getLong()));
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

	public float getAzimut(SensorEvent event){
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if(mGravity != null && mGeomagnetic != null){
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if(success){
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				return (float)Math.toDegrees(orientation[0]); // orientation contains: azimut, pitch and roll
			}
			else
				return toBeIgnored;
		}
		else
			return toBeIgnored;
	}
}
