package com.dan.seqa.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dan.seqa.fragments.QCMFragment;
import com.dan.seqa.fragments.QCSFragment;
import com.dan.seqa.fragments.QCVideFragment;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.modeles.Item;
import com.dan.seqa.modeles.QuestionFragmentsInterface;

import java.util.ArrayList;
import java.util.List;

import com.dan.seqa.outils.Methodes;

public class AnnalePagerAdapter extends FragmentPagerAdapter {

	public final static String KEY_MODEL="KEY_MODEL";
	public final static String KEY_ITEM="KEY_ITEM";
	
	private final List<QuestionFragmentsInterface> fragments;
	private int questionsNonVides=0;
	
	/**
	 * Constructeur - Adaptateur qui organise les fragments à faire défiler en les appelant depuis la BDD <br/>
	 * Sélectionne les 60 fragments d'une session
	 * @param pModel Le modèle à prendre en compte
	 * @return La liste des fragments (implémentant l'interface)
	 * @author Dan Chaltiel
	 */
	public AnnalePagerAdapter(FragmentManager fm, AnnaleModel pModel) {
		super(fm);
		List<QuestionFragmentsInterface> tmpFragments = new ArrayList<QuestionFragmentsInterface>();
		ArrayList<Item>myItemList = pModel.getItemlist();
		
		// Ajout les Fragments de Question dans la liste
		for (int i = 0; i < myItemList.size(); i++) {
			Item item = myItemList.get(i);		
//			item.setOrderNumero(""+(i+1));
			if (item.getQuestion()==null) //que ce soit un QCS ou un QCM
				tmpFragments.add(QCVideFragment.newInstance(item));	
			else if (item.getType().equals("QCS")){
				tmpFragments.add(QCSFragment.newInstance(item, pModel));	
				questionsNonVides++;
			}
			else if (item.getType().equals("QCM")){
				tmpFragments.add(QCMFragment.newInstance(item, pModel));
				questionsNonVides++;
			}
			else
				Methodes.alert("Erreur, fragment de type non reconnu : "+item.getType());		
		}
		this.fragments = tmpFragments;
	}
	
	public int getQuestionsNonVides() {return questionsNonVides;}

	public boolean fragmentExists(int position){
		try{
			this.fragments.get(position);
			return true;
		}catch(IndexOutOfBoundsException e){
			return false;
		}
	}
	
	@Override
	public Fragment getItem(int position) {
		try {
			return (Fragment) this.fragments.get(position);			
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}
}