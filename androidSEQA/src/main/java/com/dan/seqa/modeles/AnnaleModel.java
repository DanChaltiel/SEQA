package com.dan.seqa.modeles;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.dan.seqa.outils.Methodes;

@SuppressLint("UseSparseArrays")
public class AnnaleModel implements Parcelable {
	public enum ANNALE_TYPE {SESSION, THEME}
	
	public static final int QCS=0, QCM=1;

	private SparseArray<String> arrayUserInput; 	//réponses entrées
	private SparseArray<Double> arrayUserScoreList;	//scores correspondant
	private ArrayList<Item> annaleItemList;
	public String annee, region;
	public String theme;
	private boolean corrected = false;


	public AnnaleModel(){
		this.arrayUserInput = new SparseArray<String>();
		this.arrayUserScoreList = new SparseArray<Double>();	
	}

	@SuppressWarnings("unchecked")
	public AnnaleModel(Parcel in) {
		this.arrayUserInput = new SparseArray<String>();
		this.arrayUserScoreList = new SparseArray<Double>();	
		Bundle bundle = in.readBundle();
		HashMap<Integer, String> arrayUserInputMap = (HashMap<Integer, String>) bundle.getSerializable("arrayUserInputMap");
		for (int i = 1; i <= arrayUserInputMap.size(); i++) {
			arrayUserInput.put(i, arrayUserInputMap.get(i));
		}
		HashMap<Integer, Double> arrayUserScoreListMap = (HashMap<Integer, Double>) bundle.getSerializable("arrayUserScoreListMap");
		for (int i = 1; i <= arrayUserScoreListMap.size(); i++) {
			arrayUserScoreList.put(i, arrayUserScoreListMap.get(i));
		} 
		annaleItemList = in.readArrayList(getClass().getClassLoader());
		annee=in.readString();
		region=in.readString();
		theme=in.readString();
		this.corrected = in.readByte() != 0;
	}

	public void setUserInput(int pOrderNumeroQuestion, String userInput){
		this.arrayUserInput.put(pOrderNumeroQuestion, userInput);
		updateScoreList(pOrderNumeroQuestion, userInput);
	}
	public String getUserInput(int pOrderNumeroQuestion){
		return this.arrayUserInput.get(pOrderNumeroQuestion);
	}

	public int getTotalNumberAnswered() {//juste pour les themes
		return this.arrayUserInput.size();
	}
	
	public void setSession(AnnalesDAO dao, EditedAnnalesDAO editDao, String pAnnee, String pRegion, boolean randomize){
		if(annee==null) {//si c'est bien la première fois qu'on set la session
			this.annee=pAnnee;
			this.region=pRegion;
			this.annaleItemList = dao.selectBySession(annee, region);
			if(annaleItemList.size()!=60)
				Methodes.alert("Erreur, annale tronquée : "+annee+region+" à "+annaleItemList.size()+" éléments");
			if(randomize) {
				long seed = System.nanoTime();
				Collections.shuffle(annaleItemList, new Random(seed));
			}
			for (int i = 0; i < annaleItemList.size(); i++) {
				Item item = annaleItemList.get(i);		
				item.setOrderNumero(""+(i+1));
				if(item.isEdited(editDao)) {
					annaleItemList.set(i, editDao.selectItem(item));
				}					
			}
		}
	}

	public void setTheme(AnnalesDAO pDao, EditedAnnalesDAO editDao, String pTheme, boolean randomize){
		if(theme==null) {//si c'est bien la première fois qu'on set le theme
			this.theme=pTheme;
			this.annaleItemList = pDao.selectByTheme(theme);
			if(randomize) {
				long seed = System.nanoTime();
				Collections.shuffle(annaleItemList, new Random(seed));
			}
			for (int i = 0; i < annaleItemList.size(); i++) {
				Item item = annaleItemList.get(i);		
				item.setOrderNumero(""+(i+1));
				if(item.isEdited(editDao)) {
					annaleItemList.set(i, editDao.selectItem(item));
				}					
			}
		}
	}

	public ArrayList<Item> getItemlist(){
		return this.annaleItemList;
	}

	private void updateScoreList(int pOrderNumeroQuestion, String userInput) {
		Item item=null;
		for (Item lItem : annaleItemList) {
			if(lItem.getOrderNumero().equals(pOrderNumeroQuestion+"")) {
				item=lItem;
				break;
			}
		}
		if(item==null)
			Methodes.alert("Erreur : Item "+pOrderNumeroQuestion+" non trouvé, y'a un problème...");
		
		String correction = item.getCorrection();
		String QCSM = item.getType();

		if (QCSM.equals("QCS")){
			if(userInput.equals(correction))
				arrayUserScoreList.put(pOrderNumeroQuestion, 2.0);
			else
				arrayUserScoreList.put(pOrderNumeroQuestion, 0.0);
		} else if (QCSM.equals("QCM")){
			int nbErreurs = 0;
			char abcde[] = {'a','b','c','d','e'};
			for (char c : abcde) {
				if(userInput.matches(".*"+c+".*")!=correction.matches(".*"+c+".*"))
					nbErreurs++;
			}
			switch (nbErreurs) {
			case 0:	arrayUserScoreList.put(pOrderNumeroQuestion, 2.0);	break;
			case 1:	arrayUserScoreList.put(pOrderNumeroQuestion, 1.0);	break;
			case 2:	arrayUserScoreList.put(pOrderNumeroQuestion, 0.4);	break;
			default : arrayUserScoreList.put(pOrderNumeroQuestion, 0.0);	break;
			}
		}
		else
			Methodes.alert("Erreur : ni QCS ni QCM");
	}

	public Double getTotalScore() {
		Double score=0.0;
		for (int i = 0; i < arrayUserScoreList.size(); i++) {
			score+=arrayUserScoreList.get(arrayUserInput.keyAt(i));
		}
		return Methodes.arrondir(score, 1);
	}

	public Double getScore(int pNumeroQuestion) {
		return arrayUserScoreList.get(pNumeroQuestion);
	}

	//Getters, Setters
	public void setCorrected(boolean corrected) {this.corrected = corrected;}
	public boolean isCorrected() {return corrected;}
	
	@Override
	public String toString(){	
//		String text = "arrayUserInput:Array["+arrayUserInput.size()+"]:";
//		for(int i = 0; i < arrayUserInput.size(); i++) {
//			text+= arrayUserInput.get(arrayUserInput.keyAt(i));
//		}
		String text = "annaleItemList="+annaleItemList+"\n, ";
		text += "arrayUserInput="+arrayUserInput+"\n, ";
		text += "arrayUserScoreList="+arrayUserScoreList+"";
		return text;		
	}
	@Override
	public int describeContents() {return 0;}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
		HashMap<Integer, String> arrayUserInputMap = new HashMap<Integer, String>();
		for (int i = 0; i < arrayUserInput.size(); i++) {
			arrayUserInputMap.put(arrayUserInput.keyAt(i), arrayUserInput.get(arrayUserInput.keyAt(i)));
		}
		HashMap<Integer, Double> arrayUserScoreListMap = new HashMap<Integer, Double>();
		for (int i = 0; i < arrayUserScoreList.size(); i++) {
			arrayUserScoreListMap.put(arrayUserScoreList.keyAt(i), arrayUserScoreList.get(arrayUserScoreList.keyAt(i)));
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("arrayUserInputMap", arrayUserInputMap);
		bundle.putSerializable("arrayUserScoreListMap", arrayUserScoreListMap);
		dest.writeBundle(bundle);
		dest.writeList(annaleItemList);
		dest.writeString(annee);
		dest.writeString(region);
		dest.writeString(theme);
        dest.writeByte((byte) (corrected ? 1 : 0));
    }

	public static final Parcelable.Creator<AnnaleModel> CREATOR	= new Parcelable.Creator<AnnaleModel>() {
		@Override
		public AnnaleModel createFromParcel(Parcel in) {
			return new AnnaleModel(in);
		}

		@Override
		public AnnaleModel[] newArray(int size) {
			return new AnnaleModel[size];
		}
	};
}
