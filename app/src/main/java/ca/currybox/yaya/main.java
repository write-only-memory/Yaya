package ca.currybox.yaya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class main extends Activity {

    private TextView status;
    private List<Anime> animeList = null;
    ListView listview;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        if (prefs.getBoolean("dark_pref", false)) //checks if settings checkbox is true to set app into dark mode
        {
            setTheme(R.style.AppTheme_Dark);
        }

        setContentView(R.layout.activity_main);

        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once
        //Todo: Possibly change this to see if MAL username is non-default

        if (!shownPrefs) { //if preferences have not been previously shown (ie, first launch), go directly to the settings screen
            Intent intent = new Intent(this, SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);
        } else { //otherwise show Toast. placeholder action for now
            Toast.makeText(getApplicationContext(), "Not first launch", Toast.LENGTH_LONG).show();
        }

        //Follow block checks if user xml file exists. If it does, it populates the listview. Otherwise, do nothing
        File user = new File(main.this.getFilesDir().toString() + "/user.xml");
        if (user.exists()) {
            Toast.makeText(getApplicationContext(), "User file exists", Toast.LENGTH_LONG).show();
            new PopulateList().execute();
        } else {
            Toast.makeText(getApplicationContext(), "User file not found", Toast.LENGTH_LONG).show();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void retrieveList(View v) {
        status = (TextView) findViewById(R.id.status); //the status info box
        DownloadXML task = new DownloadXML();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        String username = prefs.getString("pref_mal_username", ""); //gets the username from preferences
        String url = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime"; //creates a valid url
        status.setText("Fetching data...");

        task.execute(new String[]{url}); //get data asynchronously


    }

    private class PopulateList extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            XMLParser parser = new XMLParser();
            String xml = parser.read("lel.xml", main.this);
            Document doc = parser.getDomElement(xml);
            animeList = new ArrayList<Anime>();

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

                    animeList.add(show);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            listview = (ListView) findViewById(R.id.shows);

            adapter = new ListViewAdapter(main.this, animeList);

            listview.setAdapter(adapter);

            adapter.filter(1); //type 1 is currently watching

            status = (TextView) findViewById(R.id.status);

            status.setText("Waaaai~~"); //te-he~
        }

    }


    private class DownloadXML extends AsyncTask<String, Void, String> {

        String[] shows; //array to hold the shows - most likely will be changed to an arrayList

        protected String doInBackground(String... urls) {
            String xml = ""; //holds the raw data - not really needed. mostly for debug


            for (String url : urls) {

                XMLParser parser = new XMLParser();
                xml = parser.getXmlFromUrl(url, main.this);
            }
            return xml;
        }

        @Override
        protected void onPostExecute(String result) {

            new PopulateList().execute();
        }
    }


}
