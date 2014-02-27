package ru.vorobjev.rbcnews.activities;

import ru.vorobjev.rbcnews.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {
	
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
            addPreferencesFromResource(R.layout.preferences);
            Preference interval = (Preference) findPreference("interval");
            interval.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					Intent intent = new Intent(WebsterActivity.UPDATE_INTERVAL);
					sendBroadcast(intent);
					return true;
				}
			});
            
//            ActionBar actionBar = getActionBar();
//            actionBar.setDisplayHomeAsUpEnabled(true);
            
    }
}
