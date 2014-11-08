package com.akifbatur.whichway;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener{

	public void onLocationChanged(Location location){
		if(location != null){
			double pLong = location.getLongitude();
			double pLat = location.getLatitude();
			HelloAndroidActivity.textLat.setText(Functions.convertSeconds(pLat));
			HelloAndroidActivity.textLong.setText(Functions.convertSeconds(pLong));
			HelloAndroidActivity.gps.setCoordinates(pLat, pLong);
			HelloAndroidActivity.gpsSet = true;
		}
		else{
			HelloAndroidActivity.textLat.setText("Unknown");
			HelloAndroidActivity.textLong.setText("Unknown");
		}
	}

	public void onProviderDisabled(String provider){
	}

	public void onProviderEnabled(String provider){
	}

	public void onStatusChanged(String provider, int status, Bundle extras){
	}
}