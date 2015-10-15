package com.dan.seqa.modeles;

public class StatModel{
	
	public String session; 
	public String meilleurScore;
	public String moyenneSemaine;
	public String scoreMax;
	public int nombreEssaisSemaine;
	public int nombreEssaisTotal;
	public float rating;
	
	/**
	 * 
	 * @param pSession
	 * @param pMeilleurScore
	 * @param pMoyenneSemaine
	 * @param pNombreEssaisTotal
	 * @param pNombreEssaisSemaine
	 * @param pRating
	 */
	public StatModel(String pSession, String pMeilleurScore, String pMoyenneSemaine, 
					int pNombreEssaisTotal, int pNombreEssaisSemaine, float pRating){
		session=pSession;
		meilleurScore=pMeilleurScore;
		moyenneSemaine=pMoyenneSemaine;
		rating=pRating;
		nombreEssaisTotal=pNombreEssaisTotal;
		nombreEssaisSemaine=pNombreEssaisSemaine;
	}

	public StatModel(String pSession) {
		session=pSession;}

	@Override
	public String toString() {
		return "session = "+session+"\n"
				+ "meilleurScore = "+meilleurScore+"\n"
				+ "moyenneSemaine = "+moyenneSemaine+"\n"
				+ "rating = "+rating+"\n"
				+ "nombreEssaisTotal = "+ nombreEssaisTotal+"\n"
				+ "nombreEssaisSemaine = "+nombreEssaisSemaine;
	}
}
