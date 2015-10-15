package com.dan.seqa.bdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dan.seqa.modeles.Item;

import java.util.ArrayList;

public class EditedAnnalesDAO extends AbstractDAO{


	public static final String
			KEY = "id", TYPE = "type",
			ANNEE = "annee", REGION = "region", NUMERO = "numero",
			QUESTION = "question", CATEGORIE = "categorie",
			QCM_A = "qcmA", QCM_B = "qcmB", QCM_C = "qcmC", QCM_D = "qcmD", QCM_E = "qcmE",
			CORRECTION = "correction",
			COMMENTAIRE = "commentaire";
	public static final String TABLE_NAME = "EditedAnnales";

	public EditedAnnalesDAO(Context pContext) {
		super(pContext);
		super.mDb = open();
	}

	public long modifierOuAjouter(Item item) {
		ContentValues value = encryptItem(item);
		Cursor cursor = mDb.query(
				TABLE_NAME,
				new String[] {KEY},
				ANNEE+"=? and "+REGION+"=? and "+NUMERO+"=?",
				new String[] {item.getAnnee(),item.getRegion(),item.getNumero()},
				null, null, null);
		int count = cursor.getCount();
		cursor.close();
		if(count==0)
			return mDb.insert(TABLE_NAME, null, value);
		else
			return mDb.update(TABLE_NAME,
					value,
					ANNEE+"=? and "+REGION+"=? and "+NUMERO+"=?",
					new String[] {item.getAnnee(),item.getRegion(),item.getNumero()}
            );
	}

    public long retirer(Item item) {
        return mDb.delete(TABLE_NAME,
                ANNEE+"=? and "+REGION+"=? and "+NUMERO+"=?",
                new String[] {item.getAnnee(),item.getRegion(),item.getNumero()}
        );
    }

	public ArrayList<Item> selectAll() {
		//on gère les items sans session simplement avec pRegion=""
		ArrayList<Item> tmp = new ArrayList<>();
		Cursor cursor = mDb.query(
				TABLE_NAME,
				null,null, null, null, null, null);
		while (cursor.moveToNext()) {//on commence à 1 parce que 0 c'est id et osef
			tmp.add(buildItem(cursor));
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
		Item rtn = buildItem(cursor);
		cursor.close();
		return rtn;
	}

	private ContentValues encryptItem(Item item){
        ContentValues value = new ContentValues();
        value.put(AnnalesDAO.TYPE, item.getType());
        value.put(ANNEE, item.getAnnee());
        value.put(REGION, item.getRegion());
        value.put(NUMERO, item.getNumero());
        value.put(CATEGORIE, item.getCategorie());
        value.put(QUESTION, item.getQuestion());
        value.put(QCM_A, item.getQcmA());
        value.put(QCM_B, item.getQcmB());
        value.put(QCM_C, item.getQcmC());
        value.put(QCM_D, item.getQcmD());
        value.put(QCM_E, item.getQcmE());
        value.put(CORRECTION, item.getCorrection());
        value.put(COMMENTAIRE, item.getCommentaire());
        return value;
    }

	private Item buildItem(Cursor cursor){
		return new Item(cursor.getString(1),	//Type
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
				cursor.getString(12),	//Correction
				cursor.getString(13));	//Commentaire
	}
}