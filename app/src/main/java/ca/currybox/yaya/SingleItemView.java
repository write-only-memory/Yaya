package ca.currybox.yaya;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by write_only_memory on 10/3/2014.
 */
public class SingleItemView extends Activity {

    //Declare vars
    private String title;
    private String synonyms;
    private int episodes;
    private int watched;
    private int status;
    private int updated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);

        Intent i = getIntent();
        title = i.getStringExtra("title");
        Log.i("INFO", title);
        synonyms = i.getStringExtra("synonyms");
//        Log.i("INFO",synonyms);
        episodes = i.getIntExtra("episodes", 0);
        //episodes = Integer.parseInt(i.getStringExtra("episodes"));
        // watched = Integer.parseInt(i.getStringExtra("watched"));
        // status = Integer.parseInt(i.getStringExtra("status"));
        //  updated = Integer.parseInt(i.getStringExtra("updated"));

        //Locate items in layout
        TextView titleView = (TextView) findViewById(R.id.list_title);
        TextView episodeView = (TextView) findViewById(R.id.episodes);
        TextView watchedView = (TextView) findViewById(R.id.watched);
        TextView statusView = (TextView) findViewById(R.id.list_status);
        TextView updatedView = (TextView) findViewById(R.id.updated);

        //Set values
        titleView.setText(title);
        episodeView.setText(String.valueOf(episodes));
        //watchedView.setText(watched);
        //statusView.setText(status);
        //updatedView.setText(updated);
    }


}
