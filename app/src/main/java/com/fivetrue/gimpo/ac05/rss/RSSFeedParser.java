package com.fivetrue.gimpo.ac05.rss;

import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

public class RSSFeedParser extends AsyncTask<Void, Void, Feed>{

	private static Executor sExecutor = AsyncTask.THREAD_POOL_EXECUTOR;

	public interface OnLoadFeedListener{
		void onLoad(Feed feed);
	}

	static final String TITLE = "title";
	static final String DESCRIPTION = "description";
	static final String CHANNEL = "channel";
	static final String LANGUAGE = "language";
	static final String COPYRIGHT = "copyright";
	static final String LINK = "link";
	static final String AUTHOR = "author";
	static final String ITEM = "item";
	static final String PUB_DATE = "pubDate";
	static final String GUID = "guid";
	static final String LAST_BUILD_DATE = "lastBuildDate";

	final URL url;

	private OnLoadFeedListener mOnLoadFeedListener = null;

	public RSSFeedParser(String feedUrl, OnLoadFeedListener ll) {
		mOnLoadFeedListener = ll;
		try {
			this.url = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public void readFeed() {
		executeOnExecutor(sExecutor);
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected Feed doInBackground(Void... params) {
		Feed feed = null;
		try {
			boolean isFeedHeader = true;
			// Set header values intial to the empty string
			String description = "";
			String title = "";
			String link = "";
			String language = "";
			String copyright = "";
			String author = "";
			String pubdate = "";
			String guid = "";
			String lastBuildDate = "";

			// First create a new XMLInputFactory
			XmlPullParserFactory inputFactory = XmlPullParserFactory.newInstance();
			inputFactory.setNamespaceAware(true);
			XmlPullParser parser = inputFactory.newPullParser();
			// Setup a new eventReader
			InputStream in = read();
			parser.setInput(in, null);
			// read the XML document
			int eventType = parser.getEventType();
			String tagname = "";
			while(eventType != XmlPullParser.END_DOCUMENT ) {
				if(eventType == XmlPullParser.START_TAG) {
					// 태그의 이름을 알아야 텍스트를 저장하기에 태그이름을 읽어서 변수에 저장
					tagname = parser.getName();

				} else if(eventType == XmlPullParser.TEXT) {
					// 태그 이름이 title과 같다면 변수에 title 저장
					switch (tagname) {
						case TITLE:
							title += parser.getText();
							break;
						case DESCRIPTION:
							description += parser.getText();
							break;
						case LINK:
							link += parser.getText();
							break;
						case GUID:
							guid += parser.getText();
							break;
						case LANGUAGE:
							language += parser.getText();
							break;
						case AUTHOR:
							author += parser.getText();
							break;
						case PUB_DATE:
							pubdate += parser.getText();
							break;
						case COPYRIGHT:
							copyright += parser.getText();
							break;
						case LAST_BUILD_DATE:
							lastBuildDate += parser.getText();
							break;
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					// end tag 이름을 얻어옴
					tagname = parser.getName();
					// end tag 이름이 item이라면 저장한 변수 title과 desc를 벡터에 저장
					if(tagname.equals(ITEM)) {
						if (isFeedHeader) {
							isFeedHeader = false;
							feed = new Feed(title.trim(), link.trim(), description.trim(), language.trim(),
									copyright.trim(), pubdate.trim(), lastBuildDate.trim());
						}else{
							FeedMessage message = new FeedMessage();
							message.setAuthor(author.trim());
							message.setDescription(description.trim());
							message.setGuid(guid.trim());
							message.setLink(link.trim());
							message.setTitle(title.trim());
							message.setPubDate(pubdate.trim());
							feed.getMessages().add(message);
						}
						// 변수 초기화
						description = "";
						title = "";
						link = "";
						language = "";
						copyright = "";
						author = "";
						pubdate = "";
						guid = "";
						lastBuildDate = "";
					}
				}
				// 다음 이벤트를 넘김
				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return feed;
	}

	@Override
	protected void onPostExecute(Feed feed) {
		super.onPostExecute(feed);
		if(mOnLoadFeedListener != null){
			mOnLoadFeedListener.onLoad(feed);
		}
	}
}

