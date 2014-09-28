package com.example.mode;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GoogleMapActivity extends FragmentActivity implements LocationListener {
	 
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    LocationManager locationManager;
    OnLocationChangedListener mListener;
    Double latitude, longitude, mlatitude, mlongitude;
    String addressText;
    Location location = null;
    Intent intent;
    DataBaseHelper helper;
	SQLiteDatabase db;
	Cursor cursor;
	
    
    private static double POINT_RADIUS;
    private static final long PROX_ALERT_EXPIRATION = -1;
    private static final String PROX_ALERT_INTENT = "com.android.proximity.ProximityIntentReceiver";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maplayout);
        
        intent = getIntent();
        
        Log.e("XXXXXXX", intent.getStringExtra("RADIUS"));
        
        helper = new DataBaseHelper(this);
		db = helper.getWritableDatabase();
		cursor = db.rawQuery("SELECT * FROM information" , null);
 
        SupportMapFragment supportMapFragment = (SupportMapFragment)
        getSupportFragmentManager().findFragmentById(R.id.mapview);
 
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(locationManager != null) {
        	boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        	boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        	if(gpsIsEnabled)
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
        	else if(networkIsEnabled)
        		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
        }
        
        
        Button btn_find = (Button) findViewById(R.id.button);   
        btn_find.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText etLocation = (EditText) findViewById(R.id.edit);
				 
                String location = etLocation.getText().toString();
 
                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
			}
        });

        Button btn_save = (Button) findViewById(R.id.button2);
        btn_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				//Toast.makeText(GoogleMapActivity.this, "접근경고 시작", Toast.LENGTH_LONG).show();
				
				addProximityAlert(latitude, longitude);
				
				//intent = getIntent();
				intent.putExtra("ADDRESS", addressText);
				intent.putExtra("Latitude", latitude);
				intent.putExtra("Longitude", longitude);
				setResult(RESULT_OK, intent);
				finish();
			}
        });        
    }
    
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }
    
    
    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{
 
        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }
 
        @Override
        protected void onPostExecute(List<Address> addresses) {
 
            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }
 
            googleMap.clear();
 
            for(int i=0;i<addresses.size();i++){
 
                Address address = (Address) addresses.get(i);
                
                latitude = address.getLatitude();
                longitude = address.getLongitude();
 
                latLng = new LatLng(latitude, longitude);
 
                addressText = String.format("%s, %s",
                address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                address.getCountryName());
 
                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);
 
                googleMap.addMarker(markerOptions);
                
                Log.e("XXXXXXX", intent.getStringExtra("RADIUS"));
                POINT_RADIUS = Double.parseDouble(intent.getStringExtra("RADIUS"));
                
                Circle circle = googleMap.addCircle(new CircleOptions()
                								.center(latLng)
                								.radius(POINT_RADIUS)
                								.strokeColor(Color.RED));
 
                if(i==0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            }
        }
    }


	@Override
	public void onLocationChanged(Location mlocation) {
		AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
	
		mlatitude = mlocation.getLatitude();
		mlongitude = mlocation.getLongitude();
			
		if(latitude != null && longitude != null) {
			location = new Location("location");
			location.setLatitude(latitude);
			location.setLongitude(longitude);
		
			
			float distance = mlocation.distanceTo(location);
			//Toast.makeText(this, distance +"meters", Toast.LENGTH_LONG).show();
			//Toast.makeText(this, cursor.getString(5), Toast.LENGTH_SHORT).show();	
			if((intent.getStringExtra("TIME")).equals("2~3km 벗어날 때")) {	
				Log.e("XXXXXXX", intent.getStringExtra("TIME"));
				if(distance > POINT_RADIUS) {
					Toast.makeText(this, "목적지 도착", Toast.LENGTH_LONG).show();
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
			else {
				Log.e("XXXXXXX", intent.getStringExtra("TIME"));
				if(distance <= POINT_RADIUS) {
					Toast.makeText(this, "목적지 도착", Toast.LENGTH_LONG).show();
					if((intent.getStringExtra("MODE")).equals("벨소리")) {
						Log.e("XXXXXXX", intent.getStringExtra("MODE"));
						audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
					}
					else if((intent.getStringExtra("MODE")).equals("진동")) {
						Log.e("XXXXXXX", intent.getStringExtra("MODE"));
						audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
					}
					else {
						Log.e("XXXXXXX", intent.getStringExtra("MODE"));
						audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
				}
			}
		}
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		String message = null;
		if(LocationManager.GPS_PROVIDER.equals(provider)) {
			switch(status) {
			case LocationProvider.AVAILABLE:
				message = "정상적으로 서비스를 제공합니다.";
				break;
			case LocationProvider.OUT_OF_SERVICE:
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				message = "잠시 기다려 주시기 바랍니다.";
				break;
			}
		} else message = "위치정보사업자가 변경되었습니다.";
		
		Toast.makeText(GoogleMapActivity.this, message, Toast.LENGTH_LONG).show();
	}
	
	MyReceiver receiver;
	
	private void addProximityAlert(double latitude, double longitude) {
		// 브로드캐스트 리시버가 메세지를 받을 수 있도록 설정
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		registerReceiver(receiver, filter);
		
		// ProximityAlert 등록
		Intent intent2 = new Intent(PROX_ALERT_INTENT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
		locationManager.addProximityAlert(latitude, longitude, (float)POINT_RADIUS, PROX_ALERT_EXPIRATION, proximityIntent);

		Toast.makeText(GoogleMapActivity.this, "접근경고 시작", Toast.LENGTH_LONG).show();
	}
	
	protected void onResume() {
	      super.onResume();
	      Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	      if(loc != null) {
	    	  onLocationChanged(loc);
	      }
	      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);

	   } 
}