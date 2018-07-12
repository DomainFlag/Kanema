package com.example.cchiv.kanema.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.cchiv.kanema.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.movies_preferences);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        sharedPreferences = preferenceScreen.getSharedPreferences();
        changeSummaryValue(getString(R.string.entertainment_key));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void changeSummaryValue(String key) {
        Preference preference = findPreference(key);

        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(listPreference.getValue());
            if(index >= 0) {
                CharSequence value = listPreference.getEntries()[index];
                listPreference.setSummary(value.toString());
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        changeSummaryValue(key);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
