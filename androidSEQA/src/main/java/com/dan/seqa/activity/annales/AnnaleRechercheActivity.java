package com.dan.seqa.activity.annales;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.modeles.AnnaleModel;

public class AnnaleRechercheActivity extends AbstractAnnaleActivity{
	//CONSTANTES
//	private final int LIST_FRAGMENT_ID=1337;//pour la fonction recherche
	private int totalFrag;
	private TextView positionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		//On instancie les variables et les listeners
		positionView = (TextView)  findViewById(R.id.timer);
		//on gère le modèle et le bundle
		Bundle extraBundle = getIntent().getExtras();
		if(savedInstanceState==null) {
			if(extraBundle!=null && !extraBundle.isEmpty()){
				String theme = extraBundle.getString("theme");
				this.model=extraBundle.getParcelable("model");
				choixThemeOK(theme);
			}  else {
				this.model=new AnnaleModel();
				montrerDialogueChoixTheme();
			}
		} else {
			this.model = savedInstanceState.getParcelable("MODEL");
			int gCurrent = savedInstanceState.getInt("CURRENT");
			if(model.theme==null)
				montrerDialogueChoixTheme();	
			else {
				choixThemeOK(model.theme);//ça va créer pager
				pager.setCurrentItem(gCurrent);
			}
		}
	}

	protected void createPager(){
		// Création de l'adapter qui s'occupera de l'affichage de la liste des fragments et de l'affectation au ViewPager
		mPagerAdapter = new AnnalePagerAdapter(getSupportFragmentManager(), model);
		totalFrag=mPagerAdapter.getCount();
		pager = (ViewPager) super.findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) { //position va de 0 à x-1 ?
				currentFrag=position;
				seeker.setProgress(position);
				positionView.setText((position+1)+"/"+totalFrag);//on commence à 0
				goPreviousButton.setEnabled(position==0?false:true);
				goNextButton.setEnabled(position==totalFrag?false:true);
			}
		});
		pager.setAdapter(this.mPagerAdapter);

		positionView.setText("1/"+totalFrag);
		seeker.setMax(mPagerAdapter.getCount()-1);//on commence à 0 donc on s'arrete à n-1
	}
	
	//Gestion de la dialogbox (modale)
	private void montrerDialogueChoixTheme() {
//		DialogFragment newFragment = ChoixThemeDialogFragment.newInstance("Choisis une session", this);
//		newFragment.setCancelable(false);
//		newFragment.show(getSupportFragmentManager(), "dialog");
	}
	
	public void choixThemeOK(String pSelectedTheme){
		//On met à jour le modèle
		AnnalesDAO annaleDao=new AnnalesDAO(this);
		EditedAnnalesDAO editDao=new EditedAnnalesDAO(this);
//		annaleDao.test();
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean randomize  = preferences.getBoolean("randomize", false);
		model.setTheme(annaleDao, editDao, pSelectedTheme, randomize); 
		//on crée le pager avec les fragments d'Annale
		createPager();
		//on crée la searchList ou les searchboutons selon l'orientation
		creerVerticalSearchList();		
		creerHorizontalSearchList();
	}


	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Retour")
		.setMessage("Tu es sur d'avoir fini ?")
		.setPositiveButton("Oui", new DialogInterface.OnClickListener()   {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		})
		.setNegativeButton("Non", null)
		.show();
	}

	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putParcelable("MODEL", model);
		outState.putInt("CURRENT", currentFrag);
	}

	/**Méthode d'interface au clic sur un item*/
	@Override
	public void updateViewOnFragmentChange() {
		if(searchList!=null)
			searchList.setOnScrollListener(new SearchViewListener());
		else if(searchButtonsLayout!=null) {
			creerHorizontalSearchList();
		}
		if(model.isCorrected())
			correction();
	}

	private class SearchViewListener implements OnScrollListener{
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			for (int i = 0; i <= view.getLastVisiblePosition() - view.getFirstVisiblePosition(); i++) {
				view.getChildAt(i).setBackgroundColor(getListItemColor(firstVisibleItem+i));
			}
		}	
	}
}

