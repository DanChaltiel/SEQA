package com.dan.seqa.activity.donnees;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.dan.seqa.outils.Methodes;

public class CountdownActivity extends AbstractActivity{

	private final static String DATE_CONCOURS = "17/12/2014 12h30";
	private final static int EASTER_EGG_GOAL = 3;

	private TextView dateConcours, countdownMois, countdownJours, countdownHeures, countdownMinutes, countdownSecondes;
	private int easterEggCount=0;
	private String date, time;
	private Handler h = new Handler();
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			updateTime();	
			h.postDelayed(this, 1000); //Donc revient toutes les 1000ms	
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_countdown);

		dateConcours = (TextView) findViewById(R.id.date_concours);
		countdownMois = (TextView) findViewById(R.id.countdown_mois);
		countdownJours = (TextView) findViewById(R.id.countdown_jours);
		countdownHeures = (TextView) findViewById(R.id.countdown_heures);
		countdownMinutes = (TextView) findViewById(R.id.countdown_minutes);
		countdownSecondes = (TextView) findViewById(R.id.countdown_secondes);

		//		dateConcours.setText("17 et 18 décembre 2014");
		dateConcours.setText("17/12/2014 à 12h30");

		//On set le calendrier
		final Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);

		final DatePickerDialog dpd = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {	 
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						date=dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year;
						dateConcours.setText(date+" "+time);
					}
				}, 
				mYear, mMonth, mDay);
		final TimePickerDialog tpd = new TimePickerDialog(
				this,
				new TimePickerDialog.OnTimeSetListener() {		 
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						time=hourOfDay + "h" + minute;
						dateConcours.setText("le "+date+" à "+time);
					}
				}, 
				mHour, mMinute, true);
		//On set les boutons
		Button changeDateButton = (Button) findViewById(R.id.changeDateButton);
		changeDateButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				dpd.show();
			}
		});
		Button changeTimeButton = (Button) findViewById(R.id.changeTimeButton);
		changeTimeButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {	
				tpd.show();
			}
		});
        ScrollView layout = (ScrollView) findViewById(R.id.timeLeftLayout);
		layout.setOnClickListener(new EasterEggListener());
	}

	private void updateTime() {
		SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy HH'h'mm", Locale.FRANCE);
		Date date;
		try {
			date = format.parse(DATE_CONCOURS);
            long millisRestant = date.getTime()-System.currentTimeMillis();
            millisRestant=millisRestant<=0?0:millisRestant;
            countdownMois.setText(""+Methodes.arrondir(TimeUnit.MILLISECONDS.toDays(millisRestant)/30,1)+" mois");
            countdownJours.setText(""+TimeUnit.MILLISECONDS.toDays(millisRestant)+" jours");
            countdownHeures.setText(""+TimeUnit.MILLISECONDS.toHours(millisRestant)+" heures");
            countdownMinutes.setText(""+TimeUnit.MILLISECONDS.toMinutes(millisRestant)+" minutes");
            countdownSecondes.setText(""+TimeUnit.MILLISECONDS.toSeconds(millisRestant)+" secondes");
		} catch (ParseException e) {
			Methodes.alert(e.getMessage());
			countdownMois.setText("erreur");
			countdownJours.setText("erreur");
			countdownHeures.setText("erreur");
			countdownMinutes.setText("erreur");
			countdownSecondes.setText("erreur");
		}
	}

	@Override
	protected void onResume() {
		h.postDelayed(run, 0);		
		super.onResume();
	}
	@Override
	protected void onStop() {
		h.removeCallbacks(run);
		super.onStop();
	}

	private class EasterEggListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Methodes.alert("clic !");
			easterEggCount++;
			if(easterEggCount>=EASTER_EGG_GOAL)
				Toast.makeText(CountdownActivity.this, "Tididi tou, tididi titi, tididi tou, tididitititi tiiii tididi tididiiiii", Toast.LENGTH_SHORT).show();
		}		
	}
}