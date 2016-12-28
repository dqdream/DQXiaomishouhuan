package com.dq.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedpreferenceUtil {

	public static void savePreference(String key,int value,Context context){
		SharedPreferences preferences=context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
		Editor editor = preferences.edit();	
		editor.putInt(key, value);
		editor.commit();		
	};

	public static int getsavePreference(String key,Context context) {
		SharedPreferences preferences=context.getSharedPreferences("userinfo",Context.MODE_PRIVATE);
		return preferences.getInt(key,0);
	}
}
