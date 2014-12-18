package ca.currybox.yaya;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class SettingsActivity extends Activity {
    //@Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); //preferences object
        if (prefs.getBoolean("dark_pref", false)) //checks if settings checkbox is true to set app into dark mode
        {
            setTheme(R.style.AppTheme);
        }

        //Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();


        SharedPreferences firstLaunch = getSharedPreferences("FirstLaunch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstLaunch.edit();

        editor.putBoolean("HaveShownPrefs", true);
        editor.commit();


    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Load the preferences from the XML resource
            addPreferencesFromResource(R.xml.preferences);

        }


        @Override
        public void onResume() {
            super.onResume();

            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

            initSummary();
        }

        @Override
        public void onPause() {
            super.onPause();

            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePrefsSummary(sharedPreferences, findPreference(key));
        }


        protected void updatePrefsSummary(SharedPreferences sharedPreferences, Preference pref) {

            if (pref == null)
                return;

            if (pref instanceof EditTextPreference) {
                // EditPreference
                EditTextPreference editTextPref = (EditTextPreference) pref;
                if (!editTextPref.getKey().equals("pref_mal_password")) {
                    editTextPref.setSummary(editTextPref.getText());
                }
            }
        }

        /*
        Initialize summary for all items
         */
        protected void initSummary() {
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                initPrefsSummary(getPreferenceManager().getSharedPreferences(),
                        getPreferenceScreen().getPreference(i));
            }
        }

        /*
        Initialize summary for single elements
         */
        protected void initPrefsSummary(SharedPreferences sharedPreferences, Preference p) {
            if (p instanceof PreferenceCategory) {
                PreferenceCategory pCat = (PreferenceCategory) p;
                for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                    initPrefsSummary(sharedPreferences, pCat.getPreference(i));
                }
            } else {
                updatePrefsSummary(sharedPreferences, p);
            }
        }

    }

}


