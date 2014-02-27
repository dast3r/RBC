package ru.vorobjev.rbcnews.db;

import java.text.SimpleDateFormat;

import ru.vorobjev.rbcnews.constants.C;
import ru.vorobjev.rbcnews.objects.RssItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHandler {

	private DBHelper dbHelper;
	private SQLiteDatabase database;

	// methods for all table

	public DatabaseHandler(Context context) {
		dbHelper = new DBHelper(context);

	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void clearTable(String tableName) {
		database.delete(tableName, null, null);
	}

	public Cursor getAllData(String tableName) {
		return database.query(tableName, null, null, null, null, null, null);
	}

	// news table method

	public void insertRssItem(RssItem rssItem) {
		ContentValues cv = new ContentValues();
		cv.put(C.RSS_ITEMS_TABLE_TITLE, rssItem.getTitle());
		cv.put(C.RSS_ITEMS_TABLE_DESCRIPTION, rssItem.getDescription());
		cv.put(C.RSS_ITEMS_TABLE_PUBDATE, rssItem.getPubDate() != null ? new SimpleDateFormat().format(rssItem.getPubDate()) : null);
		cv.put(C.RSS_ITEMS_TABLE_LINK, rssItem.getLink());
		
		database.beginTransaction();
		database.insert(C.RSS_ITEMS_TABLE, null, cv);
		database.setTransactionSuccessful();
		database.endTransaction();
		
	}

}