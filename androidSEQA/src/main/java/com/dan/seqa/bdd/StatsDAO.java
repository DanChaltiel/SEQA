package com.dan.seqa.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;

import com.dan.seqa.modeles.ScoreEtCompte;
import com.dan.seqa.modeles.Stat;

import java.util.ArrayList;

public class StatsDAO extends AbstractDAO{
    public static final String 	KEY = "id",
            SESSION = "session",
            DATE = "date",
            SCORE_RATIO = "scoreRatio",
            SCORE_TOTAL = "scoreTotal";
    public static final String TABLE_NAME = "Statistiques";

	private int noteMinimale;

	public StatsDAO(Context pContext) {
		super(pContext);
		noteMinimale = PreferenceManager.getDefaultSharedPreferences(pContext).getInt("minStatsValue", 15);
		mDb = open();
	}

	public void addStat(Stat stat){
		ContentValues value = new ContentValues();
		value.put(DATE, stat.getTimestamp());
		value.put(SESSION, stat.getSession());
		value.put(SCORE_RATIO, stat.getScoreRatio());
		value.put(SCORE_TOTAL, stat.getScoreTotal());
		mDb.insert(TABLE_NAME, null, value);
	}

    /**
     * Récupère la liste de statistiques de cette session
     * @param pSession le nom de la session
     * @param tri le champs sur lequel on trie
     * @return une liste de Stat
     */
	public ArrayList<Stat> getAllStatsOfSession(String pSession, String tri) {
		ArrayList<Stat> tmp = new ArrayList<>();
		Cursor cursor = mDb.rawQuery("select * from " + TABLE_NAME +
				" where "+SESSION+" = ?" +
				" order by "+tri+" desc",
				new String[]{pSession});
		while (cursor.moveToNext()) {
			if(cursor.getDouble(4)>=noteMinimale)//on ne prend en compte que les notes supérieures au minimum
				tmp.add(new Stat(cursor.getString(1),	//session	//on commence à 1 parce que 0 c'est id et osef
						cursor.getLong(2), 		//date
						cursor.getDouble(3),	//scoreRatio
						cursor.getDouble(4)));	//scoreTotal
		}
		cursor.close();
		return tmp;
	}

	/**
	 * Récupère la statistique du meilleur ratio pour cette session
	 * @param pSession la session
	 * @return Un objet stats si la session est peuplée<br/>
	 * 	null sinon
	 */
	public Stat getBestOfSession(String pSession) {
		Cursor cursor = mDb.rawQuery(
				"select * from " + TABLE_NAME + 
				" where "+SESSION+" = ? " +
				" and "+SCORE_RATIO+" = (select max("+SCORE_RATIO+") from " + TABLE_NAME + " where "+SESSION+" = ? )",
				new String[]{pSession, pSession});
		cursor.moveToNext();
		if(cursor.getCount()==0 || cursor.isNull(4))
			return null;//on commence à 1 parce que 0 c'est id et osef
		Stat tmp=new Stat(cursor.getString(1),	//session
				cursor.getLong(2), 	//date
				cursor.getDouble(3), 	//date
				cursor.getDouble(4));	//ratio
		cursor.close();
		return tmp;
	}

	@Deprecated
	public StatsDAO razStatBDD() {
		mDb.execSQL(DatabaseHandler.DROP_STATS_TABLE);
		mDb.execSQL(DatabaseHandler.CREATE_STATS_TABLE);
		return this;
	}

	/**
	 * Calcule la moyenne sur le temps défini
	 * @param pSession la session
	 * @param temps : semaine/mois/an
	 * @return le tuple contenant le score et le compte
	 */	
	public ScoreEtCompte getMoyenne(String pSession, String temps) {
		long timestampPrecedent=0;
        switch (temps) {
            case "semaine":
                timestampPrecedent = System.currentTimeMillis() - (1000l * 3600l * 24l * 7l);//7j*24h*3600sec*1000ms
                break;
            case "mois":
                timestampPrecedent = System.currentTimeMillis() - (1000l * 3600l * 24l * 30l);//on met tout en long sinon on calcule sur des int et ça plante
                break;
            case "an":
                timestampPrecedent = System.currentTimeMillis() - (1000l * 3600l * 24l * 365l);
                break;
            case "total":
                timestampPrecedent = 0;
                break;
        }
		
		double minRatio = noteMinimale/120.0;
		Cursor cursor = mDb.rawQuery(
					"select count("+KEY+"), avg("+SCORE_RATIO+") from " + TABLE_NAME +
					" where "+SESSION+" = ? " +
					" and "+DATE+" >= ? " +
					" and "+SCORE_RATIO+">=? " +
					"", new String[]{pSession, ""+timestampPrecedent, ""+minRatio});
		cursor.moveToFirst();
		int count =cursor.getInt(0);
		double ratio = cursor.getFloat(1);
		cursor.close();
		return new ScoreEtCompte(ratio, count);
	}
	

	/**
	 * Compte le nombre d'entrées dans la table des statistiques
	 * @param noteMinimale : la note minimale des préférences
	 * @return le nombre d'entrées dans la table<br/>
	 * 		-1 si la table est inexistante<br/>
	 * 		-2 si la table est fermée<br/>
	 * @see com.dan.seqa.bdd.AbstractDAO#CLOSEN_DATABASE CLOSEN_DATABASE 
	 * @see com.dan.seqa.bdd.AbstractDAO#INEXISTANT_TABLE INEXISTANT_TABLE
	 */
	public int getCount(int noteMinimale){
		try{
			if(!mDb.isOpen())
				return CLOSEN_DATABASE;
			Cursor mCount = mDb.rawQuery("select count(*) " +
					"from "+TABLE_NAME+" " +
					"where "+SCORE_RATIO+"*"+SCORE_TOTAL+">? " +
					";", new String[] {""+noteMinimale});
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
	

	/**
	 * Compte le nombre de statistiques entrées dans la table pour une session ou un thème donné
	 * @return
	 * 		le nombre d'entrées dans la table<br/>
	 * 		-1 si la table est inexistante<br/>
	 * 		-2 si la table est fermée
	 * @see com.dan.seqa.bdd.AbstractDAO#CLOSEN_DATABASE CLOSEN_DATABASE 
	 * @see com.dan.seqa.bdd.AbstractDAO#INEXISTANT_TABLE INEXISTANT_TABLE
	 */
	public int getSpecificCount(String pTable, String pSession){
		try{
			if(!mDb.isOpen())
				return CLOSEN_DATABASE;

			Cursor mCount = mDb.rawQuery("select count(*)" +
					" from " + pTable + 
					" where "+SESSION+" = ?",
					new String[]{pSession});
			mCount.moveToFirst();
			int count= mCount.getInt(0);
			mCount.close();
			return count;	
		}
		catch(NullPointerException e){
			return INEXISTANT_TABLE;
		}	  
	}

	public int compterEntreesCorrection(String type,String patternCorrection){
		Cursor cursor = mDb.rawQuery(
				"select count(*) from " + AnnalesDAO.TABLE_NAME + 
				" where "+AnnalesDAO.TYPE+" = ? " +
				" and "+AnnalesDAO.CORRECTION+"= ? " +
				"", new String[]{type, ""+patternCorrection});
		cursor.moveToFirst();

		int count= cursor.getInt(0);
		cursor.close();
		return count;
	}
}