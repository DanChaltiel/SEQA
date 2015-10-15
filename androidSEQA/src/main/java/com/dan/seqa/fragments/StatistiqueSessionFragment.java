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
import com.dan.seqa.bdd.DatabaseHandler;
import com.dan.seqa.bdd.StatsDAO;
import com.dan.seqa.modeles.ScoreEtCompte;
import com.dan.seqa.modeles.Stat;
import com.dan.seqa.modeles.StatModel;

import java.util.ArrayList;
import java.util.List;

import com.dan.seqa.outils.Methodes;

public class StatistiqueSessionFragment extends Fragment{

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
		List<StatModel> rtnListe = new ArrayList<StatModel>();
		dao = new StatsDAO(getActivity());
		List<String> sessionsList = dao.getDifferentSessions();
		
		//on parcourt
		for(int i = 0 ; i < sessionsList.size() ; i++) {
			StatModel tmpModel;
			int essaisTotal = dao.getSpecificCount(DatabaseHandler.STATS_TABLE_NAME, sessionsList.get(i));
			if(essaisTotal!=0){
				Stat best=dao.getBestOfSession(sessionsList.get(i));
				final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
				String statsTimeChoice = preferences.getString("statsTimeChoice", "mois");
				ScoreEtCompte moyenne = dao.getMoyenne(sessionsList.get(i), statsTimeChoice);
				int total = (int) best.getScoreTotal();
				float rating = (float) moyenne.ratio*6;//pour 6 étoiles
				
				tmpModel = new StatModel(
						sessionsList.get(i), 
						best.getFormattedScore(), 
						Methodes.arrondir(moyenne.ratio*total,1)+"/"+total,
						essaisTotal,
						moyenne.nombreEssais,
						rating);
				
				if (i==1)
					Methodes.alert(tmpModel.toString());
			}
			else {
				tmpModel = new StatModel(sessionsList.get(i));
			}


			rtnListe.add(tmpModel);
		}
		dao.close();
		return rtnListe;	
	}

	
	public static StatistiqueSessionFragment newInstance() {	
		return new StatistiqueSessionFragment();
	}
}
