package com.akifbatur.whichway;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener extends Activity implements LocationListener{
	public void onLocationChanged(Location location){
		if(location != null){
			double pLong = location.getLongitude();
			double pLat = location.getLatitude();
			HelloAndroidActivity.textLat.setText(convert(pLat));
			HelloAndroidActivity.textLong.setText(convert(pLong));
		}
		else{
			HelloAndroidActivity.textLat.setText("Unknown");
			HelloAndroidActivity.textLong.setText("Unknown");
		}
	}

	public String convert(double d){
		String s = Location.convert(d, Location.FORMAT_SECONDS);
		String[] ss = s.split(":|\\.");
		return ss[0] + "° " + ss[1] + "\" " + ss[2] + "\'";
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}