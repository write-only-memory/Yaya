package ca.currybox.yaya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

/**
 * Created by Kanchana on 12/19/2014.
 */
public class mainFragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_layout, container, false);

        super.getFragmentManager().beginTransaction().replace(R.id.main_list, new animeListView()).commit();

        SharedPreferences firstLaunch = super.getActivity().getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE); //get object for below
        boolean shownPrefs = firstLaunch.getBoolean("HaveShownPrefs", false); //gets the value to check if application has been launched at least once

        if (!shownPrefs) { //if preferences have not been previously shown (ie, first launch), go directly to the settings screen
            File file = new File(getActivity().getApplicationContext().getFilesDir(), "custom-names.dat");
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(super.getActivity(), SettingsActivity.class); //intent for the default drop-down menu Settings button
            startActivity(intent);
        } else { //otherwise show Toast. placeholder action for now
            //Toast.makeText(getApplicationContext(), "Not first launch", Toast.LENGTH_LONG).show();
        }

        //Follow block checks if user xml file exists. If it does, it populates the listview. Otherwise, do nothing


        Log.i("Fragment", "We're here");


        return view;
    }


}
