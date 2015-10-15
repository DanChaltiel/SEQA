package com.dan.seqa.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.activity.donnees.ModeEmploiActivity;
import com.dan.seqa.outils.ChangeLog;

import com.dan.seqa.outils.Methodes;


@SuppressWarnings("deprecation") //Vu que la classe entière est dépréciée et qu'il n'y a pas de bibliothèque de compatibilité
public class PrefActivity extends PreferenceActivity {	

	public final static String STYLE_KEY="com.dan.seqa.prefactivity.STYLE_KEY";
	public final static String CHANGE_LOG_KEY="com.dan.seqa.prefactivity.CHANGE_LOG_KEY";
	private final static int CHANGE_LOG_GEEK_COUNT = 3;
	private final static String ADRESS="superserie.developer@gmail.com",
								SUBJECT="J'ai une idée pour améliorer le SEQA",
								MESSAGE="Salut Dan, \n J'ai une bonne idée pour améliorer le Super Entraineur aux QCM des Annales !\n Il s'agirait de ";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);

		Methodes.listPreferences(getApplicationContext());
		
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final Preference emailButton = findPreference("emailButton");
		final Preference aboutButton = findPreference("aboutButton");
		final Preference changeLogButton = findPreference("changeLogButton");
		final Preference faqButton = findPreference("faqButton");
		final Preference modeEmploi = findPreference("modeEmploi");
		final Preference themeChoiceSwitch = findPreference("themeChoiceSwitch");
		final Preference statsTimeChoice = findPreference("statsTimeChoice");
		final Preference likeButton = findPreference("likeButton");

		
		//statsTimeChoice
		statsTimeChoice.setSummary("Actuellement : Par "+preferences.getString("statsTimeChoice", "mois"));
		statsTimeChoice.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                statsTimeChoice.setSummary("Actuellement : Par " + newValue.toString());
                Methodes.alert(newValue.toString());
                return true;
            }
        });
		
		
		//LIKE BUTTON
		likeButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) {
                try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/1465447603711062")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/seqaAndroid")));
                }
                return true;
            }
        });

		//EMAIL BUTTON
		emailButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) { 
				Methodes.emailSendTo(PrefActivity.this, ADRESS, SUBJECT, MESSAGE);
				return true;
			}
		});

		//THEME CHOICE
		themeChoiceSwitch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences.Editor editor = preferences.edit();
				//switch off=noir et on=blanc
				if (Boolean.parseBoolean(newValue.toString())) {
					editor.putInt(STYLE_KEY, R.style.CustomThemeLight);
				} else {
					editor.putInt(STYLE_KEY, R.style.CustomThemeDark);
				}
				editor.apply();
				Methodes.reload(PrefActivity.this);
				return true;
			}
		});
		
		//CHANGELOG BUTTON
		changeLogButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				//TODO changelog
				int count = preferences.getInt(CHANGE_LOG_KEY, 0);
				boolean isGeek = count > CHANGE_LOG_GEEK_COUNT;
				ChangeLog cl = new ChangeLog(PrefActivity.this, isGeek);
				cl.getLogDialog().show();
				if (isGeek)
					Toast.makeText(PrefActivity.this, "Plus de 3 clics sur le changeLog ? Tu dois être un geek :-)", Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor editor = preferences.edit();
				editor.putInt(CHANGE_LOG_KEY, count + 1);
				editor.apply();
				return true;
			}
		});

		//FAQ BUTTON
		faqButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(PrefActivity.this, FAQActivity.class);
				startActivity(intent);
				return true; 
			}
		});

		//FAQ BUTTON
		modeEmploi.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(PrefActivity.this, ModeEmploiActivity.class);
				startActivity(intent);
				return true;
			}
		});
		//ABOUT BUTTON
        String versionName;
        try {
			versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "?";
			e.printStackTrace();
		}
		aboutButton.setSummary("Version "+ versionName);
		aboutButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				Intent intent = new Intent(PrefActivity.this, AboutActivity.class);
				startActivity(intent);
				return true; 
			}
		});
	}
	

	private static int getThemeFromPreferences(Context ctx){
		return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(PrefActivity.STYLE_KEY, R.style.CustomThemeLight);
	}
	
	@Override
	public void onBackPressed() {
		if (getIntent().hasExtra("dummy")) {
			Intent intent = new Intent(PrefActivity.this, AccueilActivity.class);
			startActivity(intent);
		}
		super.onBackPressed();
	}
}
