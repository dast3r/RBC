package ru.vorobjev.rbcnews.servicies;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.vorobjev.rbcnews.activities.NewsActivity;
import ru.vorobjev.rbcnews.constants.C;
import ru.vorobjev.rbcnews.db.DatabaseHandler;
import ru.vorobjev.rbcnews.exceptions.ErrorStatusException;
import ru.vorobjev.rbcnews.objects.RssItem;
import ru.vorobjev.rbcnews.utils.PreferencesHelper;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class UpdateNewsService extends Service {

	@Override
	public void onCreate() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		final String feedUrl = PreferencesHelper.getPreferences(
				getBaseContext()).getString("category",
				PreferencesHelper.getDefaultCategory(getBaseContext()));
		new AsyncTask<Void, Void, Void>() {

			ErrorStatusException ex;

			@Override
			protected Void doInBackground(Void... params) {
				try {
					ArrayList<RssItem> rssItems = getRssItems(feedUrl);
					if (!rssItems.isEmpty()) {
						writeToDB(rssItems);
					}
				} catch (ErrorStatusException ex) {
					this.ex = ex;
				}
				return null;

			}

			protected void onPostExecute(Void result) {
				Intent intent = new Intent(NewsActivity.REFRESH_COMPLETE);
				if (ex != null) {
					intent.putExtra(C.PARAM_STATUS, C.STATUS_BAD);
					intent.putExtra(C.PARAM_EXCEPTION, ex.getMessage());
				}
				sendBroadcast(intent);
			};
		}.execute();
		return super.onStartCommand(intent, flags, startId);
	}

	private void writeToDB(ArrayList<RssItem> rssItems) {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		db.open();
		db.clearTable(C.RSS_ITEMS_TABLE);
		for (RssItem item : rssItems) {
			db.insertRssItem(item);
		}
		db.close();
	}

	private ArrayList<RssItem> getRssItems(String feedUrl)
			throws ErrorStatusException {

		ArrayList<RssItem> rssItems = new ArrayList<RssItem>();

		// open an URL connection make GET to the server and take xml RSS data
		URL url = null;
		try {
			url = new URL(feedUrl);
		} catch (MalformedURLException e2) {
			throw new ErrorStatusException("Bad URL");
		}
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			throw new ErrorStatusException("Can't connect to url");
		}

		int code = -2;
		try {
			code = conn.getResponseCode();
		} catch (IOException e2) {
			throw new ErrorStatusException("Server retrieve bad status code");
		}
		if (HttpURLConnection.HTTP_OK == code) {
			InputStream is = null;
			try {
				is = conn.getInputStream();
			} catch (IOException e2) {
				throw new ErrorStatusException("Server exception!");
			}

			// DocumentBuilderFactory, DocumentBuilder are used for xml parsing
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e2) {
				throw new ErrorStatusException("Bad parser configuration");
			}

			// using db (Document Builder) parse xml data and assign it to
			// Element
			Document document = null;
			try {
				document = db.parse(is);
			} catch (SAXException e1) {
			} catch (IOException e1) {
			}
			Element element = document.getDocumentElement();

			// take rss nodes to NodeList
			NodeList nodeList = element.getElementsByTagName("item");

			if (nodeList.getLength() > 0) {
				for (int i = 0; i < nodeList.getLength(); i++) {

					// take each entry (corresponds to <item></item> tags in xml
					// data

					Element entry = (Element) nodeList.item(i);

					Element _titleE = (Element) entry.getElementsByTagName(
							"title").item(0);
					Element _descriptionE = (Element) entry
							.getElementsByTagName("description").item(0);
					Element _pubDateE = (Element) entry.getElementsByTagName(
							"pubDate").item(0);
					Element _linkE = (Element) entry.getElementsByTagName(
							"link").item(0);

					String _title = _titleE != null ? _titleE.getFirstChild()
							.getNodeValue() : "";
					String _description = _descriptionE != null ? _descriptionE
							.getFirstChild().getNodeValue() : "";
					Date _pubDate = null;
					try {
						_pubDate = _pubDateE != null ? new SimpleDateFormat(
								"EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
								.parse(_pubDateE.getFirstChild().getNodeValue())
								: null;
					} catch (DOMException e) {
					} catch (ParseException e) {
					}
					String _link = _linkE != null ? _linkE.getFirstChild()
							.getNodeValue() : "";

					// create RssItemObject and add it to the ArrayList
					RssItem rssItem = new RssItem(_title, _description,
							_pubDate, _link);

					rssItems.add(rssItem);
				}
			}
		}
		return rssItems;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}