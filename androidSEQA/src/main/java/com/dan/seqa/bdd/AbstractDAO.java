package com.dan.seqa.bdd;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.dan.seqa.activity.AccueilActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractDAO {
	// Si je décide de la mettre à jour, il faudra changer cet attribut
	public final static int VERSION = 12;
    //TODO stocker chaque session dans un fichier sql différent !! et mettre à jour si on a pas la session !!
//	Permettrait d'avoir la bdd sur la carte SD, donc plus visible pour debugguer...
//	protected final static String NOM_BDD = AccueilActivity.MODE_ADMIN?Environment.getExternalStorageDirectory().getPath() +"database_seqa.db":"database_seqa.db";
	protected final static String NOM_BDD = "database_seqa.db";
	/** CLOSEN_DATABASE=-1<br/> INEXISTANT_TABLE=-2	*/
	protected final static int CLOSEN_DATABASE=-1, INEXISTANT_TABLE=-2;

	protected SQLiteDatabase mDb = null;
	protected DatabaseHandler mHandler = null;

	public AbstractDAO(Context pContext) {
		this.mHandler = new DatabaseHandler(pContext, NOM_BDD, null, VERSION);
	}

	public SQLiteDatabase open() {
		//pour juste lire dans la table, utiliser plutot getreadabledatabase()
		mDb = mHandler.getWritableDatabase();
		return mDb;
	}
 

	/**
	 * Liste les années ou catégories possibles <br/>
	 * Utiliser une constante de {@link AnnalesDAO} ou de {@link EditedAnnalesDAO}
	 */
	public List<String> getDifferent(String anneeOrCategorie) {
		List<String> tmp = new ArrayList<String>();
		Cursor cursor= mDb.rawQuery("select distinct " + anneeOrCategorie+" " +
				"from "+AnnalesDAO.TABLE_NAME,
				null);
		while (cursor.moveToNext()) {
			if (cursor.getString(0)!=null){
				String str[]=cursor.getString(0).split("-");//categorie	
				for (String string : str) {
					String trimstr = string.trim();
					if(!tmp.contains(trimstr) && !trimstr.equals(""))
						tmp.add(trimstr);						
				}
			}
		}
		cursor.close();
        if(anneeOrCategorie.equals(AnnalesDAO.CATEGORIE)){
            Cursor editedCursor= mDb.rawQuery("select distinct " + AnnalesDAO.CATEGORIE+" " +
                    "from "+EditedAnnalesDAO.TABLE_NAME,
                    null);
            while (editedCursor.moveToNext()) {
                if (editedCursor.getString(0)!=null){
                    String str[]=editedCursor.getString(0).split("-");//categorie
                    for (String string : str) {
                        String trimstr = string.trim();
                        if(!tmp.contains(trimstr) && !trimstr.equals(""))
                            tmp.add(trimstr);
                    }
                }
            }
            editedCursor.close();
		}
		Collections.sort(tmp);

		return tmp;
	}


	/**
	 * Liste les sessions possibles
	 */
	public List<String> getDifferentSessions() {
		List<String> tmp = new ArrayList<String>();
		Cursor cursor; 
		cursor= mDb.rawQuery("select " + AnnalesDAO.ANNEE+", " + AnnalesDAO.REGION+" "+
				"from " + AnnalesDAO.TABLE_NAME +" " +
				"group by " + AnnalesDAO.ANNEE+", " + AnnalesDAO.REGION+" " +
				"", 
				null);   
		    
		while (cursor.moveToNext()) {
			if (cursor.getString(0)!=null){
//				String region = "" ;
//				if (cursor.getString(1).equals("N")) region = "Nord";
//				if (cursor.getString(1).equals("S")) region = "Sud";
//				if (cursor.getString(1).equals("mai")) region = "Mai";
//				if (cursor.getString(1).equals("dec")) region = "Décembre";
				tmp.add(cursor.getString(0)+""+cursor.getString(1));
			}
		}		
		cursor.close();
		Collections.sort(tmp);
		Collections.reverse(tmp);

		return tmp;
	}
	
	/**
	 * Compte le nombre de régions pour l'année donnée et retourne vrai s'il y en a plus d'une.
	 */
	public boolean isRegionised(String annee) {
		Cursor cursor= mDb.rawQuery(""+
				"select distinct " + AnnalesDAO.REGION+" "+
				"from " + AnnalesDAO.TABLE_NAME +" " +
				"where " + AnnalesDAO.ANNEE +"=? " +
				"", 
				new String[]{annee});	    
		boolean rtn=cursor.getCount()>1;
		cursor.close();
		return rtn;
	}
	
	/**
	 * Compte le nombre d'entrées dans la table
	 * @return
	 * 		le nombre d'entrées dans la table<br/>
	 * 		-1 si la table est inexistante<br/>
	 * 		-2 si la table est fermée<br/>
	 * @see
	 * 	com.dan.seqa.bdd.AbstractDAO#CLOSEN_DATABASE CLOSEN_DATABASE 
	 * @see
	 * 	com.dan.seqa.bdd.AbstractDAO#INEXISTANT_TABLE INEXISTANT_TABLE
	 */
	public int getCount(String tableName){
		final String DATABASE_COMPARE =	"select count(*) " +
		"from "+tableName+" " +
		";";
		try{
			if(!mDb.isOpen())
				return CLOSEN_DATABASE;
			Cursor mCount= mDb.rawQuery(DATABASE_COMPARE, null);
			mCount.moveToFirst();
			int count= mCount.getInt(0);
			mCount.close();
			return count;	
		}
		catch(NullPointerException e){
			return INEXISTANT_TABLE;
		}	  
		catch(SQLiteException e){
			return INEXISTANT_TABLE;
		}	  
	}
	
	@Deprecated
	public void megaReset(){
		mDb.execSQL(DatabaseHandler.DROP_ANNALE_TABLE);
		mDb.execSQL(DatabaseHandler.CREATE_ANNALE_TABLE);
	}
	
	public void dropAllTables(){
		mDb.execSQL(DatabaseHandler.DROP_ANNALE_TABLE);
		if(AccueilActivity.ERASE_STATS)
			mDb.execSQL(DatabaseHandler.DROP_STATS_TABLE);
	}

	
	public void close() {
		mDb.close();
	}

}