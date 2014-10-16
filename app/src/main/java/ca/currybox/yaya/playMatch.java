package ca.currybox.yaya;

/**
 * Created by Lel on 9/7/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class playMatch extends Activity {

    private Anime show;


    public playMatch() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        if (prefs.getBoolean("dark_pref", false)) //checks if settings checkbox is true to set app into dark mode
        {
            setTheme(R.style.AppTheme_Dark);
        }

        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once
        //Todo: Possibly change this to see if MAL username is non-default

        if (!shownPrefs) { //if preferences have not been previously shown (ie, first launch), go directly to the settings screen
            Intent intent = new Intent(this, SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);
        } else { //otherwise show Toast. placeholder action for now
            Toast.makeText(getApplicationContext(), "Not first launch", Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_info);

        Intent intent = getIntent(); //get initial Intent from calling app

        String path = intent.getDataString(); //gets the full uri of the episode (usually a http url from a local proxy)

        TextView uriView = (TextView) findViewById(R.id.full_URI);
        uriView.setText(path);

        TextView filename = (TextView) findViewById(R.id.filename); //id of the textview used to show the cleaned up filename
        filename.setText(path); //sets the path (why is this even here? its getting replaced below...)

        String s4; //temporary strings to hold the niceified names
        String s5; //another temp string

        try {
            s4 = URLDecoder.decode(path, "UTF-8"); //un-escapes the uri path because dicks and stupid %20s and shit
        } catch (UnsupportedEncodingException unsupportedencodingexception) {
            throw new AssertionError("UTF-8 is unknown"); //likes to throw this error for no reason. this ignores it
        }

        s5 = s4.substring(1 + s4.lastIndexOf("/")).replaceAll("\\[\\S+\\]", ""); //removes things in between square brackets (probably)

        if (s5.indexOf(" ") == 0) {
            s5 = s5.substring(1); //removes leading spaces
        }

        if (s5.charAt(s5.lastIndexOf(".") - 1) == ' ') {
            s5 = (new StringBuilder()).append(s5.substring(0, -1 + s5.lastIndexOf("."))).append(s5.substring(s5.lastIndexOf("."))).toString(); //wat? why string builder? wtf is going on. stupid decompilation
            //seriously, rewrite this shit. this is terrible
        }

        filename.setText(s5); //replaces the text in the textview... stupid
        //changed all these comments

        TextView titleView = (TextView) findViewById(R.id.title_view);
        TextView episodeView = (TextView) findViewById(R.id.episode_view);

        String title = s5.substring(0, s5.lastIndexOf("-") - 1); //extracts the show title (substring from position 0 to last occurrence of "-")
        String episode = s5.substring(s5.lastIndexOf("-") + 1, s5.lastIndexOf(".")); //extracts episode number (substring from last occurrence of "-" to last occurrence of ".")
        titleView.setText(title); //sets view accordingly
        episodeView.setText(episode);

        XMLParser parser = new XMLParser();
        String xml = parser.read("user.xml", this);
        Document doc = parser.getDomElement(xml);
        List<Anime> animeList = new animeList().getList(doc); //reads the user xml file into memory

        //iterates through the array and checks if triggered show exists in user list
        for (int i = 0; i < animeList.size(); i++) {
            if (title.equalsIgnoreCase(animeList.get(i).getTitle())) {
                show = animeList.get(i);
                TextView match = (TextView) findViewById(R.id.match_title);
                match.setText(animeList.get(i).getTitle());
            }
        }

    }

    public void playFile(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        TextView uriView = (TextView) findViewById(R.id.full_URI);
        String uri = (String) uriView.getText();

        Uri videoUri = Uri.parse(uri);
        intent.setDataAndType(videoUri, "application/x-mpegURL");
        intent.setPackage("com.mxtech.videoplayer.ad");
        startActivity(intent);
    }

    public void update(View v) {
        show.setWatched(show.getWatched() + 1);
        updateMal updater = new updateMal();
        updater.execute();
    }

    private class updateMal extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... urls) {
            String result = "";
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
                String creds = prefs.getString("pref_mal_username", "a") + ":" + prefs.getString("pref_mal_password", "a"); //set credentials


                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://myanimelist.net/api/animelist/update/" + show.getId() + ".xml"); //mal update url
                httpPost.addHeader("User-Agent", "yourAPIkeyHere"); //set user agent

                final String basicAuth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP); //encode auth
                httpPost.addHeader("Authorization", basicAuth); //add auth header to request

                String xml = new createXML(show).getXML(); //set xml data for updating
                Log.i("XML Sent", xml);
                ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>(1); //namevaluepair for extra parameters
                postParams.add(new BasicNameValuePair("data", xml)); //add xml data
                httpPost.setEntity(new UrlEncodedFormEntity(postParams));

                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity()); //store response
                Log.i("HTTP Response", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }
    }


}

