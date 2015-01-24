package ca.currybox.yaya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.File;
import java.util.List;


public class main extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_appbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);


        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once
        drawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar, shownPrefs);
        //Todo: Possibly change this to see if MAL username is non-default

        Intent i = getIntent();

        /**
         * Launch correct fragment based on calling intent.
         * IE: Launch the file match fragment if a video file is played,
         * otherwise launch the main app view
         */

        if (i.getAction() == "android.intent.action.VIEW")
        {
            Bundle bundle = new Bundle();
            bundle.putString("uri", i.getDataString());

            playMatch play = new playMatch();
            play.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.body, play).commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.body, new mainFragment()).commit();
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

            Intent intent = new Intent(this, MyPreferenceActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
