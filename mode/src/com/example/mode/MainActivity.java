package com.example.mode;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity {

	DataBaseHelper helper;
	SQLiteDatabase db;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.total);
		
		Button btn = (Button)findViewById(R.id.button);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, Table.class);
				startActivity(intent);
			}
			
		});
		
		helper = new DataBaseHelper(this);
		db = helper.getWritableDatabase();
		cursor = db.rawQuery("SELECT * FROM information" , null);
		startManagingCursor(cursor);
		
		String[] from = {"name", "mode"};
		int[] to = { android.R.id.text1, android.R.id.text2 };
		adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, from, to);
		
		ListView list = (ListView)findViewById(R.id.list2);
		list.setAdapter(adapter);
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				if(db != null)
					db.execSQL("DELETE FROM information WHERE _id=" + id);

				cursor = db.rawQuery("SELECT * FROM information", null);
				adapter.changeCursor(cursor);
				
				Toast.makeText(MainActivity.this, "LongClick _id=" + id, Toast.LENGTH_SHORT).show();
				return false;
			}
			
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add:
			Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
			startActivity(intent);
			break;
		default:
			return false;
		}
		return false;
	}
	
	
}
