package com.dan.seqa.outils;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.dan.seqa.activity.annales.AnnaleSessionActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AnnaleTimer implements Parcelable{

	private TextView text;
	private AnnaleSessionActivity activity;
	private boolean isRunning=false;
	private long totalMillis;
	private long currentMillisEcoulees;
	private long startTime;
	private final static long TOTAL_TIME = (long) (1000*60*60*1.5);//==1h30

	private Handler h = new Handler();
    private boolean fini=false;
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			currentMillisEcoulees = System.currentTimeMillis()-startTime;
			long millisRestant = TOTAL_TIME - (totalMillis+currentMillisEcoulees);
			millisRestant=millisRestant<=0?0:millisRestant;
			String tempsRestant = String.format(Locale.getDefault(), "%d h, %d min, %d sec", 
				TimeUnit.MILLISECONDS.toHours(millisRestant),
				TimeUnit.MILLISECONDS.toMinutes(millisRestant) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisRestant)),
			    TimeUnit.MILLISECONDS.toSeconds(millisRestant) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisRestant))
			);				

			//TEST ADMIN : en mode admin, on arrete le chrono en 10 secondes
//			if(currentMillisEcoulees>=10*1000 && !fini){
			if(currentMillisEcoulees>=TOTAL_TIME && !fini){
				fini=true;
                h.removeCallbacks(run);
				text.setText("TERMINÉ !");	
				activity.tempsFini();
			} else {
                text.setText(tempsRestant);
				h.postDelayed(this, 1000); //Donc revient toutes les 1000ms			
			}
		}
	};

	public AnnaleTimer(AnnaleSessionActivity annaleActivity, TextView view) {
		activity = annaleActivity;
		text=view;
	}
	
	public void start() {
		if(!isRunning) {
			isRunning=true;
			startTime=System.currentTimeMillis();
			currentMillisEcoulees=totalMillis;
			h.postDelayed(run, 0);
		}
	}
	
	public void stop() {
		if(isRunning) {
			isRunning=false;
			currentMillisEcoulees = System.currentTimeMillis()-startTime;
			totalMillis+=currentMillisEcoulees;
			h.removeCallbacks(run);
		}
	}

    public void cancel() {
        fini=true;
        h.removeCallbacks(run);
        text.setText("TERMINÉ !");
    }

    @Override
    public int describeContents() {
        //On renvoie 0, car notre classe ne contient pas de FileDescriptor
        return 0;
    }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	}
}