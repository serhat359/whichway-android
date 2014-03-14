package com.akifbatur.whichway;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;

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

public class HelloAndroidActivity extends FragmentActivity{

	double lat = 0;
	double lng = 0;
	static int currentDegree = 0;
	static int angle = 0;
	static String dist = "Unknown";
	String arananYer = "";
	String sText;

	public static TextView textLat;
	public static TextView textLong;
	public static TextView tvHeading;
	public static TextView angleDiff;
	public static TextView distance;
	public static TextView geoLat;
	public static TextView geoLong;
	public static ImageView needle;

	SensorManager sm;
	MyCompassListener cl = new MyCompassListener();
	Sensor compass;
	static Vector gps = new Vector(40.98707, 29.053081); // GPS koordinatı (İstanbul)
	Vector geo = new Vector(0, 0, 1); // Geocoder koordinatı (Kuzey Kutbu)

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		// GPS acik mi degil mi?
		if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

		LocationListener ll = new MyLocationListener();
		// GPS'i dinlemeye basla
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);

		// Pusula ekranı
		textLat = (TextView)findViewById(R.id.textLat);
		textLong = (TextView)findViewById(R.id.textLong);
		tvHeading = (TextView)findViewById(R.id.tvHeading);
		angleDiff = (TextView)findViewById(R.id.angle);
		distance = (TextView)findViewById(R.id.distance);
		geoLat = (TextView)findViewById(R.id.geoLat);
		geoLong = (TextView)findViewById(R.id.geoLong);
		needle = (ImageView)findViewById(R.id.needle);

		// Sensör kurulumları
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		compass = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Konumlar arası açı hesaplama için timer
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new java.util.TimerTask(){
			@Override
			public void run(){
				angle = (int)gps.getDirection(geo);
				dist = gps.getDistance(geo);
			}
		}, 1000, 5000);
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
		sm.registerListener(cl, compass, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onPause(){
		super.onPause();
		sm.unregisterListener(cl);
	}

	public void onClick_Search(View v) throws IOException, ParserConfigurationException, SAXException{
		EditText search = (EditText)findViewById(R.id.textSearch);
		sText = search.getText().toString();
		sText = sText.replaceAll("\\s+", "+");
		// Yeni bir thread ile aranan yerin koordinatlarını al.
		new RetreiveFeedTask().execute("http://maps.googleapis.com/maps/api/geocode/xml?address=" + sText
				+ "&sensor=true");
		// Direction'ı güncelle
		// TextView glat = (TextView)findViewById(R.id.geoLat);
		// TextView glong = (TextView)findViewById(R.id.geoLong);
		// glat.setText(MyLocationListener.convertSeconds(lat));
		// glong.setText(MyLocationListener.convertSeconds(lng));
	}

	private class RetreiveFeedTask extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... params){
			try{
				URL url = new URL(params[0]);
				URLConnection conn = url.openConnection();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(conn.getInputStream());

				doc.getDocumentElement().normalize();

				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

				NodeList nList = doc.getElementsByTagName("result");

				System.out.println("----------------------------");

				for(int temp = 0; temp < nList.getLength(); temp++){
					Node nNode = nList.item(temp);
					System.out.println("\nCurrent Element :" + nNode.getNodeName());

					if(nNode.getNodeType() == Node.ELEMENT_NODE){
						Element eElement = (Element)nNode;
						System.out.println("Latitude : "
								+ eElement.getElementsByTagName("lat").item(0).getTextContent());
						System.out.println("Longitute : "
								+ eElement.getElementsByTagName("lng").item(0).getTextContent());
						lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0)
								.getTextContent());
						lng = Double.parseDouble(eElement.getElementsByTagName("lng").item(0)
								.getTextContent());
						arananYer = eElement.getElementsByTagName("formatted_address").item(0)
								.getTextContent();
						// Geo update
						geo.setCoordinates(lat, lng);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}
}
