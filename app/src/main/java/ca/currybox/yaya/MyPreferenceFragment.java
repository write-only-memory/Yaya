package ca.currybox.yaya;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MyPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}