package com.dan.seqa.activity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dan.newseqa.BuildConfig;
import com.dan.newseqa.R;
import com.dan.seqa.activity.annales.AnnaleSessionActivity;
import com.dan.seqa.activity.annales.AnnaleThemeActivity;
import com.dan.seqa.activity.donnees.BrowseEditionsActivity;
import com.dan.seqa.activity.donnees.CountdownActivity;
import com.dan.seqa.activity.donnees.HoroscopeActivity;
import com.dan.seqa.activity.donnees.ModeEmploiActivity;
import com.dan.seqa.activity.donnees.RechercheActivity;
import com.dan.seqa.activity.stats.StatistiquesActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccueilActivity extends AbstractActivity{

	//CONSTANTES GLOBALES
	public static final boolean MODE_ADMIN=false;
	public static final boolean ERASE_STATS=false;

	private static final String KEY = "key";
	//VARIABLES 
	private static Map<String, List<Bouton>> map = new HashMap<String, List<Bouton>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accueil);
//
//		Intent secondeActivite = new Intent(this, ModeEmploiActivity.class);
//		startActivity(secondeActivite);
		
		if (findViewById(R.id.frame) != null) {
			if (savedInstanceState != null) {
				return;//sinon on empilerait des fragments...
			}
			//
			List<Bouton> buttons = new ArrayList<Bouton>();
//			buttons.add(new Bouton("Test", ModeEmploiActivity.class));
			buttons.add(new Bouton("Annales par Session", AnnaleSessionActivity.class));
			buttons.add(new Bouton("Questions par Thème", AnnaleThemeActivity.class));
			buttons.add(new Bouton("Scores", StatistiquesActivity.class));
			buttons.add(new Bouton("Donnees", "Data"));
			buttons.add(new Bouton("Paramètres", PrefActivity.class));
			map.put("Accueil", buttons);
			//
			List<Bouton> buttons2 = new ArrayList<Bouton>();
			buttons2.add(new Bouton("Recherche", RechercheActivity.class));
			buttons2.add(new Bouton("Horoscope", HoroscopeActivity.class));
			buttons2.add(new Bouton("The Final Countdown", CountdownActivity.class));
			buttons2.add(new Bouton("Questions éditées", BrowseEditionsActivity.class));
			buttons2.add(new Bouton("Mode d'emploi", ModeEmploiActivity.class));
			map.put("Data", buttons2);

			//Mise en place de la View
			AccueilFragment firstFragment = AccueilFragment.newInstance("Accueil");
			getSupportFragmentManager().beginTransaction().add(R.id.frame, firstFragment).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true; //on enlève carrément le menu de l'actionbar
	}

	protected class Bouton{
		String nom;
		String next;
		Class<?> classe;			
		public Bouton(String nom, Class<?> classe) {
			this.nom=nom;
			this.classe=classe;
		}
		public Bouton(String nom, String next) {
			this.nom=nom;
			this.next=next;
		}
	}

	public static class AccueilFragment extends Fragment{

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
			View tmpView = inflater.inflate(R.layout.fragment_accueil,container, false);
			LinearLayout layout = (LinearLayout) tmpView.findViewById(R.id.accueil_container);

			LinearLayout.LayoutParams buttonLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			int fiveDp = dpToPixels(5);
			buttonLP.setMargins(0, fiveDp, 0, 0);

			Bundle args = getArguments();
			String name = args.getString(KEY);
			List<Bouton> buttons = map.get(name);
			for (final Bouton bouton : buttons) {
				Button tmp = new Button(getActivity());
				tmp.setLayoutParams(buttonLP);
				tmp.setMinimumHeight(1);
                tmp.setMinHeight(1);
//                tmp.setPadding(0,fiveDp,0,fiveDp);
                tmp.setText(bouton.nom);
                if(bouton.classe!=null)
                    tmp.setOnClickListener(new DirectLauncher(bouton.classe));
				else
					tmp.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							AccueilFragment nextFragment = AccueilFragment.newInstance(bouton.next);
							FragmentTransaction ft = getFragmentManager().beginTransaction();
							ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
							ft.replace(R.id.frame, nextFragment);
							ft.addToBackStack(null);
							ft.commit();
						}
					});
				layout.addView(tmp);
			}
			return tmpView;
		}

		public static AccueilFragment newInstance(String name) {			
			AccueilFragment fragment = new AccueilFragment();
			Bundle args = new Bundle();
			args.putString(KEY, name);
			fragment.setArguments(args);
			return fragment;
		}

		private int dpToPixels(int dp) {
			final float scale = getResources().getDisplayMetrics().density;
			return (int) (dp * scale + 0.5f);
		}	

		private class DirectLauncher implements OnClickListener{		
			Class<?> targetClass;		
			Bundle bundle;		
			private DirectLauncher(Class<?> className){
				targetClass = className;			
			}	

			private DirectLauncher(Class<?> className, Bundle extras){
				targetClass = className;	
				bundle=extras;
			}		
			@Override
			public void onClick(View v) {
				Intent secondeActivite = new Intent(getActivity(), targetClass);
				if(bundle!=null)
					secondeActivite.putExtras(bundle);
				startActivity(secondeActivite);	
			}
		}
	}
}