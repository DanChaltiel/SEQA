package com.dan.seqa.activity;

import android.annotation.TargetApi;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dan.newseqa.R;
import com.dan.seqa.bdd.AbstractDAO;
import com.dan.seqa.outils.Codenames;
import com.dan.seqa.outils.Methodes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Activité appelée depuis les préférences
 * Liste les versions de l'appli, du smartphone etc.
 * Utile en cas de bug utilisateur pour avoir un contexte
 */
public class AboutActivity extends AbstractActivity{

    private String versionSEQA, versionAndroid;
	private String timeFirstInstallation, timeLastUpdated;
	private String infosAdmin="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_about);
		
		try {
			versionSEQA = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName + " (v"+this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode+")";
		} catch (NameNotFoundException e) {
			versionSEQA = "?";
			e.printStackTrace();
		}
		versionAndroid="Android "+android.os.Build.VERSION.RELEASE+" \""+ Codenames.getCodename()+"\" (API"+android.os.Build.VERSION.SDK_INT+")";
		
		if(AccueilActivity.MODE_ADMIN){
			infosAdmin = Methodes.getAdminInfos(this);
		}

        ListView vue = (ListView) findViewById(R.id.listAbout);

		List<HashMap<String, String>> liste = getList();		
		ListAdapter adapter = new SimpleAdapter(this,  
				liste, 
				R.layout.liste_simple,//comme le colorLayout par défaut mais non cliquable
				new String[] {"titre", "description"}, //clés entrée dans la méthode privée
				new int[] {android.R.id.text1, android.R.id.text2 }); //id par défaut du package android
		vue.setAdapter(adapter);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private void getSoftTimes(){
		long installation, update;
		try {
			installation = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).firstInstallTime;
			update = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).lastUpdateTime;

			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(installation);
			timeFirstInstallation = "Le "+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);
			cal.setTimeInMillis(update);
			timeLastUpdated = "Le "+cal.get(Calendar.DATE)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR);			
		}  catch (NoSuchFieldError e){		//gère les version anté-Gingerbread
			timeFirstInstallation = timeLastUpdated = "Version d'Android trop ancienne pour afficher cette information";
			e.printStackTrace();			
		} catch (NameNotFoundException e) { //obligatoire pour getPackageInfo
			timeFirstInstallation = timeLastUpdated = "Cette information est introuvable";
			e.printStackTrace();
		}
	}
	
	
	private List<HashMap<String, String>> getList(){
		List<HashMap<String, String>> rtnListe = new ArrayList<HashMap<String, String>>();
		List<String[]> entrees = new ArrayList<String[]>();
		getSoftTimes();
		entrees.add(new String[]{"Version du SEQA", versionSEQA});
		entrees.add(new String[]{"Date d'installation", ""+timeFirstInstallation});
		entrees.add(new String[]{"Date de dernière mise à jour", ""+timeLastUpdated});
		entrees.add(new String[]{"Version d'Android", versionAndroid});
        entrees.add(new String[]{"Version de la base de données", ""+ AbstractDAO.VERSION});
		if(AccueilActivity.MODE_ADMIN)
			entrees.add(new String[]{"Infos Admin", infosAdmin});
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		entrees.add(new String[]{"Informations légales","Copyright Dan Chaltiel 2013-"+year+"."+"\n"+"Tous droits réservés"});

        for (String[] entree : entrees) {
            HashMap<String, String> element = new HashMap<String, String>();
            element.put("titre", entree[0]);
            element.put("description", entree[1]);
            rtnListe.add(element);
        }

		return rtnListe;
	}
}