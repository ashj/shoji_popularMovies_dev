package com.shoji.example.android.popularmoviesstage1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

public class SettingsFragment
        extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        initPreferencesSummary();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if( (preference != null) && !(preference instanceof CheckBoxPreference)) {
            String value = sharedPreferences.getString(key, "");
            setPreferenceSummary(preference, value);
        }
    }


    private void setPreferenceSummary(Preference preference, String value) {

        if(preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);

            if(index >= 0) {
                CharSequence[] entriesList = listPreference.getEntries();
                CharSequence summary = entriesList[index];
                listPreference.setSummary(summary);
            }
        }
    }

    private void initPreferencesSummary() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int count = preferenceScreen.getPreferenceCount();
        for(int i = 0; i < count; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if(!(preference instanceof CheckBoxPreference)) {
                String key = preference.getKey();
                String value = sharedPreferences.getString(key, "");

                setPreferenceSummary(preference, value);
            }
        }
    }
}
