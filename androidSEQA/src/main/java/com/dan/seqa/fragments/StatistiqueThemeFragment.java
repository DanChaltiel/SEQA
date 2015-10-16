package com.dan.seqa.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dan.newseqa.R;
import com.dan.seqa.activity.stats.StatistiquesDetailActivity;
import com.dan.seqa.adapters.StatsListAdapter;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.DatabaseHandler;
import com.dan.seqa.bdd.StatsDAO;
import com.dan.seqa.modeles.ScoreEtCompte;
import com.dan.seqa.modeles.Stat;
import com.dan.seqa.modeles.StatModel;

import java.util.ArrayList;
import java.util.List;

import com.dan.seqa.outils.Methodes;

public class StatistiqueThemeFragment extends Fragment{

	public final static String EXTRA_NOM_SESSION = "com.dan.seqa.nomSession";
	private ListView vue;
	private StatsDAO dao;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View tmpView = inflater.inflate(R.layout.statistiques_simple_list,container, false);

		vue = (ListView) tmpView.findViewById(R.id.listStats); 
		
		final StatsListAdapter adapter = new StatsListAdapter(this.getActivity(), getList()); 
		
		vue.setAdapter(adapter);
		vue.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String sessionName = adapter.getNomSession(position);
				Intent detailActivite = new Intent(getActivity(), StatistiquesDetailActivity.class);
				detailActivite.putExtra(EXTRA_NOM_SESSION, sessionName);
				startActivity(detailActivite);
			}
		});
		return tmpView;
	}

	private List<StatModel> getList(){
		List<StatModel> rtnListe = new ArrayList<>();
		dao = new StatsDAO(getActivity());
		List<String> themesList = dao.getDifferent(AnnalesDAO.CATEGORIE);
		
		//on parcourt
		for(int i = 0 ; i < themesList.size() ; i++) {
			StatModel tmpModel;
			int essaisTotal = dao.getSpecificCount(StatsDAO.TABLE_NAME, themesList.get(i));
			if(essaisTotal!=0){
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String statsTimeChoice = preferences.getString("statsTimeChoice", "mois");
				Stat best=dao.getBestOfSession(themesList.get(i));
				ScoreEtCompte moyenne = dao.getMoyenne(themesList.get(i), statsTimeChoice);
				int total = (int) best.getScoreTotal();
				float rating = (float) moyenne.ratio*6;//pour 6 étoiles
				
				tmpModel = new StatModel(
						themesList.get(i), 
						best.getFormattedScore(), 
						Methodes.arrondir(moyenne.ratio*total,1)+"/"+total,
						essaisTotal, moyenne.nombreEssais, rating);
			}
			else {
				tmpModel = new StatModel(themesList.get(i));
			}


			rtnListe.add(tmpModel);
		}
		dao.close();
		return rtnListe;	
	}

	public static StatistiqueThemeFragment newInstance() {	
		return new StatistiqueThemeFragment();
	}
}
