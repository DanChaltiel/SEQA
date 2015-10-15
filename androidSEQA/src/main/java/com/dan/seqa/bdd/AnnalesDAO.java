package com.dan.seqa.bdd;

import android.content.Context;
import android.database.Cursor;

import com.dan.seqa.modeles.Item;

import java.util.ArrayList;

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
	 * @param item l'item à ajouter à la base
	 */
//	public void ajouter(Item item) {
//		ContentValues value = new ContentValues();
//		value.put(TYPE, item.getType());
//		value.put(ANNEE, item.getAnnee());
//		value.put(REGION, item.getRegion());
//		value.put(NUMERO, item.getNumero());
//		value.put(CATEGORIE, item.getCategorie());
//		value.put(QUESTION, item.getQuestion());
//		value.put(QCM_A, item.getQcmA());
//		value.put(QCM_B, item.getQcmB());
//		value.put(QCM_C, item.getQcmC());
//		value.put(QCM_D, item.getQcmD());
//		value.put(QCM_E, item.getQcmE());
//		value.put(CORRECTION, item.getCorrection());
//		mDb.insert(TABLE_NAME, null, value);
//	}

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
//		Methodes.alert("selectBySession() fait en : "+(System.currentTimeMillis()-debut)+"ms");
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

	public Item selectItem(Item normalItem){
		Cursor cursor = mDb.query(
				TABLE_NAME, 
				null,
				ANNEE+"=? and "+REGION+"=? and "+NUMERO+"=?", 
				new String[] {normalItem.getAnnee(),normalItem.getRegion(),normalItem.getNumero()}, 
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

    public Item selectItem(String pAnnee, String pRegion, String pNumero){
        Item dummy = new Item(pAnnee, pRegion, pNumero, null, null, null, null, null, null, null, null, null);
        return selectItem(dummy);
    }

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
}