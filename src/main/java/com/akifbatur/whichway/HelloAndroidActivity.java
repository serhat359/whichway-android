package com.akifbatur.whichway;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HelloAndroidActivity extends FragmentActivity implements DialogAddToFavorites.DialogListener{

	static double lat = 0;
	static double lng = 0;
	static int currentDegree = 0;
	static int angle = 0;
	static String dist = "Unknown";
	static List<Favorite> favoriteList = null;
	static String[] favorites = new String[0];

	public static TextView textLat;
	public static TextView textLong;
	public static TextView tvHeading;
	public static TextView angleDiff;
	public static TextView distance;
	public static TextView geoLat;
	public static TextView geoLong;
	public static ImageView needle;
	// Debug
	public static TextView debug;

	static DatabaseHandler db;
	SensorManager sm;
	MyCompassListener cl = new MyCompassListener();
	Sensor compass;
	static Vector gps = new Vector(40.98707, 29.053081); // GPS koordinatı (İstanbul)
	static boolean gpsSet = false; // Konum bulundu mu?
	static Vector geo = new Vector(90, 0); // Geocoder koordinatı (Kuzey Kutbu)
	static boolean geoSet = false; // Bir yer arandı mı?

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

		// Database kurulumu
		db = new DatabaseHandler(this);

		// Pusula ekranı
		textLat = (TextView)findViewById(R.id.textLat);
		textLong = (TextView)findViewById(R.id.textLong);
		tvHeading = (TextView)findViewById(R.id.tvHeading);
		angleDiff = (TextView)findViewById(R.id.angle);
		distance = (TextView)findViewById(R.id.distance);
		geoLat = (TextView)findViewById(R.id.geoLat);
		geoLong = (TextView)findViewById(R.id.geoLong);
		needle = (ImageView)findViewById(R.id.needle);
		// debug
		debug = (TextView)findViewById(R.id.debug);

		// Sensör kurulumları
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		compass = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Konumlar arası açı ve uzaklık hesaplama için timer
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new java.util.TimerTask(){
			@Override
			public void run(){
				if(gpsSet && geoSet){
					angle = (int)gps.getDirection(geo);
					dist = gps.getDistance(geo);
				}
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
		String sText = search.getText().toString();

		String num = "\\d+(((,|\\.)\\d+)|\\s*\\d+(\\s*\\d+)?)?";
		String pattern = "([nNsS-])?\\s*" + num + "\\s*([eEwW-]|,)\\s*" + num;

		if(sText.matches(pattern)){
			// TODO
		}
		else{
			sText = sText.replaceAll("\\s+", "+");
			// Yeni bir thread ile aranan yerin koordinatlarını al.
			new RetreiveFeedTask().execute("http://maps.googleapis.com/maps/api/geocode/xml?address=" + sText
					+ "&sensor=true");
		}
	}

	public void onClick_AddToFavorites(View v){
		DialogAddToFavorites atf = new DialogAddToFavorites();
		atf.show(getSupportFragmentManager(), "atf");
	}

	public void onClick_GetFavorites(View v){
		favoriteList = db.getAllFavorites();
		// Listeden array'e doldur
		favorites = new String[favoriteList.size()];
		for(int i = 0; i < favoriteList.size(); i++)
			favorites[i] = favoriteList.get(i).getLocation();
		new DialogShowFavorites().show(getSupportFragmentManager(), "gf");
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

				NodeList nList = doc.getElementsByTagName("result");

				for(int temp = 0; temp < nList.getLength(); temp++){
					Node nNode = nList.item(temp);
					if(nNode.getNodeType() == Node.ELEMENT_NODE){
						Element eElement = (Element)nNode;
						lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0)
								.getTextContent());
						lng = Double.parseDouble(eElement.getElementsByTagName("lng").item(0)
								.getTextContent());
						// Geo update
						geo.setCoordinates(lat, lng);
						geoSet = true;
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	}

	public void onDialogPositiveClick(DialogFragment df){
		Dialog dialog = df.getDialog();
		
		EditText et = (EditText)dialog.findViewById(R.id.locationName);
		String name = et.getText().toString();
		RadioGroup rg = (RadioGroup)dialog.findViewById(R.id.radioGroup);
		int checked = rg.getCheckedRadioButtonId();
		// TODO
		Vector pos = null;
		if(checked == R.id.radioCurrent)
			pos = gps;
		else if(checked == R.id.radioSearched)
			pos = geo;
		// debug
		else{
			debug.setText("Hata-radio");
		}
		
		long ret = db.addFavorite(new Favorite(name, pos.getLat(), pos.getLong()));
		if(ret>=0)
			new DialogMessage().setMessage("Successfully added").show(getSupportFragmentManager(), "msg");
		else
			new DialogMessage().setMessage("Could not add").show(getSupportFragmentManager(), "msg");
	}
}
