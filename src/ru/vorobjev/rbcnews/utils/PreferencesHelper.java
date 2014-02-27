package ru.vorobjev.rbcnews.utils;

import ru.vorobjev.rbcnews.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {

	public static SharedPreferences getPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static String getDefaultCategory(Context context) {
		return context.getResources().getStringArray(R.array.category_values)[0];
	}
}
