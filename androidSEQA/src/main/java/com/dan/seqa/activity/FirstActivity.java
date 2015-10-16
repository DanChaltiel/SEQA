package com.dan.seqa.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.bdd.DatabaseFeeder;

import java.util.List;

/**
 * Activité qui teste si la base de donnée est à jour, qui la met à jour si
 * besoin puis qui lance l'activité d'accueil
 */
public class FirstActivity extends AbstractActivity {

    // Hashkey Facebook : 0gkN5qZC5cTK06Zpsdo5wbkh29E=
    //	B:\PROGRAMMES\SDK\adt-bundle-windows-x86-20130219\sdk
    //  C:\Program Files\Java\SDK\adt-bundle-windows-x86-20130219\sdk
    //http://www.weka.fr/actualite/education-thematique_7847/les-sujets-d-examen-ou-de-concours-peuvent-ils-etre-reproduits-et-diffuses-par-une-personne-privee-editeur-par-exemple-sans-que-cela-ne-constitue-une-atteinte-au-droit-d-auteur-article_11218/

	/*
        TODO GLOBAL BIG : partager un item, ses scores (mail, réseaux sociaux...) --> export en fichier ?
        TODO GLOBAL BIG : Faire animation-tuto d'accueil --> tuto en ligne avec impressions d'écran
        TODO GLOBAL BIG : Améliorer graphiques statistiques
        TODO GLOBAL : Bibli ChangeLog : mode verbose si admin dynamiquement
        TODO GLOBAL : Bibli ChangeLog : fonction de versioncode et pas name
        TODO GLOBAL : FAQ : comme changelog, fichier externe parsé
        TODO GLOBAL : Annale correction --> popup ratio, statistiques si possible
        TODO GLOBAL : faire une todolist visualisable (et bien rangée)
        TODO SERVEUR : bouton "contester/actualiser la correction" dans les annales
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

    private ProgressBar progressBar;
    private TextView progressTV;
    private TextView progressNameTV;
    private int max = 0;
    private List<String> assetToRead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeFromPreferences(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        TextView progressMaxTV = (TextView) findViewById(R.id.progressMax);
        progressBar = (ProgressBar) findViewById(R.id.firstProgressBar);
        progressTV = (TextView) findViewById(R.id.progress);
        progressNameTV = (TextView) findViewById(R.id.progressName);
        assetToRead = DatabaseFeeder.test(this);
        max = assetToRead.size();
        progressBar.setMax(max);
        progressMaxTV.setText("" + max);
        if(max!=0) {
            DatabaseFeederTask task = new DatabaseFeederTask();
            task.execute(assetToRead);
        } else {
            Intent i = new Intent(FirstActivity.this, AccueilActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true; //on enlève carrément le menu de l'actionbar
    }

    /**
     * Tache asynchrone permettant de mettre à jour la base de données
     * et d'informer l'UI du thread principal (progressbar etc...)
     */
    private class DatabaseFeederTask extends AsyncTask<List<String>, String, Void> {

        @Override
        protected Void doInBackground(List<String>[] assetToRead) {
            int progress=1;
            for (String assetFile : assetToRead[0]) {
                String nomFichier = "sessions/" + assetFile+".sql";
                DatabaseFeeder.feed(FirstActivity.this, nomFichier);
                publishProgress(nomFichier, ""+progress);
                progress++;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String[] values) {
            super.onProgressUpdate(values);
            int progress = Integer.parseInt(values[1]);
            //On met à jour l'UI du main thread
            progressNameTV.setText(values[0]);
            progressTV.setText(values[1]);
            progressBar.setProgress(progress);
            //Et si on a fini, un petit dialogue et on lance l'appli
            if (progress == max) {
                String fileList = "";
                for (String file : assetToRead) {
                    fileList += file.replace(".sql", "").replace("//session//", "") + ", ";
                }
                fileList += "___";
                fileList = fileList.replace(", ___", ".");
                AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                builder.setTitle("Base de données mise à jour")
                    .setMessage("On a rajouté toutes ces sessions : \n" + fileList)
                    .setPositiveButton("Super !", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(FirstActivity.this, AccueilActivity.class);
                            startActivity(i);
                        }
                    })
                    .create().show();
            }
        }
    }
}