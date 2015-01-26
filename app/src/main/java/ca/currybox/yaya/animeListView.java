package ca.currybox.yaya;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.w3c.dom.Document;

import java.io.File;
import java.util.List;

/**
 * Created by write-only-memory on 1/24/2015.
 */
public class animeListView extends Fragment {


    ListView listview;
    ListViewAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Anime> animeList = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.anime_list, container, false);

        File user = new File(super.getActivity().getFilesDir().toString() + "/user.xml");


        if (user.exists()) {
            new PopulateList().execute();
        } else {
            //not found conditions here
        }

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.anime_list_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Refresh", "refresh called");


                DownloadUser task = new DownloadUser();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()); //preferences object
                String username = prefs.getString("pref_mal_username", ""); //gets the username from preferences
                String url = "http://myanimelist.net/malappinfo.php?u=" + username + "&status=all&type=anime"; //creates a valid url


                task.execute(new String[]{url}); //get data asynchronously


            }
        });

        return view;
    }

    public void updateListView() {
        listview = (ListView) getActivity().findViewById(R.id.shows);


        adapter = new ListViewAdapter(getActivity(), animeList);

        listview.setAdapter(adapter);

        adapter.filter(1); //type 1 is currently watching
        adapter.sortByUpdated(); //sorts the list by last updated
    }

    private class PopulateList extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            XMLParser parser = new XMLParser();
            String xml = parser.read("user.xml", getActivity());
            Document doc = parser.getDomElement(xml);

            animeList = new animeList().getList(doc);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


            updateListView();

            swipeRefreshLayout.setRefreshing(false);


        }

    }

    private class DownloadUser extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {

            for (String url : urls) {

                XMLParser parser = new XMLParser();
                parser.getXmlFromUrl(url);
                parser.write("user.xml", getActivity());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            new PopulateList().execute();
        }
    }
}
