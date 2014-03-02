package ru.vorobjev.rbcnews.activities;


import ru.vorobjev.rbcnews.R;
import ru.vorobjev.rbcnews.constants.C;
import ru.vorobjev.rbcnews.db.DBProvider;
import ru.vorobjev.rbcnews.servicies.UpdateNewsService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NewsActivity extends ActionBarActivity implements
		android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

	public final static String REFRESH_COMPLETE = "ru.vorobjev.rbcnews.NewsActivity.broadcast";
	
	PullToRefreshListView lvData;
	SimpleCursorAdapter scAdapter;
	BroadcastReceiver br;
	Cursor cursor;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.news);
		
		setupActionBar();

		initList();

		initBroadcastReceiver();

		getSupportLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(br);
		super.onDestroy();
	}
	
	

	private void initBroadcastReceiver() {
		br = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				lvData.onRefreshComplete();
				getSupportLoaderManager().getLoader(0).forceLoad();
				int status = intent.getIntExtra(C.PARAM_STATUS, 0);
				if (status == C.STATUS_BAD) {
					String exception = intent.getStringExtra(C.PARAM_EXCEPTION);
					Toast.makeText(NewsActivity.this, exception, Toast.LENGTH_LONG).show();
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter(REFRESH_COMPLETE);
		registerReceiver(br, intentFilter);
	}

	private void initList() {
		String[] from = new String[] { C.RSS_ITEMS_TABLE_TITLE, C.RSS_ITEMS_TABLE_PUBDATE };
		int[] to = new int[] { R.id.title, R.id.date };

		scAdapter = new SimpleCursorAdapter(this, R.layout.item, cursor, from, to, 0);
		lvData = (PullToRefreshListView) findViewById(R.id.list);
		lvData.setAdapter(scAdapter);
		lvData.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				Intent intent = new Intent(NewsActivity.this, UpdateNewsService.class);
				startService(intent);
			}
		});
		lvData.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CursorWrapper item = (CursorWrapper) scAdapter.getItem(position);
				String link = item.getString(item.getColumnIndex(C.RSS_ITEMS_TABLE_LINK));
				if (link != null) {
					Intent intent = new Intent(NewsActivity.this, ViewActivity.class);
					intent.putExtra(C.LINK, link);
					startActivity(intent);
				} else {
					Toast.makeText(NewsActivity.this, "Bad link", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
		return new CursorLoader(this, DBProvider.RSS_CONTENT_URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		scAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		scAdapter.swapCursor(null);
	}

}
