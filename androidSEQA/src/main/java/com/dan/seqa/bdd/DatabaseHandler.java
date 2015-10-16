package com.dan.seqa.bdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.dan.seqa.outils.Methodes;

/**
 * Le gestionnaire de la base de données <br/> 
 * La version est dans AbstractDAO
 * @author Dan
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	/*
	 * TABLE DES ANNALES
	 */

	public static final String CREATE_ANNALE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + AnnalesDAO.TABLE_NAME + " (" +
					AnnalesDAO.KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					AnnalesDAO.TYPE + " VARCHAR(3), " + //QCM ou QCS
					AnnalesDAO.ANNEE + " VARCHAR(4), " +
					AnnalesDAO.REGION + " VARCHAR(1), " +
					AnnalesDAO.NUMERO + " VARCHAR(5), " +
					AnnalesDAO.CATEGORIE + " TEXT, " +
					AnnalesDAO.QUESTION + " TEXT, " +
					AnnalesDAO.QCM_A + " TEXT, " +
					AnnalesDAO.QCM_B + " TEXT, " +
					AnnalesDAO.QCM_C + " TEXT, " +
					AnnalesDAO.QCM_D + " TEXT, " +
					AnnalesDAO.QCM_E + " TEXT, " +
					AnnalesDAO.CORRECTION + " VARCHAR(5));";
	public static final String CREATE_EDITED_ANNALE_TABLE =
			"CREATE TABLE IF NOT EXISTS " + EditedAnnalesDAO.TABLE_NAME + " (" +
					EditedAnnalesDAO.KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					EditedAnnalesDAO.TYPE + " VARCHAR(3), " + //QCM ou QCS
					EditedAnnalesDAO.ANNEE + " VARCHAR(4), " +
					EditedAnnalesDAO.REGION + " VARCHAR(1), " +
					EditedAnnalesDAO.NUMERO + " VARCHAR(5), " +
					EditedAnnalesDAO.CATEGORIE + " TEXT, " +
					EditedAnnalesDAO.QUESTION + " TEXT, " +
					EditedAnnalesDAO.QCM_A + " TEXT, " +
					EditedAnnalesDAO.QCM_B + " TEXT, " +
					EditedAnnalesDAO.QCM_C + " TEXT, " +
					EditedAnnalesDAO.QCM_D + " TEXT, " +
					EditedAnnalesDAO.QCM_E + " TEXT, " +
					EditedAnnalesDAO.CORRECTION + " VARCHAR(5), " +
					EditedAnnalesDAO.COMMENTAIRE + " TEXT);";
	public static final String DROP_ANNALE_TABLE = "DROP TABLE IF EXISTS " + AnnalesDAO.TABLE_NAME + ";";
	public static final String DROP_EDITED_ANNALE_TABLE = "DROP TABLE IF EXISTS " + EditedAnnalesDAO.TABLE_NAME + ";";
	/*
	 * TABLE DES STATISTIQUES
	 */
	public static final String CREATE_STATS_TABLE =
			"CREATE TABLE IF NOT EXISTS " + StatsDAO.TABLE_NAME + " (" +
					StatsDAO.KEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    StatsDAO.SESSION + " VARCHAR(5), " + //4 année + 1 N/S
                    StatsDAO.DATE + " INTEGER, " +
                    StatsDAO.SCORE_RATIO + " REAL, " +
                    StatsDAO.SCORE_TOTAL + " REAL);";
	public static final String DROP_STATS_TABLE = "DROP TABLE IF EXISTS " + StatsDAO.TABLE_NAME + ";";

	//VARIABLES

	//CONSTRUCTEUR
	public DatabaseHandler(Context ctx, String name, CursorFactory factory, int version) {
		super(ctx, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Methodes.alert("DatabaseHandler.onCreate() - Création des tables de la Base de Données");
		db.execSQL(CREATE_ANNALE_TABLE);
		db.execSQL(CREATE_EDITED_ANNALE_TABLE);
		db.execSQL(CREATE_STATS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Methodes.alert("Mise à jour des tables de la Base de Données");
//		db.execSQL(DROP_EDITED_ANNALE_TABLE);
//		db.execSQL(CREATE_EDITED_ANNALE_TABLE);
//		resetDB(db);
//		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Methodes.alert("DownGrade des tables de la Base de Données");
//		db.execSQL(DROP_EDITED_ANNALE_TABLE);
//		db.execSQL(CREATE_EDITED_ANNALE_TABLE);
//		resetDB(db);
//		onCreate(db);
	}

	private static void resetDB(SQLiteDatabase db) {
//		db.execSQL(DROP_ANNALE_TABLE);
//		if(AccueilActivity.ERASE_STATS)
//			db.execSQL(DROP_STATS_TABLE);
		//Normalement on drop pas les stats, sinon on perd tout !
	}
}