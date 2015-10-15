package com.dan.seqa.activity;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.dan.newseqa.R;
import com.dan.seqa.modeles.SharedMenu;

/**
 * Activité de base
 * Prend en charge le menu commun à toutes les autres activités
 */
public class AbstractActivity extends AppCompatActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		SharedMenu.onCreateOptionsMenu(menu, this);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		return true;
	}

	protected static int getThemeFromPreferences(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(PrefActivity.STYLE_KEY, R.style.CustomThemeLight);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
            openOptionsMenu();
	    	return true;
	    }
		return super.onKeyUp(keyCode, event);
	}
}