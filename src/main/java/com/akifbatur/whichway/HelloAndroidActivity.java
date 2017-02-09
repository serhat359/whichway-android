package com.akifbatur.whichway;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HelloAndroidActivity extends FragmentActivity implements DialogClickListener,
		DialogFavoriteListener{

	final boolean debugEnabled = false;

	static int currentDegree = 0;
	static int angle = 0;
	static String dist = "Unknown";
	static ArrayList<Favorite> favoriteList = null;
	static String[] favorites = new String[0];

	static TextView textLat;
	static TextView textLong;
	static TextView tvHeading;
	static TextView angleDiff;
	static TextView distance;
	static TextView geoLat;
	static TextView geoLong;
	static ImageView needle;
	static TextView debugView;

	static DatabaseHandler db;
	static SensorManager sm;
	MyCompassListener cl = new MyCompassListener();
	Sensor accelerometer;
	Sensor magnetometer;
	static Vector gps = new Vector(41, 29); // GPS koordinatı (Şu anki yerimiz)
	static boolean gpsSet = false; // Konum bulundu mu?
	static Vector geo = new Vector(90, 0); // Geocoder koordinatı (Aranan yer)
	static boolean geoSet = false; // Bir yer arandı mı?

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		hideTopBar();
		setContentView(R.layout.activity_main);

		// debug view
		debugView = (TextView)findViewById(R.id.debug);

		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		// GPS acik mi degil mi?
		if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

		LocationListener ll = new MyLocationListener();
		// GPS'i dinlemeye basla
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);

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

		// Sensör kurulumları
		sm = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		// Konumlar arası açı ve uzaklık hesaplama için timer
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new java.util.TimerTask(){
			@Override
			public void run(){
				calcDirAndDist();
			}
		}, 1000, 5000);
	}

	private void hideTopBar(){
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(com.akifbatur.whichway.R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		sm.registerListener(cl, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		sm.registerListener(cl, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause(){
		super.onPause();
		sm.unregisterListener(cl);
	}

	public void onClick_Search(@SuppressWarnings("unused") View v) throws IOException{
		EditText search = (EditText)findViewById(R.id.textSearch);
		String searchText = search.getText().toString();

		String numberPattern = "(-)?\\d+(\\.\\d+)?";
		String wholePattern = numberPattern + "\\s*(,)\\s*" + numberPattern;

		if(searchText.matches(wholePattern)){
			Pattern p = Pattern.compile(numberPattern);
			Matcher m = p.matcher(searchText);
			
			m.find();
			double lat = Double.parseDouble(m.group());
			m.find();
			double lng = Double.parseDouble(m.group());
			
			setCoordinate(lat, lng);
		}
		else{
			// Yeni bir thread ile aranan yerin koordinatlarını al.
			new RetreiveFeedTask()
					.execute("http://maps.googleapis.com/maps/api/geocode/xml?address="
							+ URLEncoder.encode(searchText, "UTF-8") + "&sensor=true");
		}
	}

	public void onClick_AddToFavorites(@SuppressWarnings("unused") View v){
		EditText search = (EditText)findViewById(R.id.textSearch);
		String sText = search.getText().toString();
		new DialogAddToFavorites().setMessage(sText).show(getSupportFragmentManager(), "atf");
	}

	public void onClick_GetFavorites(@SuppressWarnings("unused") View v){
		favoriteList = db.getAllFavorites();
		// Listeden array'e doldur
		favorites = new String[favoriteList.size()];
		for(int i = 0; i < favoriteList.size(); i++)
			favorites[i] = favoriteList.get(i).location;
		new DialogShowFavorites().show(getSupportFragmentManager(), "gf");
	}

	public void onDialogPositiveClick(DialogFragment df){
		Dialog dialog = df.getDialog();
		EditText et = (EditText)dialog.findViewById(R.id.locationName);
		String name = et.getText().toString();
		RadioGroup rg = (RadioGroup)dialog.findViewById(R.id.radioGroup);
		int checked = rg.getCheckedRadioButtonId();
		Vector pos = null;
		if(checked == R.id.radioCurrent)
			pos = gps;
		else if(checked == R.id.radioSearched)
			pos = geo;

		long retVal = db.addFavorite(new Favorite(name, pos.getLat(), pos.getLong()));
		if(retVal >= 0)
			new DialogMessage().setMessage("Successfully added").show(getSupportFragmentManager(),
					"msg");
		else
			new DialogMessage().setMessage("Could not add")
					.show(getSupportFragmentManager(), "msg");
	}

	public void favoriteChosen(int id){
		Favorite chosen = favoriteList.get(id);
		geo.setCoordinates(chosen.latitude, chosen.longitude);
		geoSet = true;
		calcDirAndDist();
	}

	public void calcDirAndDist(){
		if(gpsSet && geoSet){
			angle = (int)gps.getDirection(geo);
			dist = Functions.formatToN(gps.getDistance(geo), 3) + " km";
		}
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

				String status = doc.getElementsByTagName("status").item(0).getTextContent();

				if(status.equals("OK")){
					Node resultNode = doc.getElementsByTagName("result").item(0);

					if(resultNode.getNodeType() == Node.ELEMENT_NODE){
						Element resultElement = (Element)resultNode;
						double lat = Double.parseDouble(resultElement.getElementsByTagName("lat")
								.item(0).getTextContent());
						double lng = Double.parseDouble(resultElement.getElementsByTagName("lng")
								.item(0).getTextContent());
						
						setCoordinate(lat, lng);
					}
				}
				else{
					debug(status);
				}
			}
			catch(Exception e){
				e.printStackTrace();
				debug(e.getMessage());
			}

			return null;
		}
	}

	private void setCoordinate(double lat, double lng){
		geo.setCoordinates(lat, lng);
		geoSet = true;
		calcDirAndDist();
	}

	private void debug(String text){
		if(debugEnabled)
			debugView.setText(text);
	}
}
