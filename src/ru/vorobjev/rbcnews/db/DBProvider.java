package ru.vorobjev.rbcnews.db;

import ru.vorobjev.rbcnews.constants.C;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class DBProvider extends ContentProvider {
	final String LOG_TAG = "myLogs";

	static final String DB_NAME = "mydb";
	static final int DB_VERSION = 1;

	static final String ID = "_id";

	static final String DB_CREATE = "create table " + C.RSS_ITEMS_TABLE + " ("
			+ ID + " integer primary key autoincrement,"
			+ C.RSS_ITEMS_TABLE_TITLE + " text," 
			+ C.RSS_ITEMS_TABLE_DESCRIPTION + " text,"
			+ C.RSS_ITEMS_TABLE_PUBDATE + " text,"
			+ C.RSS_ITEMS_TABLE_LINK + " text"
			+ ");";

	static final String AUTHORITY = "ru.vorobjev.providers.rbcnews";

	static final String RSS_ITEMS_PATH = "rssItems";

	public static final Uri RSS_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + RSS_ITEMS_PATH);

	static final String RSS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
			+ AUTHORITY + "." + RSS_ITEMS_PATH;

	static final String RSS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
			+ AUTHORITY + "." + RSS_ITEMS_PATH;

	static final int URI_ITEMS = 1;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, RSS_ITEMS_PATH, URI_ITEMS);
	}

	DBHelper dbHelper;
	SQLiteDatabase db;

	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		switch (uriMatcher.match(uri)) {
		case URI_ITEMS: 
			if (TextUtils.isEmpty(sortOrder)) {
				sortOrder = C.RSS_ITEMS_TABLE_PUBDATE + " ASC";
			}
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(C.RSS_ITEMS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), RSS_CONTENT_URI);
		return cursor;
	}

	public Uri insert(Uri uri, ContentValues values) {
		if (uriMatcher.match(uri) != URI_ITEMS) {
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		long rowID = db.insert(C.RSS_ITEMS_TABLE, null, values);
		Uri resultUri = ContentUris.withAppendedId(RSS_CONTENT_URI, rowID);
		getContext().getContentResolver().notifyChange(resultUri, null);
		return resultUri;
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (uriMatcher.match(uri)) {
		case URI_ITEMS:
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.delete(C.RSS_ITEMS_TABLE, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		switch (uriMatcher.match(uri)) {
		case URI_ITEMS:
			break;
		default:
			throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.update(C.RSS_ITEMS_TABLE, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case URI_ITEMS:
			return RSS_CONTENT_TYPE;
		}
		return null;
	}

}
