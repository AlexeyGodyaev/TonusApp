package com.caloriesdiary.caloriesdiary;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference DeletePref = (Preference) findPreference("pref_key_delete_account");
        DeletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                Intent intent = new Intent(getActivity(),DeleteAccountActivity.class);
                startActivity(intent);
                return true;
            }
        });
        Preference ChangePref = (Preference) findPreference("pref_key_change_password");
        ChangePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}
