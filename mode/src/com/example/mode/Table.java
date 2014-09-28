package com.example.mode;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class Table extends Activity{
	DataBaseHelper helper;
	SQLiteDatabase db;
	Cursor cursor;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table);
		
		helper = new DataBaseHelper(this);
		db = helper.getWritableDatabase();
		cursor = db.rawQuery("SELECT * FROM information" , null);
		
		TextView text = (TextView)findViewById(R.id.text);
		
		String str = "";
		while(cursor.moveToNext()) {
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			String address = cursor.getString(2);
			String latitude = cursor.getString(3);
			String longitude = cursor.getString(4);
			String time = cursor.getString(5);
			String mode = cursor.getString(6);
			
			str += "id : " + id + "\n"
				+ "name : " + name + "\n"
				+ "address : " + address + "\n"
				+ "latitude : " + latitude + "\n"
				+ "longitude : " + longitude + "\n"
				+ "time : " + time + "\n"
				+ "mode : " + mode + "\n\n";
		}
		text.setText(str);
		
		
	}
	
}
