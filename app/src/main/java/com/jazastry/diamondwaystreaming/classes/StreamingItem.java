package com.jazastry.diamondwaystreaming.classes;

/**
 * Created by Jazzy on 24/12/2015.
 */
public class StreamingItem {
    public final String title;
    public final String description;
    public final String pubDate;
    public final String guid;

    protected StreamingItem(String title, String description, String pubDate, String guid) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.guid = guid;
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