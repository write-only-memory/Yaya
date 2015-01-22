package ca.currybox.yaya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

/**
 * Created by Kanchana on 12/19/2014.
 */
public class mainFragment extends Fragment implements View.OnClickListener {

    private TextView status;
    private List<Anime> animeList = null;
    ListView listview;
    ListViewAdapter adapter;


    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_layout, container, false);

        Button retrieve = (Button) view.findViewById(R.id.retrieve_button);
        retrieve.setOnClickListener(this);


        SharedPreferences firstLaunch = super.getActivity().getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once

        if (!shownPrefs) { //if preferences have not been previously shown (ie, first launch), go directly to the settings screen
            Intent intent = new Intent(super.getActivity(), SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);
        } else { //otherwise show Toast. placeholder action for now
            //Toast.makeText(getApplicationContext(), "Not first launch", Toast.LENGTH_LONG).show();
        }

        //Follow block checks if user xml file exists. If it does, it populates the listview. Otherwise, do nothing
        File user = new File(super.getActivity().getFilesDir().toString() + "/user.xml");


        if (user.exists()) {
            new PopulateList().execute();
        } else {
            //not found conditions here
        }

        Log.i("Fragment", "We're here");


        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.retrieve_button:
                retrieveList();
        }
    }

    public void retrieveList() {
        status = (TextView) super.getActivity().findViewById(R.id.status); //the status info box
        DownloadUser task = new DownloadUser();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(super.getActivity().getApplicationContext()); //preferences object
        String username = prefs.getString("pref_mal_username", ""); //gets the username from preferences
        String url = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime"; //creates a valid url
        status.setText("Fetching data...");

        task.execute(new String[]{url}); //get data asynchronously


    }

    private class PopulateList extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            XMLParser parser = new XMLParser();
            String xml = parser.read("user.xml", mainFragment.super.getActivity());
            Document doc = parser.getDomElement(xml);

            animeList = new animeList().getList(doc);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            listview = (ListView) mainFragment.super.getActivity().findViewById(R.id.shows);


            adapter = new ListViewAdapter(mainFragment.super.getActivity(), animeList);

            listview.setAdapter(adapter);

            adapter.filter(1); //type 1 is currently watching
            adapter.sortByUpdated(); //sorts the list by last updated
            status = (TextView) mainFragment.super.getActivity().findViewById(R.id.status);

            status.setText("Waaaai~~"); //te-he~
        }

    }


    private class DownloadUser extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {

            for (String url : urls) {

                XMLParser parser = new XMLParser();
                parser.getXmlFromUrl(url);
                parser.write("user.xml", mainFragment.super.getActivity());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            new PopulateList().execute();
        }
    }
}
