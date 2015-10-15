package com.dan.seqa.activity.stats;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.bdd.DatabaseHandler;
import com.dan.seqa.bdd.StatsDAO;
import com.dan.seqa.modeles.ScoreEtCompte;
import com.dan.seqa.modeles.SharedMenu;
import com.dan.seqa.modeles.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dan.seqa.outils.Methodes;

/**
 * Activité de statistique spécifique à une session ou à une catégorie
 */
public class StatistiquesDetailActivity extends AbstractActivity{
	
	private ListView detailListView;
	private String nomSession;
	private String optionTri = DatabaseHandler.STATS_SCORE_RATIO;
	private SharedPreferences preferences;
	private BaseAdapter adapter;
	private List<HashMap<String, String>> listeForAdapter;		

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_statistiques_detail);
		
	    nomSession =getIntent().getStringExtra(StatistiquesActivity.EXTRA_NOM_SESSION);
	    this.setTitle("Session "+nomSession);
	    	    
		//Le Graphique
		LinearLayout layoutGraphique = (LinearLayout) findViewById(R.id.layoutForGraphique);
		GraphesView graphique = new GraphesView(this, nomSession);
		if(graphique.getMeasuredHeight()>100)
			graphique.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
		graphique.invalidate();
		layoutGraphique.addView(graphique);
		Methodes.alert("taille : "+graphique.getHeight()+" mesured="+graphique.getMeasuredHeight());
	}

	@Override
	public void onResume() {
		super.onResume();
		//Le sous-titre
		StatsDAO dao = new StatsDAO(this);
	    TextView sousTitre = (TextView) findViewById(R.id.detailStatSubTitle); 
	    int total = (int) dao.getBestOfSession(nomSession).getScoreTotal();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String statsTimeChoice = preferences.getString("statsTimeChoice", "mois");
	    ScoreEtCompte sEtc = dao.getMoyenne(nomSession, statsTimeChoice);
		String moyenneString = Methodes.arrondir(sEtc.ratio*total, 1)+"/"+total;
		String finEssais = sEtc.nombreEssais==1?" essai":" essais";
		String subTitle = sEtc.nombreEssais==0?
				moyenneString+", sur "+sEtc.nombreEssais+finEssais+" (le dernier "+statsTimeChoice+")":
				"aucun essai ce dernier "+statsTimeChoice+"";
	    sousTitre.setText("Moyenne : "+subTitle);
		dao.close();
		//La liste
	    listeForAdapter=getList();
		detailListView = (ListView) findViewById(R.id.listDetailStats); 
		adapter = new SimpleAdapter(this,  
				listeForAdapter, 
				R.layout.liste_simple,//Layout par défaut pour avoir deux textes l'un au-dessus de l'autre
				new String[] {"date", "resultat"}, //clés entrée dans la méthode privée {@getList()}
				new int[] {android.R.id.text1, android.R.id.text2 }); //colorLayout par défaut du package android
		detailListView.setAdapter(adapter);
		Methodes.alert("StatistiquesDetailActivity ("+nomSession+"): "+listeForAdapter.size()+" entrées");
	}

	private List<HashMap<String, String>> getList(){
		List<HashMap<String, String>> rtnListe = new ArrayList<HashMap<String, String>>();

		StatsDAO dao = new StatsDAO(this);
		ArrayList<Stat> statistiques = dao.getAllStatsOfSession(nomSession, optionTri);
		for (Stat stat : statistiques) {
			HashMap<String, String> element = new HashMap<String, String>();
			element.put("date", stat.getFormattedDate());
			int noteMinimale = preferences.getInt("minStatsValue", 1);
			String resultat =  stat.getNote()>noteMinimale?stat.getFormattedScore():"note trop basse (<"+noteMinimale+")";
			element.put("resultat", resultat);	
			rtnListe.add(element);			
		}
		dao.close();
		return rtnListe;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_statsdetail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(SharedMenu.onOptionsItemSelected(item, this) == false) {
			switch (item.getItemId()) {
			case R.id.menu_share: //TODO partage des scores...
				Toast.makeText(getApplicationContext(), "Option de partage en développement...", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.menu_trier_date: 
				optionTri=DatabaseHandler.STATS_DATE;
				onResume();
				return true;
			case R.id.menu_trier_note: 
				optionTri=DatabaseHandler.STATS_SCORE_RATIO;
				onResume();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	class GraphesView extends View {
		private final static int padding = 30;
		private Paint paint;
		private List<Double> ratioList =  new ArrayList<Double>();

		public GraphesView(Context context, String subject) {
			super(context);
			paint = new Paint();         

			StatsDAO dao = new StatsDAO(StatistiquesDetailActivity.this);
			ArrayList<Stat> blabla = dao.getAllStatsOfSession(subject, DatabaseHandler.STATS_DATE); //pour le graphe on trie toujours par date
			for (Stat stat : blabla) {
				ratioList.add(stat.getScoreRatio());
			}
			dao.close();
		}

		// Dessinons sur la totalité de l'écran
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawRGB(0, 0, 0);//fond noir
			paint.setAntiAlias(true);

			// On va dessiner selon les dimensions du colorLayout
			int iWidth = getWidth(); // Largeur
			int iHeight = getHeight(); // Hauteur

			//axes et contour
			int gauche = padding,
			droite = iWidth-padding,
			haut = padding,
			bas = iHeight-padding;
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth (5);
			canvas.drawLine(gauche, bas, 
					gauche, haut, 
					paint);	//ordonnées : on reste à gauche et on va de bas en haut
			canvas.drawLine(gauche, bas, 
					droite, bas, 
					paint);	//abscisses : on reste en bas et on va de gauche à droite
			paint.setStyle(Style.STROKE);
			canvas.drawRect(0, 0, iWidth, iHeight, paint);//contour

			//colonnes
			paint.setColor(Color.GREEN);
			paint.setStyle(Style.FILL);
			int xPadding = (iWidth-2*padding)/ratioList.size();
			int spaceNow=padding;
			for (Double ratio : ratioList) {
				spaceNow+=xPadding;	
				canvas.drawRect(spaceNow-xPadding/2, (int)((1-ratio)*(bas-haut)), 
						spaceNow, bas, paint);
//				Methodes.alert("spacenow="+spaceNow+"\n hauteur="+(int)(ratio*haut));
			}
		}
	}	
}