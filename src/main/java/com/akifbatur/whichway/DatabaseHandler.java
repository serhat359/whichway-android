package com.akifbatur.whichway;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "favoritesManager";
	private static final String TABLE_FAVORITES = "favorites";

	private static final String KEY_ID = "id";
	private static final String KEY_NAME = "name";
	private static final String KEY_LAT = "latitude";
	private static final String KEY_LONG = "longitude";

	public DatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		String createTableQuery = "CREATE TABLE " + TABLE_FAVORITES + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_LAT
				+ " DOUBLE, " + KEY_LONG + " DOUBLE" + ")";
		db.execSQL(createTableQuery);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
		onCreate(db);
	}

	public long addFavorite(Favorite f){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_NAME, f.getLocation());
		values.put(KEY_LAT, f.getLatitude());
		values.put(KEY_LONG, f.getLongitude());

		long ret = db.insert(TABLE_FAVORITES, null, values);
		db.close();
		return ret;
	}

	public ArrayList<Favorite> getAllFavorites(){
		ArrayList<Favorite> favorites = new ArrayList<Favorite>();

		String selectQuery = "SELECT * FROM " + TABLE_FAVORITES;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if(cursor.moveToFirst()){
			do{
				Favorite favorite = new Favorite();
				favorite.setID(cursor.getInt(0));
				favorite.setLocation(cursor.getString(1));
				favorite.setLatitude(cursor.getDouble(2));
				favorite.setLongitude(cursor.getDouble(3));
				favorites.add(favorite);
			}
			while(cursor.moveToNext());
		}
		return favorites;
	}

	public void updateFavorite(Favorite f){ // Use only on objects got from the database
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(KEY_NAME, f.getLocation());
		values.put(KEY_LAT, f.getLatitude());
		values.put(KEY_LONG, f.getLongitude());

		db.update(TABLE_FAVORITES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(f.getId()) });
	}

	public void deleteFavorite(Favorite f){ // Use only on objects got from the database
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FAVORITES, KEY_ID + " = ?", new String[] { String.valueOf(f.getId()) });
		db.close();
	}
}
