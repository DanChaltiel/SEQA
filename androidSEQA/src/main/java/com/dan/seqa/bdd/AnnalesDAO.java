package com.dan.seqa.bdd;

import android.content.Context;
import android.database.Cursor;

import com.dan.seqa.modeles.Item;
import com.dan.seqa.outils.Methodes;

import java.util.ArrayList;

/**
 * Data Access Object des annales non modifiées
 *
 */
public class AnnalesDAO extends AbstractDAO{

	public static final String 	
		KEY = "id", TYPE = "type", 
		ANNEE = "annee", REGION = "region", NUMERO = "numero",
		QUESTION = "question", CATEGORIE = "categorie",
		QCM_A = "qcmA", QCM_B = "qcmB", QCM_C = "qcmC", QCM_D = "qcmD", QCM_E = "qcmE",
		CORRECTION = "correction";
	public static final String TABLE_NAME = "Annales";

    public AnnalesDAO(Context pContext) {
		super(pContext);
		super.mDb = open();
	}

	/**
	 *
	 * @return la liste des (60 normalement) items composant la session
	 */
	public ArrayList<Item> selectBySession(String pAnnee, String pRegion) {
		//on gère les items sans session simplement avec pRegion=""
//		long debut = System.currentTimeMillis();
		ArrayList<Item> tmp = new ArrayList<>();
		Cursor cursor = mDb.rawQuery("select * from " + TABLE_NAME + 
				" where "+ANNEE+" = ?" +
				" AND "+REGION+" = ?",
				new String[]{pAnnee, pRegion});
		while (cursor.moveToNext()) {//on commence à 1 parce que 0 c'est id et osef
			tmp.add(new Item(cursor.getString(1),	//Type
					cursor.getString(2), 	//Année
					cursor.getString(3), 	//Région
					cursor.getString(4), 	//Numéro
					cursor.getString(5), 	//Catégorie
					cursor.getString(6), 	//Question
					cursor.getString(7), 	//A
					cursor.getString(8), 	//B
					cursor.getString(9), 	//C
					cursor.getString(10), 	//D
					cursor.getString(11), 	//E
					cursor.getString(12)));	//Correction
		}

		cursor.close();
		return tmp;
	}

    /**
     * @return la liste des items composant ayant le theme donné
     */
	public ArrayList<Item> selectByTheme(String pTheme) {
		ArrayList<Item> tmp = new ArrayList<>();
		Cursor cursor = mDb.rawQuery("select * from " + TABLE_NAME + 
				" where "+CATEGORIE+" like ?" +
				" order by "+ANNEE+" desc",
				new String[]{"%"+pTheme+"%"});
		while (cursor.moveToNext()) {//on commence é 1 parce que 0 c'est id et osef
			tmp.add(new Item(cursor.getString(1),	//Type
					cursor.getString(2), 	//Année
					cursor.getString(3), 	//Région
					cursor.getString(4), 	//Numéro
					cursor.getString(5), 	//Catégorie
					cursor.getString(6), 	//Question
					cursor.getString(7), 	//A
					cursor.getString(8), 	//B
					cursor.getString(9), 	//C
					cursor.getString(10), 	//D
					cursor.getString(11), 	//E
					cursor.getString(12)));	//Correction
		}

		cursor.close();
		return tmp;
	}

    /**
     * Interface entre les QCM édités ou non. <br/>
     * Sélectionne le QCM de mêmes année, région et numéro
     * @param editedItem l'item édité
     * @return l'item non édité
     */
	public Item selectItem(Item editedItem){
		Cursor cursor = mDb.query(
                TABLE_NAME,
                null,
                ANNEE + "=? and " + REGION + "=? and " + NUMERO + "=?",
                new String[]{editedItem.getAnnee(), editedItem.getRegion(), editedItem.getNumero()},
                null, null, null);
		if(cursor.getCount()!=1)
			return null;
		cursor.moveToNext();
		Item rtn = new Item(cursor.getString(1),	//Type
				cursor.getString(2), 	//Année
				cursor.getString(3), 	//Région
				cursor.getString(4), 	//Numéro
				cursor.getString(5), 	//Catégorie
				cursor.getString(6), 	//Question
				cursor.getString(7), 	//A
				cursor.getString(8), 	//B
				cursor.getString(9), 	//C
				cursor.getString(10), 	//D
				cursor.getString(11), 	//E
				cursor.getString(12));	//Commentaire		
		cursor.close();
		return rtn;
	}

    /**
     * Cherche le keyWord dans tous les champs des items
     * @param keyWord le keyWord
     * @return liste des items contenant le keyWord quelque part
     */
	public ArrayList<Item> selectByKeyWord(String keyWord) {
		ArrayList<Item> tmp = new ArrayList<>();
		Cursor cursor = mDb.rawQuery("select * from " + TABLE_NAME + 
				" where "+QUESTION+" like ?" +
				" or "+CATEGORIE+" like ?" +
				" or "+QCM_A+" like ?" +
				" or "+QCM_B+" like ?" +
				" or "+QCM_C+" like ?" +
				" or "+QCM_D+" like ?" +
				" or "+QCM_E+" like ?" +
				" order by "+ANNEE+" desc",
				new String[]{"%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%", "%"+keyWord+"%"});
		while (cursor.moveToNext()) {//on commence à 1 parce que 0 c'est id et osef
			tmp.add(new Item(cursor.getString(1),	//Type
					cursor.getString(2), 	//Année
					cursor.getString(3), 	//Région
					cursor.getString(4), 	//Numéro
					cursor.getString(5), 	//Catégorie
					cursor.getString(6), 	//Question
					cursor.getString(7), 	//A
					cursor.getString(8), 	//B
					cursor.getString(9), 	//C
					cursor.getString(10), 	//D
					cursor.getString(11), 	//E
					cursor.getString(12)));	//Correction
		}

		cursor.close();
		return tmp;
	}

    /**
     * Methode de test de la base de données pour les mises à jour
     * @param session la session sous la forme anneeRegion
     * @return true/false
     */
    public boolean sessionExists(String session){
        String annee = session.substring(0,4);
        String region = session.length()>4?session.substring(4, 5):"";

        String[] columns = {KEY};
        String where = ANNEE + " = ? and "+ REGION + " = ?";
        String[] whereArgs = {annee, region};
        Cursor cursor = mDb.query(false, TABLE_NAME, columns, where, whereArgs, null, null, null, null);
        Methodes.alert(annee+region+" : exists="+(cursor.getCount()>0));
        return cursor.getCount()>0;
    }
}