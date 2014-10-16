package ca.currybox.yaya;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by write_only_memory on 10/15/2014.
 */
public class animeList {

    public ArrayList<Anime> getList(Document doc) {
        XMLParser parser = new XMLParser();
        ArrayList<Anime> animeList = new ArrayList<Anime>();

        try {
            //Locate Nodelist
            NodeList nl = doc.getElementsByTagName("anime");
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                Anime show = new Anime();
                show.setTitle(parser.getValue(e, "series_title"));
                show.setEpisodes(Integer.parseInt(parser.getValue(e, "series_episodes")));
                show.setStatus(Integer.parseInt(parser.getValue(e, "my_status")));
                show.setSynonyms(parser.getValue(e, "series_synonyms"));
                show.setUpdated(Integer.parseInt(parser.getValue(e, "my_last_updated")));
                show.setWatched(Integer.parseInt(parser.getValue(e, "my_watched_episodes")));
                show.setId(Integer.parseInt(parser.getValue(e, "series_animedb_id")));

                animeList.add(show);
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return animeList;
    }


}
