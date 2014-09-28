package com.example.mode;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.mode.Alarm;
import com.example.mode.TimeActivity;

public class TimeActivity extends Activity {

	int myHour, myMinute, setHour, setMinute;
	static final int DIALOG_SOUNDMODE = 0;
	public static CharSequence sound;
	TextView time, mode;
	String setTime, setMode;
	
	DataBaseHelper helper;
	SQLiteDatabase db;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time);
		
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
				db.execSQL("INSERT INTO information VALUES(null,'"+ setTime +"', null, null, null, '"+ setTime +"','" + setMode+"');");
				
			}
			
		});
		time = (TextView)findViewById(R.id.time);
		mode = (TextView)findViewById(R.id.mode);
		
		String[] list = {"시간설정", "소리모드설정"};
		
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(TimeActivity.this, android.R.layout.simple_list_item_1, list);
		
		ListView listview = (ListView)findViewById(R.id.timelist);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				Calendar c = Calendar.getInstance();
				
				switch(position) {
				case 0:
					myHour = c.get(Calendar.HOUR_OF_DAY);
					myMinute = c.get(Calendar.MINUTE);
					Dialog dlgtime = new TimePickerDialog(TimeActivity.this, myTimeSetListener, myHour, myMinute, false);
					dlgtime.show();					
					break;
				case 1:
					showDialog(DIALOG_SOUNDMODE);
					break;
				}
			}
		});
	}

	private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			//String time = String.valueOf(hourOfDay) + "시 "+ String.valueOf(minute) +"분";
			//Toast.makeText(DBInsert.this, time, Toast.LENGTH_LONG).show();
			Toast.makeText(TimeActivity.this, (hourOfDay-myHour)+"시간 "+(minute-myMinute)+"분 후에 알람", Toast.LENGTH_LONG).show();
			
			setHour = hourOfDay;
			setMinute = minute;
			
			setTime = setHour + "시 " + setMinute + "분";
			time.setText("시간 : " + setTime);
			
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_SOUNDMODE:
			final CharSequence[] items = {"벨소리", "진동", "무음"};

			final AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
			builder.setTitle("소리 모드 설정");
			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					sound = items[item];
					
				}
			});
			builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {		
					//벨소리 지정ㅎ면 알람전송(시간만 지정했을때엔 알람X)
					setMode= (String) sound;
					mode.setText("소리모드 : " + setMode);
					
					// 알람 매니저에 등록할 인텐트를 만듬
					Intent intent = new Intent(TimeActivity.this, Alarm.class);
					intent.putExtra("sound", setMode);
					Log.e("XXXX","sound : "+ setMode);
					PendingIntent sender = PendingIntent.getBroadcast(TimeActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					
					
					// 알람을 받을 시간 설정
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(Calendar.HOUR_OF_DAY, setHour);
					calendar.set(Calendar.MINUTE, setMinute);
					
					// 알람매니저에 알람 등록
					AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
				}
				
			});
			builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		}
		return null;
	
	}
}