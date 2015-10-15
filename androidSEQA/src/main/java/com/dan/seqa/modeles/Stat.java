package com.dan.seqa.modeles;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.dan.seqa.outils.Methodes;


public class Stat implements Parcelable {
	
	//private int item;
	private String 	session;
	private double scoreRatio, scoreTotal;
	private long timestamp;
	
	/**
	 * 
	 * @param pSession ou pTheme
	 * @param pDate
	 * @param pScoreRatio
	 * @param pScoreTotal
	 */
	public Stat(String pSession, long pDate, double pScoreRatio, double pScoreTotal){
		//this.item=pItem; //dans les paramètres : 		int pItem, 
		this.session=pSession;
		this.timestamp=pDate;
		this.scoreRatio=pScoreRatio;
		this.scoreTotal=pScoreTotal;
	}	
		
	@Override
	public String toString(){
		String output = //this.item+"\t" +//getint, getstring ??
						this.session+"\t" +
						this.timestamp+"\t" +
						this.scoreRatio*this.scoreTotal+"/" +
						this.scoreTotal+"\t" +
		"";
		return output;
	}
	
	/*
	 * Getters et Setters
	 */
	public long getTimestamp() {return timestamp;}
	public String getSession() {return session;}
	public double getScoreRatio() {return scoreRatio;}
	public double getScoreTotal() {return scoreTotal;}
	public int getNote() {return (int) (scoreRatio*scoreTotal);}

	public String getFormattedScore() {return Methodes.arrondir(scoreRatio*scoreTotal, 1)+"/" +(int)scoreTotal;}
	public String getFormattedDate(){
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(this.timestamp);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);	
		return sdf.format(cal.getTime());
	}
	
	@Override
	public int describeContents() {    
		//On renvoie 0, car notre classe ne contient pas de FileDescriptor
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO prout prout writeToParcel...		
	}
}
