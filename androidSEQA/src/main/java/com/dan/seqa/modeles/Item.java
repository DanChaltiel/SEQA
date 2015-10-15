package com.dan.seqa.modeles;

import android.os.Parcel;
import android.os.Parcelable;

import com.dan.seqa.bdd.EditedAnnalesDAO;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * QCM (ou QCS), avec sa catégorie, sa question, ses 5 items et sa correction
 * Un commentaire peut lui être ajouté par édition
 */
public class Item implements Parcelable {
	
	public final static String QCM = "QCM";
	public final static String QCS = "QCS";
	
	//private int item;
	private String 	annee, region, numero,
					categorie, question, 
					qcmA, qcmB, qcmC, qcmD, qcmE,  
					type, correction,
					commentaire="";
	/**Le numéro d'ordre quand les items sont sélectionnés par thème*/
	private String orderNumero;
	
	/** Un item d'annales de QCM */
	public Item(String pType, String pAnnee, String pRegion, String pNumero, String pCategorie, String pQuestion, 
				String pQcmA, String pQcmB, String pQcmC, String pQcmD, String pQcmE, String pCorrection){
		//this.item=pItem; //dans les paramètres : 		int pItem, 
		this.type=pType;
		this.annee=pAnnee;
		this.region=pRegion;
		this.numero=pNumero;
		this.orderNumero=pNumero;
		this.categorie=pCategorie;
		this.question=pQuestion;
		this.qcmA=pQcmA;
		this.qcmB=pQcmB;
		this.qcmC=pQcmC;
		this.qcmD=pQcmD;
		this.qcmE=pQcmE;
		this.correction=pCorrection;
	}	
	/** Un item d'annales de QCM */
	public Item(String pType, String pAnnee, String pRegion, String pNumero, String pCategorie, String pQuestion, 
				String pQcmA, String pQcmB, String pQcmC, String pQcmD, String pQcmE, String pCorrection,
				String pCommentaire){
		//this.item=pItem; //dans les paramètres : 		int pItem, 
		this.type=pType;
		this.annee=pAnnee;
		this.region=pRegion;
		this.numero=pNumero;
		this.orderNumero=pNumero;
		this.categorie=pCategorie;
		this.question=pQuestion;
		this.qcmA=pQcmA;
		this.qcmB=pQcmB;
		this.qcmC=pQcmC;
		this.qcmD=pQcmD;
		this.qcmE=pQcmE;
		this.correction=pCorrection;
		this.commentaire=pCommentaire;
	}
		
	private Item(Parcel in) {
		this.type=in.readString();
		this.annee=in.readString();
		this.region=in.readString();
		this.numero=in.readString();
		this.orderNumero=in.readString();
		this.categorie=in.readString();
		this.question=in.readString();
		this.qcmA=in.readString();
		this.qcmB=in.readString();
		this.qcmC=in.readString();
		this.qcmD=in.readString();
		this.qcmE=in.readString();
		this.correction=in.readString();
		this.commentaire=in.readString();
	}

	public Item(Item another) {
		this.type=another.getType();
		this.annee=another.getAnnee();
		this.region=another.getRegion();
		this.numero=another.getNumero();
		this.orderNumero=another.getOrderNumero();
		this.categorie=another.getCategorie();
		this.question=another.getQuestion();
		this.qcmA=another.getQcmA();
		this.qcmB=another.getQcmB();
		this.qcmC=another.getQcmC();
		this.qcmD=another.getQcmD();
		this.qcmE=another.getQcmE();
		this.correction=another.getCorrection();
		this.commentaire=another.getCommentaire();
	}


	public Map<String, String> getMap() {
		HashMap<String, String> tmp = new HashMap<String, String>();
		tmp.put(EditedAnnalesDAO.CATEGORIE, categorie);
		tmp.put(EditedAnnalesDAO.QUESTION, question);
		tmp.put(EditedAnnalesDAO.QCM_A, qcmA);
		tmp.put(EditedAnnalesDAO.QCM_B, qcmB);
		tmp.put(EditedAnnalesDAO.QCM_C, qcmC);
		tmp.put(EditedAnnalesDAO.QCM_D, qcmD);
		tmp.put(EditedAnnalesDAO.QCM_E, qcmE);
		tmp.put(EditedAnnalesDAO.CORRECTION, correction);
		tmp.put(EditedAnnalesDAO.COMMENTAIRE, commentaire);
		return tmp;
	}
	
	@Override
	public String toString(){
		return this.annee+"\t" +
				this.region+"\t" +
				this.numero+"\t" +
				this.categorie+"\t" +
				this.question+"\n" +
				this.qcmA+"\t" +
				this.qcmB+"\t" +
				this.qcmC+"\t" +
				this.qcmD+"\t" +
				this.qcmE+"\t" +
				this.correction+"\t" +
				this.commentaire+
				"";
	}

	/*
	 * Getters et Setters
	 */
	/**"QCM" ou "QCS"*/
	public String getType() {return type;}
	public String getAnnee() {return annee;}
	public String getRegion() {return region;}
	public String getNumero() {return numero;}
	/**Le numéro d'ordre quand les items sont sélectionnés par thème*/
	public String getOrderNumero() {return orderNumero;}
	public String getCategorie() {return capitalize(categorie);}
	public String getQuestion() {return capitalize(question);}
	public String getQcmA() {return capitalize(qcmA);}
	public String getQcmB() {return capitalize(qcmB);}
	public String getQcmC() {return capitalize(qcmC);}
	public String getQcmD() {return capitalize(qcmD);}
	public String getQcmE() {return capitalize(qcmE);}
	public String getCorrection() {return correction.toLowerCase(Locale.getDefault());}
	public String getCommentaire() {return commentaire;}
	/** @return annee+session+[numero]	*/
	public String getSession() {return annee+region+"["+numero+"]";}
	public String[] getQcmArray() {return new String[]{qcmA, qcmB, qcmC, qcmD, qcmE};}
	
	public void setOrderNumero(String newOrderNumero) {this.orderNumero=newOrderNumero;}
	
	public void setCategorie(String categorie) {this.categorie = categorie;}
	public void setQuestion(String question) {this.question = question;}
	public void setQcmA(String qcmA) {this.qcmA = qcmA;}
	public void setQcmB(String qcmB) {this.qcmB = qcmB;}
	public void setQcmC(String qcmC) {this.qcmC = qcmC;}
	public void setQcmD(String qcmD) {this.qcmD = qcmD;}
	public void setQcmE(String qcmE) {this.qcmE = qcmE;}
	public void setType(String type) {this.type = type;}
	public void setCorrection(String correction) {this.correction = correction.toLowerCase(Locale.getDefault());}
	public void setCommentaire(String commentaire) {this.commentaire = commentaire;}
	
	public boolean isEdited(EditedAnnalesDAO dao) {
		return dao.selectItem(this)!=null;		
	}
	
	private static String capitalize(String input) {
		if(input!=null)
			return Character.toUpperCase(input.charAt(0)) + input.substring(1);
		else 
			return null;
	}

    public boolean equals(Item other) {
        return
            this == other || other != null &&
            annee.equals(other.annee) &&
            categorie.equals(other.categorie) &&
            question.equals(other.question) &&
            region.equals(other.region) &&
            type.equals(other.type) &&
            numero.equals(other.numero) &&
            qcmA.equals(other.qcmA) &&
            qcmB.equals(other.qcmB) &&
            qcmC.equals(other.qcmC) &&
            qcmD.equals(other.qcmD) &&
            qcmE.equals(other.qcmE) &&
            correction.equals(other.correction) &&
            commentaire.equals(other.commentaire) &&
            !(commentaire == null || !commentaire.equals(other.commentaire));
    }

    @Override
	public int describeContents() {    
		//On renvoie 0, car notre classe ne contient pas de FileDescriptor
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {		
		dest.writeString(type);
		dest.writeString(annee);
		dest.writeString(region);
		dest.writeString(numero);
		dest.writeString(orderNumero);
		dest.writeString(categorie);
		dest.writeString(question);
		dest.writeString(qcmA);
		dest.writeString(qcmB);
		dest.writeString(qcmC);
		dest.writeString(qcmD);
		dest.writeString(qcmE);
		dest.writeString(correction);
		dest.writeString(commentaire);
	}
	
	public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
		@Override
		public Item createFromParcel(Parcel in) {
			return new Item(in);
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

}
