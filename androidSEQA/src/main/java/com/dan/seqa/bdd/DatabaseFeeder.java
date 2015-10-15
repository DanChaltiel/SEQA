package com.dan.seqa.bdd;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteException;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.dan.seqa.outils.Methodes;

import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("deprecation")
public class DatabaseFeeder{

	//CONSTANTES
	private final static String DIALOG_MESSAGE = "Bienvenue dans le Super Entraineur aux QCM des Annales ! \n" +
			"Une petite minute de patiente, le temps d'installer la base de données...";
	private final static int PROGRESS_DIALOG_ID = 1000;
	private final static String NOM_FICHIER_SQL = "seqa_db.sql";
	private final static String NOM_FICHIER_SQL_PRE_HC = "seqa_db_preHC.sql"; //avant HoneyCoombs
	private final static int MAX_ITEMS_NUMBER = 18000; //old

	//todo trim() !

	//VARIABLES
	private Activity activity;
	private Thread mProgress=null;
	private static ProgressDialog mProgressBar;
	private final int databaseCount;
	private final Handler mHandler = new ProgressionHandler();
	private AnnalesDAO dao;
	private boolean ilFautNourrirLaBdd = false;

	public DatabaseFeeder(Context context) {
		activity=(Activity) context;
		dao = new AnnalesDAO(context);

		Long debut = System.currentTimeMillis();
		databaseCount=dao.getCount(AnnalesDAO.TABLE_NAME);
		//si la bdd est vide ou inexistante, on nourrit	
		ilFautNourrirLaBdd=databaseCount==0 || databaseCount==AbstractDAO.INEXISTANT_TABLE;
		if (ilFautNourrirLaBdd)
			Methodes.alert("BDD vide ou inexistante");		
		//		else if (databaseCount<MAX_ITEMS_NUMBER){
		//			ilFautNourrirLaBdd=true;
		//			Methodes.alert("BDD incomplete");
		//		}
		Long fin = System.currentTimeMillis();
		String temps =""+(fin-debut);
		Methodes.alert("Check de la base de donnée : OK (temps="+temps+"ms, count="+databaseCount+")");
	}

	public void testAndFeed(){
		mProgress = new Thread(new ProgressionRunnable());
		mProgress.start();
		//On ne peut pas lancer le dialogue depuis le thread --> obligé de tester ici aussi...
		if(ilFautNourrirLaBdd) {
			activity.showDialog(PROGRESS_DIALOG_ID);
		}			
		else
			mProgress.interrupt();
		//Toast.makeText(activity, "Oups, il y a eu un petit soucis, il est possible que la base de donnée se réinstalle au prochain démarrage... Désolé ! ^^'", Toast.LENGTH_LONG).show();
	}

	public Dialog getDatabaseDialog() {
		if(mProgressBar == null){
			mProgressBar = new ProgressDialog(activity);
			mProgressBar.setCancelable(false);
			mProgressBar.setTitle("Installation de la base de données");
			mProgressBar.setMessage(DIALOG_MESSAGE);
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// On interrompt le thread  
					mProgress.interrupt();
					Toast.makeText(activity, "canceled", Toast.LENGTH_SHORT).show();
					activity.removeDialog(PROGRESS_DIALOG_ID);
				}
			});
		}
		return mProgressBar;
	}

	public void cancel() {
		mProgress.interrupt();
		activity.removeDialog(PROGRESS_DIALOG_ID);
	}

	private static class ProgressionHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//Les arg sont forcément des int, et là obj est un String
			mProgressBar.setProgress(msg.arg1);
			//			mProgressBar.setMessage(DIALOG_MESSAGE+"\n"+msg.obj);
			mProgressBar.setMessage(DIALOG_MESSAGE);
			//On peut utiliser un bundle mais c'est lourd il parait...
		}
	}

	private class ProgressionRunnable implements Runnable{
		@Override
		public void run() { 
			Long debut = System.currentTimeMillis();
			if(ilFautNourrirLaBdd){//càd getcount = 0 ou base inexistante
				Methodes.alert("Création de la base de donnée");

				try {
					feedBDDbySQL(NOM_FICHIER_SQL);//SQLite 3.7.4
				} catch (SQLiteException e) {
					Methodes.alert("SQLite 3.6.22 avant HoneyCoombs (2.3.x et avant)");
					Methodes.alert(e.getMessage());
					dao.mDb.execSQL("ROLLBACK");
					feedBDDbySQL(NOM_FICHIER_SQL_PRE_HC);//SQLite 3.6.22 avant HoneyCoombs (2.3.x et avant)
				}


				Long fin = System.currentTimeMillis();
				String temps =""+(fin-debut)/1000;
				Methodes.alert("Création de la base de donnée : OK (temps "+temps+"s). " +
						"Données:"+databaseCount+"-->"+dao.getCount(AnnalesDAO.TABLE_NAME));
			} else if(databaseCount>MAX_ITEMS_NUMBER){ //à automatiser...
				Methodes.alert("Base de données surchargée --> effacement total");
				dao.megaReset();			
				Long fin = System.currentTimeMillis();
				String temps =""+(fin-debut)/1000;
				Methodes.alert("Base de données surchargée --> effacement total : OK (temps "+temps+"s)" +
						"Données:"+databaseCount+"-->"+dao.getCount(AnnalesDAO.TABLE_NAME));
				Toast.makeText(activity, "Oups, il y a eu un petit soucis, il faut juste que la base de donnée se réinstalle au prochain démarrage... Désolé ! ^^'", Toast.LENGTH_LONG).show();
			} else	{
				Methodes.alert("La Base de données est OK (databaseCount="+databaseCount+")");
			}
			dao.close();				
		}

		private void feedBDDbySQL(String nomFichier){
			Methodes.alert("nomFichier="+nomFichier);
			Long debut = System.currentTimeMillis();
			try {
				AssetManager mgr = activity.getAssets();

				dao.dropAllTables();
				//dao.megaReset();			
				InputStream	in = mgr.open(nomFichier);
				String[] statements = FileHelper.parseSqlFile(in);
				mProgressBar.setMax(statements.length);
				int i = 0;
				for (String statement : statements) {
					dao.mDb.execSQL(statement);
					Message msg = mHandler.obtainMessage(0, i, 0, "ligne "+i);
//					Methodes.alert("dummy - "+i);
					mHandler.sendMessage(msg);
					//					Thread.sleep(200);//Sinon ça va trop vite c'est la loose xD
					i++;
				}
				Methodes.alert("BDD exécutée via "+nomFichier+" : "+statements.length+" instructions exécutées en "+(System.currentTimeMillis()-debut)+" ms");

				in.close();
			} catch (IOException e) {
				Methodes.alert(e.getMessage());
				//			} catch (InterruptedException e) {
				//				Methodes.alert(e.getMessage());
			} 
			Message msg = mHandler.obtainMessage(0, 100, 0, "Terminé !");
			mHandler.sendMessage(msg);
			mProgress.interrupt();
			activity.removeDialog(PROGRESS_DIALOG_ID);
		}	
	}
}