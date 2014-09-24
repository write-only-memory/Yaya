package ca.currybox.yaya;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class main extends Activity {

    private TextView status;

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
        status = (TextView) findViewById(R.id.status);
        Connection task = new Connection();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        String username = prefs.getString("pref_mal_username", ""); //gets the username from preferences
        String url = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime"; //creates a valid url
        status.setText("Fetching data...");

        task.execute(new String[]{url});


    }

    private class Connection extends AsyncTask<String, Void, String> {

        String[] shows;

        protected String doInBackground(String... urls) {


            String xml = "";

            for (String url : urls) {

                XMLParser parser = new XMLParser();
                xml = parser.getXmlFromUrl(url);
                //break here for status updates
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName("anime");
                //Log.i("lel","works");

                xml = "";
                shows = new String[nl.getLength()];
                for (int i = 0; i < nl.getLength(); i++) {
                    Element e = (Element) nl.item(i);
                    xml = xml + parser.getValue(e, "series_title") + "\n";
                    shows[i] = parser.getValue(e, "series_title");
                }


            }


            return xml;
        }

        @Override
        protected void onPostExecute(String result) {

            ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(main.this, android.R.layout.simple_list_item_1, shows);
            ListView showList = (ListView) findViewById(R.id.shows);
            showList.setAdapter(titleAdapter);

            //status.setText(result);
        }
    }


}
