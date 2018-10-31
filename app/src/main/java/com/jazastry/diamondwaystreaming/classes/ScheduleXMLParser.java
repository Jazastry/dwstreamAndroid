package com.jazastry.diamondwaystreaming.classes;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yasen.lazarov on 18-12-2015.
 */
public class ScheduleXMLParser {
    private static final String ns = null;

    public static List parse(String inputString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = new ByteArrayInputStream(inputString.getBytes("ISO-8859-1"));
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, "ISO-8859-1");
            parser.nextTag();
            return readRssChannel(parser);
        } finally {
            stream.close();
        }
    }

    private static List readRssChannel(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        List streamingItems = new ArrayList();
        parser.nextTag();
        String name = parser.getName();
        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            name = parser.getName();
            // Starts by looking for the item tag
            if (name.equals("item")) {
                streamingItems.add(readRssChanelItem(parser));
            } else {
                skip(parser);
            }
        }
        return streamingItems;
    }

    private static StreamingItem readRssChanelItem(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String title = null;
        String description = null;
        String pubDate = null;
        String guid = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else if (name.equals("pubDate")) {
                pubDate = readPubDate(parser);
            } else if (name.equals("guid")) {
                guid = readGuid(parser);
            } else {
                skip(parser);
            }
        }
        return new StreamingItem(title, description, pubDate, guid);
    }

    // Processes title tags in the feed.
    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes description tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    // Processes guid tags in the feed.
    private static String readGuid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "guid");
        String guid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "guid");
        return guid;
    }

    // Processes pubDate tags in the feed.
    private static String readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String pubDate = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return pubDate;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
