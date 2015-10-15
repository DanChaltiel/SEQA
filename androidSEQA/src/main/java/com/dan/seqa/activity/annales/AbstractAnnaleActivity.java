package com.dan.seqa.activity.annales;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.StatsDAO;
import com.dan.seqa.modeles.AnnaleInterface;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.modeles.Item;
import com.dan.seqa.modeles.QuestionFragmentsInterface;
import com.dan.seqa.modeles.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dan.seqa.outils.Methodes;

public abstract class AbstractAnnaleActivity extends AbstractActivity implements AnnaleInterface{
	//CONSTANTES
	protected final int LIST_FRAGMENT_ID=1337;//pour la fonction recherche
	//VARIABLES
	protected AnnaleModel model;
	protected int currentFrag=0; //normalement va de 1 à 60, (0=erreur)
	//VUES
	protected Button goNextButton, goPreviousButton;
	protected LinearLayout searchButtonsLayout;
	protected ListView searchList;
	protected ViewPager pager;
	protected AnnalePagerAdapter mPagerAdapter;
	protected SeekBar seeker;
	//OBJETS
	protected StatsDAO statDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_annales);
		//On instancie les variables et les listeners
		seeker = (SeekBar) findViewById(R.id.seeker);		 
		seeker.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				if(mPagerAdapter.fragmentExists(progress))
					pager.setCurrentItem(progress);
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});
		goNextButton = (Button) findViewById(R.id.goNextButton);
		goNextButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mPagerAdapter.fragmentExists(currentFrag+1))
					pager.setCurrentItem(currentFrag+1);
			}
		});
		goPreviousButton = (Button) findViewById(R.id.goPreviousButton);
		goPreviousButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(mPagerAdapter.fragmentExists(currentFrag-1))
					pager.setCurrentItem(currentFrag-1);
			}
		});
		//l'un des 2 est null
		int orientation = getResources().getConfiguration().orientation;
		if(orientation==Configuration.ORIENTATION_LANDSCAPE)
			searchList = (ListView) findViewById(R.id.searchList);		
		else if (orientation==Configuration.ORIENTATION_PORTRAIT) {
			searchButtonsLayout = (LinearLayout) findViewById(R.id.listlayout);	
		} else
			try {throw new Exception("Configuration d'écran non reconnue : "+orientation);} catch (Exception e) {e.printStackTrace();}
	}

	protected void creerVerticalSearchList() {
		if(searchList!=null && (model.annee!=null || model.theme!=null)) {
			final int MAX_WIDTH=300;
			List<HashMap<String, String>> tmpListe = new ArrayList<>();
			AnnalesDAO itemDao = new AnnalesDAO(this);
			ArrayList<Item>myItemList = model.getItemlist();
			itemDao.close();
			for (Item item : myItemList) {
				HashMap<String, String> element = new HashMap<>();
				element.put("numero", item.getType()+" n°"+item.getNumero());
				element.put("categorie", item.getCategorie());	
				tmpListe.add(element);		
			}
			BaseAdapter adapter = new SimpleAdapter(this,  
					tmpListe, 
					R.layout.liste_simple,
					new String[] {"numero", "categorie"}, //clés entrée dans la tmpListe
					new int[] {android.R.id.text1, android.R.id.text2 }); //colorLayout par défaut
			searchList.setAdapter(adapter);
			searchList.getLayoutParams().width = (int) (Methodes.getWidestView(this, adapter)*1.05);
			if (searchList.getLayoutParams().width>MAX_WIDTH)
				searchList.getLayoutParams().width=MAX_WIDTH;
			searchList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(mPagerAdapter.fragmentExists(position))
						pager.setCurrentItem(position);
				}
			});
			searchList.setOnScrollListener(new SearchViewListener());
		}
		
	}
	
	protected void creerHorizontalSearchList() {
		if(searchButtonsLayout!=null && (model.annee!=null || model.theme!=null)) {
			searchButtonsLayout.removeAllViews();
			ArrayList<Item>myItemList = model.getItemlist();
			Button searchButtonArray[] = new Button[myItemList.size()];
			int count=0;
			for (Item item : myItemList) {
				int i = Integer.valueOf(item.getOrderNumero())-1;
				searchButtonArray[i]=new Button(this); 
				searchButtonArray[i].setTextSize(12);
				String zero = Integer.valueOf(item.getNumero())<10?"0":"";
				searchButtonArray[i].setText(zero+item.getNumero());
				searchButtonArray[i].setBackgroundColor(getListItemColor(i));
				final int position=count;
				searchButtonArray[i].setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						if(mPagerAdapter.fragmentExists(position))
							pager.setCurrentItem(position);
					}
				});
				searchButtonsLayout.addView(searchButtonArray[i]);	
				count++;
			}
		}
	}

	protected int getListItemColor(int itemNumber){
		int couleur = Color.WHITE;
		if(!model.isCorrected()) {
			if(model.getUserInput(itemNumber+1)!=null && !model.getUserInput(itemNumber+1).equals("")) 
				couleur = Color.GRAY;
			else
				couleur = Color.WHITE;
		} else if(model.getScore(itemNumber+1)!=null) {
			if(model.getScore(itemNumber+1)==2.0)
				couleur = Color.GREEN;
			else if(model.getScore(itemNumber+1)==1.0)
				couleur = Color.YELLOW;
			else if(model.getScore(itemNumber+1)==0.4)
				couleur = Color.argb(255, 255, 130, 40);
			else if(model.getScore(itemNumber+1)==0.0)
				couleur = Color.RED;
		}
		return couleur;
	}
		
	protected void correction(){
		if(!model.isCorrected()){ 
			boolean isSession = model.theme==null || model.theme.equals("");//sinon c'est isTheme
			TextView scoreView = (TextView)  findViewById(R.id.score);
			int totalQuestions;
			if(isSession)
				totalQuestions = mPagerAdapter.getQuestionsNonVides()*2;
			else //isTheme --> on prend pas les questions non vides mais les questions répondues
				totalQuestions = model.getTotalNumberAnswered()*2;
			double ratio =model.getTotalScore()/totalQuestions;
			String correctionString=model.getTotalScore()+"/"+totalQuestions;
			//on met à jour les statistiques si c'est la première correction
			scoreView.setText("Score = "+correctionString);
			String session = isSession ? model.annee+model.region:model.theme;
			Stat stat = new Stat(session, 
					System.currentTimeMillis(), 
					ratio,
					totalQuestions);
			statDao = new StatsDAO(this); 
			if(ratio!=0.0) {
				statDao.addStat(stat);			
				Methodes.alert("Statistique entrée : "+correctionString);
			}
			//Et on affiche le dialogue de fin :
			TextView msg = new TextView(this);
			msg.setText("Tu as eu "+correctionString+"\n" +
				""+getMessageFromRatio(ratio)+"\n\n"
				+ "Maintenant tu peux reprendre, QCM par QCM, la session terminée. "
				+ "La correction n'est pas donnée : à toi de modifier les réponses du QCM jusqu'à ce que le fond devienne vert !\n\n"
				+ "Code couleur : 3+ erreurs (0pt), 2 erreurs (0.4pt), 1 erreur (1pt) et pas d'erreur (2pts)");
			Methodes.hightlightTextView(msg, "3+ erreurs (0pt)", Color.RED);
			Methodes.hightlightTextView(msg, "2 erreurs (0.4pt)", Color.argb(255, 255, 130, 40));
			Methodes.hightlightTextView(msg, "1 erreur (1pt)", Color.YELLOW);
			Methodes.hightlightTextView(msg, "pas d'erreur (2pts)", Color.GREEN);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder
			.setTitle("Correction")
			.setView(msg)
			.setCancelable(false)
			.setPositiveButton("OK", null);//un bouton OK qui ne fait rien
			builder.create().show();
			//on arréte le timer et on signale qu'on a déjà corrigé
			model.setCorrected(true);
			//on update le menu (affiche modify, enlever corriger)
			supportInvalidateOptionsMenu();
		}		

		creerHorizontalSearchList();
		//met à jour la vue actuelle et les 2 vues adjacentes si elles existent
		((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag)).setGoodColor();
		if (currentFrag!=0 && mPagerAdapter.getItem(currentFrag-1)!=null) //premier fragment y'a pas de -1
			((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag-1)).setGoodColor();
		if (currentFrag!=60 && mPagerAdapter.getItem(currentFrag+1)!=null)//dernier fragment y'a pas de +1
			((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag+1)).setGoodColor();
	}

	protected static String getMessageFromRatio(double ratio) {
		if(ratio<0.2)
			return "C'est pas terrible terrible...";
		else if(ratio<0.4)
			return "Presque la moyenne, ça commence à rentrer.";
		else if(ratio<0.6)//=72/120
			return "C'est pas mal du tout, mais tu peux sûrement faire mieux";
		else if(ratio<0.8)//=96/120
			return "Là on est archi-bon pour l'internat !";
		else if(ratio<0.95)//=114/120
			return "Eh bah ça c'est du score !";
		else
			return "Like a boss ! (enfin si tu n'as pas triché...)";
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
	
	@Override
	public void onBackPressed() {
		//TODO bouton "sauver"
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_annales, menu);
		MenuItem menuSearch = menu.findItem(R.id.menu_search);
		SubMenu subMenuSearch = menuSearch.getSubMenu();
		subMenuSearch.clear();
		for (int i = 1; i < 61; i++) {
			subMenuSearch.add(Menu.NONE, LIST_FRAGMENT_ID,  i, "QCM n°"+i);	//l'id du fragment est pas vraiment unique mais c'est pas grave^^ 
		}
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		MenuItem menuModify = menu.findItem(R.id.menu_modify);
		MenuItem menuCorrect = menu.findItem(R.id.menu_correct);
		if (model.isCorrected()) {
			menuModify.setVisible(true);
			menuCorrect.setVisible(false);
		}
//		super.onCreateOptionsMenu(menu); //on n'appelle pas super pour ne pas avoir le menu partagé
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected (final MenuItem menuItem) {
		android.text.ClipboardManager clipboard = (android.text.ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
		switch(menuItem.getItemId()){
		case R.id.menu_retour_accueil:
		case android.R.id.home:
			onBackPressed();
			return true;
		case LIST_FRAGMENT_ID:
			pager.setCurrentItem(menuItem.getOrder()-1); //Bougre de Sagouin powaaa (je stocke la valeur dans l'order...)
			return true;
		case R.id.menu_correct:
			if(model.isCorrected()){
				correction();
			} else{
				new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("On corrige déjà ?")
				.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						correction();
//						supportInvalidateOptionsMenu();
					}
				})
				.setNegativeButton("Non", null)
				.show();
			}
			return true;
		case R.id.menu_modify:
			Bundle bundle = new Bundle();
			bundle.putParcelable("ITEM", ((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag)).getItem());
			Intent secondeActivite = new Intent(AbstractAnnaleActivity.this, EditAnnaleActivity.class);
			secondeActivite.putExtras(bundle);
			startActivity(secondeActivite);	
			return true;
		case R.id.menu_copy_session:
			String sessionCopy =((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag)).getItem().getSession();			
			clipboard.setText(sessionCopy);
			Toast.makeText(this, "Session copiée dans le presse-papier", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_copy_question:
			String questionCopy =((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag)).getItem().getQuestion();			
			clipboard.setText(questionCopy);
			Toast.makeText(this, "Question copiée dans le presse-papier", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_copy_items:
			Item item = ((QuestionFragmentsInterface) mPagerAdapter.getItem(currentFrag)).getItem();
			String itemsCopy ="A) "+item.getQcmA()+"\n" +
					"B) "+item.getQcmB()+"\n" +
					"C) "+item.getQcmC()+"\n" +
					"D) "+item.getQcmD()+"\n" +
					"E) "+item.getQcmE();			
			clipboard.setText(itemsCopy);
			Toast.makeText(this, "QCM copiés dans le presse-papier", Toast.LENGTH_SHORT).show();
			return true;
		}

		return super.onOptionsItemSelected(menuItem);
	}

	protected class SearchViewListener implements OnScrollListener{
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

