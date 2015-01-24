package ca.currybox.yaya;

import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;

/**
 * Created by write_only_memory on 10/15/2014.
 */
public class createXML {

    XmlSerializer serializer = Xml.newSerializer();
    StringWriter writer = new StringWriter();

    public createXML(Anime show) {


        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "entry");

            //start of update values
            serializer.startTag("", "episode");
            serializer.text(String.valueOf(show.getWatched()));
            serializer.endTag("", "episode");

            serializer.startTag("", "status");
            serializer.text(String.valueOf(show.getStatus()));
            serializer.endTag("", "status");

            serializer.startTag("", "date_start");
            serializer.text(String.valueOf(show.getDateStarted()));
            serializer.endTag("", "date_start");

            serializer.startTag("", "date_finish");
            serializer.text(String.valueOf(show.getDateFinished()));
            serializer.endTag("", "date_finish");

            //end of update values
            serializer.endTag("", "entry");
            serializer.endDocument();
            //return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getXML() {
        return writer.toString();
    }
}
