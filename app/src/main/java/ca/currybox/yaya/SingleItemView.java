package ca.currybox.yaya;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by write_only_memory on 10/3/2014.
 */
public class SingleItemView extends ActionBarActivity implements View.OnClickListener {

    //Declare vars
    private String title;
    private String synonyms;
    private int episodes;
    private int watched;
    private int status_id;
    private int updated;
    private Anime intentShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent i = getIntent();

        intentShow = (Anime) i.getSerializableExtra("show");

        title = intentShow.getTitle();

        //Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button read = (Button) findViewById(R.id.read);
        Button write = (Button) findViewById(R.id.write);
        read.setOnClickListener(this);
        write.setOnClickListener(this);

        read();

        Log.i("INFO", title);
        synonyms = intentShow.getSynonyms();

        episodes = intentShow.getEpisodes();
        watched = intentShow.getWatched();

        status_id = intentShow.getStatus();
        updated = intentShow.getUpdated();
        String date = new SimpleDateFormat("MMM dd yyyy 'at' KK:mm:ss a").format(new Date(updated * 1000L));

        String status = String.valueOf(status_id);
        switch (status_id) {
            case 1:
                status = "Currently watching";
                break;
            case 2:
                status = "Completed";
                break;
            case 3:
                status = "On Hold";
                break;
            case 4:
                status = "Dropped";
                break;
            case 6:
                status = "Plan to watch";
                break;
            default:
                Log.e("status unknown", status);
                status = "Unknown... wot";
                break;
        }

        //Locate items in layout
        RelativeLayout main = (RelativeLayout) findViewById(R.id.main_layout);
        TextView titleView = (TextView) main.findViewById(R.id.list_title);
        TextView episodeView = (TextView) main.findViewById(R.id.episodes);
        TextView watchedView = (TextView) main.findViewById(R.id.watched);
        TextView statusView = (TextView) main.findViewById(R.id.status);
        TextView updatedView = (TextView) main.findViewById(R.id.updated);

        //Set values
        titleView.setText(title);
        episodeView.setText(" of " + String.valueOf(episodes));
        watchedView.setText(String.valueOf(watched));
        statusView.setText(status);
        updatedView.setText(date);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.read:
                read();
                break;
            case R.id.write:
                write();
                break;
        }
    }

    public void read() {
        List<Anime> shows;
        customNames customList = new customNames();

        TextView customNamesList = (TextView) findViewById(R.id.list_custom_names);
        customNamesList.setText("No custom titles set");

        shows = customList.getCustomList(getApplicationContext());
        String out;
        Log.i("list size", "lel");
        boolean found = false;
        if (shows != null) {
            for (int i = 0; i < shows.size() && found != true; i++) {
                if (intentShow.getTitle().equalsIgnoreCase(shows.get(i).getTitle())) {
                    out = shows.get(i).getCustom_synonyms();
                    found = true;
                    customNamesList.setText(out);
                    EditText editor = (EditText) findViewById(R.id.synonyms);
                    editor.setText(out);
                }
            }
        }

    }

    public void write() {
        List<Anime> shows;
        customNames customList = new customNames();

        shows = customList.getCustomList(getApplicationContext());
        if (shows == null) {
            Log.i("shows", "is null");
            shows = new ArrayList<>();
        }
        Anime show = new Anime();
        show.copy(intentShow);
        EditText syn = (EditText) findViewById(R.id.synonyms);

        for (int i = 0; i < shows.size(); i++) {
            if (intentShow.getTitle().equalsIgnoreCase(shows.get(i).getTitle())) {
                shows.remove(i);
            }
        }

        show.setCustom_synonyms(String.valueOf(syn.getText()));
        shows.add(show);
        customList.write(shows, getApplicationContext());
        read();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
