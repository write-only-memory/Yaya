package ca.currybox.yaya;

import android.content.Context;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        Intent i = getIntent();
        title = i.getStringExtra("title");

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

        Log.i("INFO", title);
        synonyms = i.getStringExtra("synonyms");
//        Log.i("INFO",synonyms);
        episodes = i.getIntExtra("episodes", 0);

        watched = i.getIntExtra("watched", 0);
        status_id = i.getIntExtra("status", 0);
        updated = i.getIntExtra("updated", 0);
        String date = new SimpleDateFormat("MMM dd yyyy 'at' KK:mm:ss a").format(new Date(updated * 1000L));

        String status;
        switch (status_id)
        {
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
    public void onClick(View v)
    {
        List<Anime> shows = new ArrayList<Anime>();
        switch (v.getId())
        {
            case R.id.read:
                TextView test = (TextView) findViewById(R.id.test_field);
                shows = getCustomList();
                String out = "";
                Log.i("list size", "lel");
                for (int i = 0; i < shows.size(); i++)
                {
                    out = out + shows.get(i).getTitle() + ":" + shows.get(i).getSynonyms();
                }
                test.setText(out);
                break;
            case R.id.write:
                shows = getCustomList();
                if (shows == null)
                {
                    Log.i("shows","is null");
                    shows = new ArrayList<Anime>();
                }
                Anime show = new Anime();
                show.setTitle(title);
                EditText syn = (EditText) findViewById(R.id.synonyms);
                show.setSynonyms(String.valueOf(syn.getText()));
                shows.add(show);
                write(shows);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void write (List<Anime> shows)
    {
        try
        {
            FileOutputStream fos = getApplicationContext().openFileOutput("custom-names.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(shows);
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Anime> getCustomList ()
    {
        List<Anime> customList;
        try
        {
            FileInputStream fis = openFileInput("custom-names.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            customList = (List<Anime>) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            customList = null;
            e.printStackTrace();
        }
        Log.i("reader", "done");
        return customList;

    }

}
