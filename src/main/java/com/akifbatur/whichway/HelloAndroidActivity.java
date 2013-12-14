package com.akifbatur.whichway;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HelloAndroidActivity extends FragmentActivity{
	
	public static GoogleMap mGoogleMap;
	public static final LatLng LOCATION_MARMARA = new LatLng(40.98707, 29.053081);
	public static final LatLng LOCATION_ANKARA = new LatLng(39.92101, 32.854183);
	
	public static double lat = 0;
	public static double lng = 0;
	public static String arananYer = "";
	
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
	private String sText;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mGoogleMap = fragment.getMap();
        mGoogleMap.addMarker(new MarkerOptions().position(LOCATION_MARMARA).title("Find me here"));
        
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
	
	public void onClick_Istanbul(View v)
	{
		//CameraUpdate update = CameraUpdateFactory.newLatLng(LOCATION_MARMARA);
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_MARMARA,8);
		mGoogleMap.animateCamera(update);
	}
	
	public void onClick_Marmara(View v)
	{
		mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOCATION_MARMARA,20);
		mGoogleMap.animateCamera(update);
	}

	public void onClick_Search(View v) throws IOException, ParserConfigurationException, SAXException
	{	
		//haritadaki ikonları sil.
		mGoogleMap.clear();
		//Serch kutusundaki string'i al.
		EditText search = (EditText) findViewById(R.id.textSearch);
		sText = search.getText().toString();
		//boşlukları + işaretine çevir
		sText= sText.replaceAll("\\s+", "+");
		//Yeni bir thread ile aranan yerin koordinatlarını al.
		new RetreiveFeedTask().execute("http://maps.googleapis.com/maps/api/geocode/xml?address="+sText+"&sensor=true");
	}	
	
	/**
	 * Background task sınıfı
	 * Arama kutusundaki string'i alıp url'e ekliyoruz.
	 * Eğer sonuç bulunduysa parse ediyoruz ve lat, lng değişkenlerine atıyoruz. 
	 * onPostExecute ile lat,lng koordinatlarına gidiyoruz. 
	 */
	private class RetreiveFeedTask extends AsyncTask<String, Integer, String>
	{
		@Override
		protected String doInBackground(String... params) 
		{
			try 
			{	 
				URL url = new URL(params[0]);
				URLConnection conn = url.openConnection();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(conn.getInputStream());
			 
				doc.getDocumentElement().normalize();
			 
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			 
				NodeList nList = doc.getElementsByTagName("result");
			 
				System.out.println("----------------------------");
			 
				for (int temp = 0; temp < nList.getLength(); temp++) 
				{
			 
					Node nNode = nList.item(temp);
			 
					System.out.println("\nCurrent Element :" + nNode.getNodeName());
			 
					if (nNode.getNodeType() == Node.ELEMENT_NODE) 
					{		 
						Element eElement = (Element) nNode;
						System.out.println("Latitude : " + eElement.getElementsByTagName("lat").item(0).getTextContent());
						System.out.println("Longitute : " + eElement.getElementsByTagName("lng").item(0).getTextContent());
						lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent());
						lng = Double.parseDouble(eElement.getElementsByTagName("lng").item(0).getTextContent());
						arananYer = eElement.getElementsByTagName("formatted_address").item(0).getTextContent();
					}
					
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) 
		{
			super.onPostExecute(result);
			LatLng searchPlace = new LatLng(lat,lng);
			mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(searchPlace,16);
		    mGoogleMap.animateCamera(update);
		    mGoogleMap.addMarker(new MarkerOptions().position(searchPlace).title(arananYer));
		    Toast.makeText(getApplicationContext(), "Lat = "+lat+"\n"+"Lng = "+lng, Toast.LENGTH_LONG).show();
		}
	}
}
