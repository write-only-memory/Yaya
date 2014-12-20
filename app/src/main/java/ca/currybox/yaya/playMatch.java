package ca.currybox.yaya;

/**
 * Created by Lel on 9/7/2014.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class playMatch extends Fragment {

    private Anime show;
    private String uri;


    public playMatch() {
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_match, container, false);

        SharedPreferences firstLaunch = super.getActivity().getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once
        //Todo: Possibly change this to see if MAL username is non-default

        if (!shownPrefs) { //if preferences have not been previously shown (ie, first launch), go directly to the settings screen
            Intent intent = new Intent(super.getActivity(), SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);
        } else { //otherwise show Toast. placeholder action for now
            //Toast.makeText(getApplicationContext(), "Not first launch", Toast.LENGTH_LONG).show();
        }


        uri = getArguments().getString("uri");

        TextView filename = (TextView) super.getActivity().findViewById(R.id.filename); //id of the textview used to show the cleaned up filename
        filename.setText(uri); //sets the path (why is this even here? its getting replaced below...)

        String s4; //temporary strings to hold the niceified names
        String s5; //another temp string

        try {
            s4 = URLDecoder.decode(uri, "UTF-8"); //un-escapes the uri path because dicks and stupid %20s and shit
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

        TextView titleView = (TextView) super.getActivity().findViewById(R.id.title_view);
        TextView episodeView = (TextView) super.getActivity().findViewById(R.id.episode_view);

        String title = s5.substring(0, s5.lastIndexOf("-") - 1); //extracts the show title (substring from position 0 to last occurrence of "-")
        String episode = s5.substring(s5.lastIndexOf("-") + 1, s5.lastIndexOf(".")); //extracts episode number (substring from last occurrence of "-" to last occurrence of ".")
        titleView.setText(title); //sets view accordingly
        episodeView.setText(episode);

        XMLParser parser = new XMLParser();
        String xml = parser.read("user.xml", super.getActivity());
        Document doc = parser.getDomElement(xml);
        List<Anime> animeList = new animeList().getList(doc); //reads the user xml file into memory
        title = title.replaceAll("[^!~'A-z]", ""); //gets rid of whitespaces and special characters that are not !, ~, or '
        Log.i("Detected title", title);

        Button updateButton = (Button) super.getActivity().findViewById(R.id.update_button);

        //iterates through the array and checks if triggered show exists in user list
        for (int i = 0; i < animeList.size(); i++) {
            String[] synonyms = animeList.get(i).getSynonyms().split(";");
            String listTitle = animeList.get(i).getTitle().replaceAll("[^!~'A-z]", "");
            if (title.equalsIgnoreCase(listTitle)) {
                show = animeList.get(i);
                TextView match = (TextView) super.getActivity().findViewById(R.id.match_title);
                match.setText("Filename matched with: " + animeList.get(i).getTitle());
                updateButton.setEnabled(true);

                ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(show.getTitle());

                TextView mal_ep = (TextView) super.getActivity().findViewById(R.id.mal_last_ep);
                mal_ep.setText(String.valueOf(show.getWatched()));

                TextView mal_updated = (TextView) super.getActivity().findViewById(R.id.mal_last_update);
                String date = new SimpleDateFormat("MMM dd yyyy 'at' KK:mm:ss a").format(new Date(show.getUpdated() * 1000L));
                mal_updated.setText(date);

                break;
            } else {
                for (String synonym : synonyms) {
                    synonym = synonym.replaceAll("[^!~'A-z]", "");
                    if (title.equalsIgnoreCase(synonym)) {
                        show = animeList.get(i);
                        TextView match = (TextView) super.getActivity().findViewById(R.id.match_title);
                        match.setText("Match found: " + animeList.get(i).getTitle());
                        updateButton.setEnabled(true);

                        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(show.getTitle());

                        TextView mal_ep = (TextView) super.getActivity().findViewById(R.id.mal_last_ep);
                        mal_ep.setText(String.valueOf(show.getWatched()));

                        TextView mal_updated = (TextView) super.getActivity().findViewById(R.id.mal_last_update);
                        String date = new SimpleDateFormat("MMM dd yyyy 'at' KK:mm:ss a").format(new Date(show.getUpdated() * 1000L));
                        mal_updated.setText(date);

                        break;
                    }
                }
            }

        }

        return view;
    }

    public void playFile(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri videoUri = Uri.parse(uri);
        intent.setDataAndType(videoUri, "application/x-mpegURL");
        intent.setPackage("com.mxtech.videoplayer.ad");
        startActivity(intent);
    }

    public void update(View v) {
        show.setWatched(show.getWatched() + 1); //increment watched episode

        if (show.getWatched() == show.getEpisodes()) {
            show.setStatus(2); //if watched = total, change status from watching to completed

            //get current date and set as date completed
            String date = new SimpleDateFormat("MMddyyyy").format(Calendar.getInstance().getTime());
            Log.i("Date", date);
            show.setDateFinished(date);
        }

        updateMal updater = new updateMal();
        TextView status = (TextView) super.getActivity().findViewById(R.id.update_status);
        status.setText("Starting update...");
        updater.execute();
    }

    private class updateMal extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... urls) {
            String result = "";
            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); //preferences object
                String creds = prefs.getString("pref_mal_username", "a") + ":" + prefs.getString("pref_mal_password", "a"); //set credentials


                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://myanimelist.net/api/animelist/update/" + show.getId() + ".xml"); //mal update url
                httpPost.addHeader("User-Agent", new ApiKey().getKey()); //set user agent

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

        protected void onPostExecute(String result) {
            TextView status = (TextView) getActivity().findViewById(R.id.update_status);
            status.setText("Reply from server: " + result);
            if (result.equalsIgnoreCase("updated")) {
                XMLParser parser = new XMLParser();

                Button updateButton = (Button) getActivity().findViewById(R.id.update_button);
                updateButton.setEnabled(false); //disable update button on successful previous update

                //re-downloads animelist from MAL after successful list update (this is stupid inefficient, but blame MAL's lack of proper API
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); //preferences object
                String username = prefs.getString("pref_mal_username", ""); //gets the username from preferences
                String url = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime"; //creates a valid url
                new downloadUser().execute(url);

                TextView last_ep = (TextView) getActivity().findViewById(R.id.mal_last_ep);
                last_ep.setText(String.valueOf(show.getWatched()));

                final TextView updated = (TextView) getActivity().findViewById(R.id.mal_last_update);
                updated.setText("Just now...");
                updated.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down));

                last_ep.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down));


            }
        }
    }

    private class downloadUser extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {
            for (String url : urls) {
                XMLParser parser = new XMLParser();
                parser.getXmlFromUrl(url);
                parser.write("user.xml", getActivity());
            }
            return null;
        }
    }


}


