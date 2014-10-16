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

            serializer.startTag("", "episode");
            serializer.text(String.valueOf(show.getWatched()));
            serializer.endTag("", "episode");

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
