package com.example.mode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PlaceActivity extends Activity {
	static final int DIALOG_PLACE = 1;
	static final int DIALOG_TIME = 2;
	static final int DIALOG_SOUNDMODE = 3;
	static final int DIALOG_RADIUS = 4;
	
	String t, myData;
	TextView text1, text2, text3, text4, text5, text6;
	String name, address, time, mode, radius;
	double latitude, longitude;
	
	DataBaseHelper helper;
	SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place);
		
		text1 = (TextView)findViewById(R.id.placename);
		text2 = (TextView)findViewById(R.id.location);
		text3 = (TextView)findViewById(R.id.latitude);
		text4 = (TextView)findViewById(R.id.time);
		text5 = (TextView)findViewById(R.id.soundmode);
		text6 = (TextView)findViewById(R.id.radius);
		
		helper = new DataBaseHelper(this);
		try {
			db = helper.getWritableDatabase();
		} catch(SQLiteException ex) {
			db = helper.getReadableDatabase();
		}
		
		Button btn = (Button)findViewById(R.id.save);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				db.execSQL("INSERT INTO information VALUES(null,'"+ name +"','"+ address+"','"+ latitude+"','"+ longitude+"','"+ time+"','" + mode+"');");
				
				//Intent intent = new Intent(PlaceActivity.this, Total.class);
				//startActivity(intent);
			}
		});
		 
		String[] list = {"목적지 설정", "소리 모드 설정", "반경 설정", "실행 시간 설정", "목적지 위치 설정"};
		
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		
		ListView listview = (ListView)findViewById(R.id.placelist);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				switch(position) {
				case 0:
					showDialog(DIALOG_PLACE);
					break;
				case 1:
					showDialog(DIALOG_SOUNDMODE);
					break;
				case 2:
					showDialog(DIALOG_RADIUS);
					break;
				case 3:
					showDialog(DIALOG_TIME);
					break;
				case 4:
					Intent intent = new Intent(PlaceActivity.this, GoogleMapActivity.class);
					intent.putExtra("TIME", time);
					intent.putExtra("MODE", mode);
					intent.putExtra("RADIUS", radius);
					startActivityForResult(intent, 0);
					break;
				}
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0) {
			if(resultCode == RESULT_OK) {
				address = data.getStringExtra("ADDRESS");
				latitude = data.getDoubleExtra("Latitude", 0);
				longitude = data.getDoubleExtra("Longitude", 0);
				text2.setText("목적지 위치 : " + address);
				text3.setText("위도 / 경도 : " + latitude + " / " + longitude);	
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_PLACE:
			AlertDialog.Builder builder = new AlertDialog.Builder(PlaceActivity.this);
			builder.setTitle("목적지 설정");
			final View v1 = View.inflate(PlaceActivity.this, R.layout.placesetting, null);
			builder.setView(v1);
			builder.setCancelable(false);
			builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText edit1 = (EditText)v1.findViewById(R.id.edit1);
					name = edit1.getText().toString();
					Toast.makeText(getApplicationContext(), edit1.getText(), Toast.LENGTH_SHORT).show();
					text1.setText("목적지 이름 : " + name);

				}
			});
			builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					text1.setText("목적지 이름 : ");
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		
		case DIALOG_TIME:
			final CharSequence[] items = {"목적지를 벗어날 때", "목적지 근방에 도착할 때"};
			
			final AlertDialog.Builder builder2 = new AlertDialog.Builder(PlaceActivity.this);
			builder2.setTitle("실행시간 설정");
			builder2.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					time = (String) items[item];
					text4.setText("실행 시간 : " + time);
				}
			});
			builder2.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					builder2.setItems(items, new OnClickListener() {
	
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
						
					});					
				}
			});
			builder2.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					text4.setText("실행 시간 : ");	
				}
			});
			AlertDialog alert2 = builder2.create();
			return alert2;
		
	
		case DIALOG_SOUNDMODE:
			final CharSequence[] items2 = {"벨소리", "진동", "무음"};
	
			final AlertDialog.Builder builder3 = new AlertDialog.Builder(PlaceActivity.this);
			builder3.setTitle("소리 모드 설정");
			builder3.setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					mode = (String) items2[item];
					text5.setText("소리 모드 : " + mode);
				}
			});
			builder3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					builder3.setItems(items2, new OnClickListener() {
	
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							text5.setText("소리 모드 : " + items2[arg1]);							
							
							Toast.makeText(getApplicationContext(), items2[arg1], Toast.LENGTH_SHORT).show();
						}
						
					});					
				}
			});
			builder3.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					text5.setText("소리 모드 : ");	
				}
			});
			AlertDialog alert3 = builder3.create();
			return alert3;
			
		case DIALOG_RADIUS:
			AlertDialog.Builder builder4 = new AlertDialog.Builder(PlaceActivity.this);
			builder4.setTitle("경도 설정");
			final View v2 = View.inflate(PlaceActivity.this, R.layout.placeradius, null);
			builder4.setView(v2);
			builder4.setCancelable(false);
			builder4.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					EditText edit3 = (EditText)v2.findViewById(R.id.edit3);
					radius = edit3.getText().toString();
					Toast.makeText(getApplicationContext(), edit3.getText(), Toast.LENGTH_SHORT).show();
					text6.setText("경도 : " + radius);

				}
			});
			builder4.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					text6.setText("경도 : ");
				}
			});
			AlertDialog alert4 = builder4.create();
			return alert4;
			
		}
		
		return null;
	}
}
