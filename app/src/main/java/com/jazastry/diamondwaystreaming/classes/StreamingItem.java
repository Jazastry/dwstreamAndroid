package com.jazastry.diamondwaystreaming.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jazzy on 24/12/2015.
 */
public class StreamingItem {
  public final String title;
  public final String description;
  public final String pubDate;
  public final String guid;
  public final Date date;
  public final String place;
  public final String teacher;

  protected StreamingItem(String title, String description, String pubDate, String guid) throws ParseException {
    this.title = title;
    this.description = description;
    this.pubDate = pubDate;
    this.guid = guid;

    String[] splitted = title.split(" - ");

    this.date = parseDateFromTitle(splitted[0]);
    this.place = splitted[1];
    this.teacher = splitted[2];
  }

  private Date parseDateFromTitle(String dateString) throws ParseException {
    int year = Calendar.getInstance().get(Calendar.YEAR);
    dateString = year + " " + dateString;
    SimpleDateFormat parser = new SimpleDateFormat("yyyy MMM d HH:mm z");
    Date date = parser.parse(dateString);
    return date;
  }
}

//<title>Dec 25 20:00 UTC+1 - Copenhagen - Lama Ole Nydahl</title>
//<link>
//http://www.diamondway-teachings.org/transmission.html
//</link>
//<description>Lecture with Lama Ole Nydahl in Copenhagen</description>
//<pubDate>Thu, 24 Dec 2015 12:00:00 +0200</pubDate>
//<guid>
//http://StreamingItem.dwbn.org/StreamingItem/schedule.php?item=81
//</guid>