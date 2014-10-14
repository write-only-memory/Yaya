package ca.currybox.yaya;

/**
 * Created by Lel on 9/7/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class info extends Activity {

    public info() {
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
        String s5; //random123

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


}


