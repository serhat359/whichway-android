package com.akifbatur.whichway;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener extends Activity implements LocationListener 
{
	public void onLocationChanged(Location location)
	{
		if (location != null) 
		{
			double pLong = location.getLongitude();
			double pLat = location.getLatitude();
			HelloAndroidActivity.textLat.setText(Double.toString(pLat));
			HelloAndroidActivity.textLong.setText(Double.toString(pLong));

			Geocoder geocoder = new Geocoder(this, Locale.getDefault());

			try 
			{
				List<Address> addresses = geocoder.getFromLocation(pLat,pLong, 1);

				if (addresses != null) 
				{
					Address returnedAddress = addresses.get(0);
					StringBuilder strReturnedAddress = new StringBuilder(
							"Address:\n");
					for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) 
					{
						strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
					}
					HelloAndroidActivity.myAddress.setText(strReturnedAddress.toString());
				} 
				else 
				{
					HelloAndroidActivity.myAddress.setText("No Address returned!");
				}
			} catch (IOException e) 
			{
				e.printStackTrace();
				HelloAndroidActivity.myAddress.setText("Unknown");
			}

		} 
		else 
		{
			HelloAndroidActivity.textLat.setText("Unknown");
			HelloAndroidActivity.textLong.setText("Unknown");
		}
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