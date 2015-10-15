package com.dan.seqa.activity.annales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.fragments.ChoixSessionDialogFragment;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.outils.AnnaleTimer;

public class AnnaleSessionActivity extends AbstractAnnaleActivity{
	private AnnaleTimer timer;
	private boolean startTimer=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		//On instancie les variables et les listeners
		TextView timerView = (TextView)  findViewById(R.id.timer);
		//on gère le modèle et le bundle
		Bundle extraBundle = getIntent().getExtras();
		if(savedInstanceState==null) {
			timer = new AnnaleTimer(this, timerView);
			if(extraBundle!=null && !extraBundle.isEmpty()){
				String annee = extraBundle.getString("annee");
				String region = extraBundle.getString("region");
				this.model=extraBundle.getParcelable("model");
				choixSessionOK(annee, region);
			}  else {
				this.model=new AnnaleModel();
				montrerDialogueChoixSession();
			}
		} else {
			this.model = savedInstanceState.getParcelable("MODEL");
			int gCurrent = savedInstanceState.getInt("CURRENT");
			timer = savedInstanceState.getParcelable("TIMER");//dejà started
			if(model.annee==null)
				montrerDialogueChoixSession();	
			else {
				choixSessionOK(model.annee, model.region);//ça va créer pager
				pager.setCurrentItem(gCurrent);
			}
		}
	}
	
	protected void createPager(){
		// Création de l'adapter qui s'occupera de l'affichage de la liste des fragments et de l'affectation au ViewPager
		pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) { //position va de 0 à 59 ?
				currentFrag=position;
				seeker.setProgress(position);
				goPreviousButton.setEnabled(position!=0);
				goNextButton.setEnabled(position!=59);
			}
		});
		mPagerAdapter = new AnnalePagerAdapter(getSupportFragmentManager(), model);
		pager.setAdapter(this.mPagerAdapter);
	}
	
	/**Gestion de l'appel de la dialogbox (modale)*/
	private void montrerDialogueChoixSession() {
		DialogFragment newFragment = ChoixSessionDialogFragment.newInstance("Choisis une session", this);
		newFragment.setCancelable(false);
		newFragment.show(getSupportFragmentManager(), "dialog");
	}
	
	/**Appelée entre autres par la dialogBox*/
	public void choixSessionOK(String pAnnee, String pRegion){
		//On met à jour le modèle
		AnnalesDAO annaleDao=new AnnalesDAO(this);
		EditedAnnalesDAO editedDao=new EditedAnnalesDAO(this);
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean randomize  = preferences.getBoolean("randomize", false);
		model.setSession(annaleDao, editedDao, pAnnee, pRegion, randomize); 
		//on crée le pager avec les fragments d'Annale
		createPager();
		//on lance le timer d'1h30
		startTimer=true;
		timer.start();
		//on crée la searchList ou les searchboutons selon l'orientation
		creerVerticalSearchList();		
		creerHorizontalSearchList();
	}

	/**Gestion de la fin du timer, appelé depuis AnnaleTimer*/
	public void tempsFini() {
		Toast.makeText(AnnaleSessionActivity.this, "Temps terminé !", Toast.LENGTH_SHORT).show();
		AlertDialog.Builder builder = new AlertDialog.Builder(AnnaleSessionActivity.this);
		builder	.setTitle("Temps écoulé !")
		.setMessage("Fin du temps imparti, que veux-tu faire ?")
		.setCancelable(false)
		.setPositiveButton("Vas-y, on corrige", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				correction();
			}
		})
		.setNegativeButton("Non non, je veux continuer sans chronomètre", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {}
		});
		builder.create().show();
	}

    @Override
    protected void correction() {
        timer.cancel();
        super.correction();
    }

	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putParcelable("MODEL", model);
		outState.putInt("CURRENT", currentFrag);
		outState.putParcelable("TIMER", timer);
	}

	@Override
	public void onPause(){
		super.onPause();
		if(timer!=null)
			timer.stop();		
	}
	@Override
	public void onResume(){
		super.onResume();
		if(timer!=null && startTimer)
			timer.start();
	}

}

