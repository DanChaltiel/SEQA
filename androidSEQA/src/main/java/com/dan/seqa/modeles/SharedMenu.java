package com.dan.seqa.modeles;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AccueilActivity;
import com.dan.seqa.activity.PrefActivity;

public class SharedMenu { 	
	@SuppressWarnings("unused")
	public static void onCreateOptionsMenu(Menu menu, FragmentActivity ctx) {
//		MenuInflater inflater = ctx.getMenuInflater();
//		inflater.inflate(R.menu.shared_menu, menu);
	}

	@SuppressWarnings("unused")
	public static void onCreateOptionsPrefMenu(Menu menu, PreferenceActivity ctx) {
//		MenuInflater inflater = ctx.getMenuInflater();
//		inflater.inflate(R.menu.shared_menu, menu);
	}

	public static boolean onOptionsItemSelected(MenuItem item, Activity caller) {
		Intent intent;			
		switch (item.getItemId()) {			
		case R.id.menu_pref:
			intent = new Intent(caller, PrefActivity.class);
			caller.startActivity(intent);
			return true;
		case R.id.menu_retour_accueil:
		case android.R.id.home:
			intent = new Intent(caller, AccueilActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			caller.startActivity(intent);
			return true;
		default:
			return false;
		}
	}
}