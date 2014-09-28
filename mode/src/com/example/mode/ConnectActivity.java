package com.example.mode;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

public class ConnectActivity extends TabActivity {

	TabHost tabhost;
	DataBaseHelper helper;
	SQLiteDatabase db;
	PlaceActivity place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		tabhost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent = null;
		
		intent = new Intent().setClass(this, PlaceActivity.class);
		spec = tabhost.newTabSpec("PlaceActivity").setIndicator("장소설정").setContent(intent);
		tabhost.addTab(spec);
		
		intent = new Intent().setClass(this, TimeActivity.class);
		spec = tabhost.newTabSpec("TimeActivity").setIndicator("시간설정").setContent(intent);
		tabhost.addTab(spec);
		
		tabhost.setCurrentTab(0);
		
		helper = new DataBaseHelper(this);
		try {
			db = helper.getWritableDatabase();
		} catch(SQLiteException ex) {
			db = helper.getReadableDatabase();
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.main:
				finish();
			break;
		default:
			return false;
		}
		return false;
	}
}

