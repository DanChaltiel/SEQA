package com.dan.seqa.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.dan.seqa.bdd.DatabaseFeeder;


public class FirstActivity extends AbstractActivity{

	// Hashkey Facebook : 0gkN5qZC5cTK06Zpsdo5wbkh29E=
	//	B:\PROGRAMMES\SDK\adt-bundle-windows-x86-20130219\sdk
	//  C:\Program Files\Java\SDK\adt-bundle-windows-x86-20130219\sdk
	//http://www.weka.fr/actualite/education-thematique_7847/les-sujets-d-examen-ou-de-concours-peuvent-ils-etre-reproduits-et-diffuses-par-une-personne-privee-editeur-par-exemple-sans-que-cela-ne-constitue-une-atteinte-au-droit-d-auteur-article_11218/

	/*
	//TODO GLOBAL BIG : partager un item, ses scores (mail, réseaux sociaux...) --> export en fichier ?
	//TODO GLOBAL BIG : Faire animation-tuto d'accueil --> tuto en ligne avec impressions d'écran
	//TODO GLOBAL BIG : Améliorer graphiques statistiques

	//TODO GLOBAL : Bibli ChangeLog : mode verbose si admin dynamiquement
	//TODO GLOBAL : Bibli ChangeLog : fonction de versioncode et pas name
	//TODO GLOBAL : FAQ : comme changelog, fichier externe parsé
	//TODO GLOBAL : Annale correction --> popup ratio, statistiques si possible
	//TODO GLOBAL : faire une todolist visualisable (et bien rangée)

	//TODO SERVEUR : bouton "contester/actualiser la correction" dans les annales
	//TODO DB : passer le feeder dans le dbHandler !!! (on createdb, onupgrade...)

	//XXX : Classes du freemium : Databasefeeder, ...
	 */
	/*	
	 * Total des sessions : 
	 * 1991-2001 	= 11
	 * 2002-2011*2	= 18 (pas de 2008)
	 * 2012-2013 	= 2
	 * total = 31
	 * Données free : 180 = 3 sessions 
	 * Données complet : 1860 = 31 sessions
	 */
	/*AnnalesDAO dao = new AnnalesDAO(this);
	Bundle bundle = new Bundle();
	bundle.putBoolean("new", true);
	bundle.putString("annee", "2012");
	bundle.putString("region", "");
	AnnaleModel model = new AnnaleModel();
	model.setSession(dao, "2012", "", false);
	//model.setTheme(dao, "Antibiotiques", false);
	model.setUserInput(1, "b");
	model.setUserInput(2, "c");
	model.setUserInput(3, "d");
	model.setUserInput(4, "e");
	model.setUserInput(4, "b");
	model.setUserInput(4, "c");
	model.setUserInput(5, "c"); 
	bundle.putParcelable("model", model);
	Intent secondeActivite = new Intent(AccueilActivity.this, AnnaleSessionActivity.class);
	secondeActivite.putExtras(bundle);
	startActivity(secondeActivite);	
	//*/

	//CONSTANTES GLOBALES
	public static final boolean MODE_ADMIN=false;
	public static final boolean ERASE_STATS=false;

	//VARIABLES 
	private DatabaseFeeder feeder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Test et remplisage de la BDD (autre thread)
		feeder=new DatabaseFeeder(this);
		feeder.testAndFeed();
		//Lancer l'accueil
		Intent i = new Intent(this, AccueilActivity.class);
		startActivity(i);	
	}

	@Override
	public Dialog onCreateDialog(int identifiant) {		
		return feeder.getDatabaseDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true; //on enlève carrément le menu de l'actionbar
	}
}