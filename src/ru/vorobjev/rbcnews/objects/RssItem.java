package ru.vorobjev.rbcnews.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RssItem {

	private String title;
	private String description;
	private Date pubDate;
	private String link;

	public RssItem(String title, String description, Date pubDate, String link) {
		this.title = title;
		this.description = description;
		this.pubDate = pubDate;
		this.link = link;
	}

	public String getTitle() {
		return this.title;
	}

	public String getLink() {
		return this.link;
	}

	public String getDescription() {
		return this.description;
	}

	public Date getPubDate() {
		return this.pubDate;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd - hh:mm:ss", Locale.getDefault());
		String result = getTitle() + "  ( " + sdf.format(this.getPubDate()) + " )";
		return result;
	}

}