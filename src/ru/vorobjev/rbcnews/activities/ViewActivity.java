package ru.vorobjev.rbcnews.activities;

import ru.vorobjev.rbcnews.R;
import ru.vorobjev.rbcnews.constants.C;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ViewActivity extends ActionBarActivity {
	
	WebView myWebView;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.view);
		
		setupActionBar();
		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		initWebView();
	}
	
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		String link = getIntent().getExtras().getString(C.LINK); 
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient());
		myWebView.loadUrl(link); 
		myWebView.getSettings().setJavaScriptEnabled(true);
	}
	
	private void setupActionBar() {
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}

}
