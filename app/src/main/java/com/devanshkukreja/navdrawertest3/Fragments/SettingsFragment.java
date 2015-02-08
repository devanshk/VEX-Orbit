package com.devanshkukreja.navdrawertest3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.devanshkukreja.navdrawertest3.R;

/**
 * Created by devanshkukreja on 8/4/14.
 */
public class SettingsFragment extends PreferenceFragment {

    EditTextPreference username, teamNumber;
    SharedPreferences sharedPreferences;

    public SettingsFragment(){} //Necessary Constructor

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        addPreferencesFromResource(R.xml.preferences); //Gets preference list
        username=(EditTextPreference)findPreference("user_name");
        teamNumber = (EditTextPreference)findPreference("team_number");
        loadListeners();
    }

    @Override
    public void onResume(){
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username.setTitle(sharedPreferences.getString("user_name","My Name"));
        teamNumber.setTitle(sharedPreferences.getString("team_number","Team Number"));
    }

    private void loadListeners(){

        //EditTextPreference Listeners
        username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                username.setTitle(newValue.toString());
                return true;
            }
        });
        teamNumber.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                teamNumber.setTitle(newValue.toString());
                return true;
            }
        });
    }
}
