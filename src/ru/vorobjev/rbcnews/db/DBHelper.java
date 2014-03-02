package ru.vorobjev.rbcnews.db;

import ru.vorobjev.rbcnews.constants.C;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context) {
		super(context, C.DATABASE, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL("DROP TABLE IF EXISTS " + C.RSS_ITEMS_TABLE);
		db.execSQL("create table " + C.RSS_ITEMS_TABLE + " ("
				+ "_id integer primary key autoincrement,"
				+ C.RSS_ITEMS_TABLE_TITLE + " text," 
				+ C.RSS_ITEMS_TABLE_DESCRIPTION + " text,"
				+ C.RSS_ITEMS_TABLE_PUBDATE + " text,"
				+ C.RSS_ITEMS_TABLE_LINK + " text"
				+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " +  C.RSS_ITEMS_TABLE);
        onCreate(db);
	}

}