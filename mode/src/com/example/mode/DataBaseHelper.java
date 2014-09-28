package com.example.mode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{
	
	public static final String DATABASE_NAME = "information.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "information";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String DROP_SQL = "drop table if exists " + TABLE_NAME;
			db.execSQL(DROP_SQL);
		} catch(Exception ex) {
			Log.e("TAG", "Exception in DROP_SQL", ex);
		}
		
		String CREATE_SQL = "create table " + TABLE_NAME + "("
							+ " _id integer PRIMARY KEY autoincrement, "
							+ " name text, "
							+ " address text, "
							+ " latitude text, "
							+ " longitude text, "
							+ " time text, "
							+ " mode text)";

		try {
			db.execSQL(CREATE_SQL);
		} catch(Exception ex) {
			Log.e("ERROR", "Exception in CREATE_SQL", ex);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String DROP_SQL = "drop table if exists " + TABLE_NAME;
		db.execSQL(DROP_SQL);
		
		onCreate(db);
	}

}
