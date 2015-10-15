package com.dan.seqa.activity.donnees;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.bdd.StatsDAO;
import com.dan.seqa.modeles.SharedMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.dan.seqa.outils.Methodes;

public class HoroscopeActivity extends AbstractActivity{
	
	private int totalQCS;
	private int totalQCM;
	private int[] subTotauxQCM = {0,0,0,0,0};
	private String optionTri="lettre";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);	
		
		setActivityView();
	}

	private void setActivityView() {
		//Les choix simples
		setContentView(R.layout.activity_horoscope);
		LinearLayout qcsLayout = (LinearLayout) findViewById(R.id.horoscope_QCS_layout);
		//	//Test de tri
		TreeMap<String, Integer> qcsMap=getMapQCS();
		if(optionTri.equals("nombre")) {	        
			ValueComparator bvc =  new ValueComparator(qcsMap);
	        TreeMap<String, Integer> tmpMap = new TreeMap<String,Integer>(bvc);
	        tmpMap.putAll(qcsMap);
	        qcsMap=tmpMap;
		}
		Methodes.alert("qcsMap="+qcsMap);
		//	//test end
		for(Entry<String, Integer> entry : getMapQCS().entrySet()) {
		    String cle = entry.getKey();
		    int valeur = entry.getValue();
		    TextView txt = new TextView(this);
		    txt.setText("-- "+cle+"="+valeur+"\t ("+(int)(100*(double)valeur/totalQCS)+"%)");
		    qcsLayout.addView(txt);
		}		
		TextView separatorQCS = new TextView(this);
		separatorQCS.setText("----------------");
		qcsLayout.addView(separatorQCS);
	    TextView txtTotalQCS = new TextView(this);
	    txtTotalQCS.setText("Total QCS : "+totalQCS);
	    qcsLayout.addView(txtTotalQCS);
		
	    //Les choix multiples
		LinearLayout qcmLayout = (LinearLayout) findViewById(R.id.horoscope_QCM_layout);
		TreeMap<String, Integer>[] mapArray = getMapQCM();
		for (int i = 0; i < mapArray.length; i++) {			
		    TextView txt = new TextView(this);
		    String s= i==0?"":"s";
		    qcmLayout.addView(txt);
		    int subTotal =subTotauxQCM[i];
			for(Entry<String, Integer> entry : mapArray[i].entrySet()) {
			    String cle = entry.getKey();
			    int valeur = entry.getValue();
			    TextView txt2 = new TextView(this);
			    txt2.setText("-- "+cle+"="+valeur+" ("+(int)(100*(double)valeur/subTotal)+"%)");
			    qcmLayout.addView(txt2);
			}		
		    txt.setText("QCM à "+(i+1)+" entrée"+s+" : "+subTotal+" ("+(int)(100*(double)subTotal/totalQCM)+"%)");
			txt.setTextAppearance(this, android.R.attr.textAppearanceLarge);
		}		
		TextView separatorQCM = new TextView(this);
		separatorQCM.setText("----------------");
		qcmLayout.addView(separatorQCM);
	    TextView txtTotalQCM = new TextView(this);
	    txtTotalQCM.setText("Total QCM : "+totalQCM);
	    qcmLayout.addView(txtTotalQCM);
	    
	    Methodes.alert("totalQCS="+totalQCS+", totalQCM="+totalQCM+", total="+(totalQCS+totalQCM));
		Methodes.alert("optionTri="+optionTri+" ");
	}
	
	private TreeMap<String, Integer>[] getMapQCM() {
		totalQCM=0;
//		TreeMap<String, Integer> tmpMap = new TreeMap<String, Integer>();
		@SuppressWarnings("unchecked")
		TreeMap<String, Integer>[] tmpMapp = new TreeMap[5];
		ArrayList<String[]> patterns = new ArrayList<String[]>();
		patterns.add(new String[]{"a","b","c","d","e"});
		patterns.add(new String[]{"ab","ac","ad","ae","bc","bd","be","cd","ce","de"});
		patterns.add(new String[]{"abc","abd","abe","acd","ace","ade","bcd","bce","bde","cde"});
		patterns.add(new String[]{"abcd","abce","abde","acde","bcde"});
		patterns.add(new String[]{"abcde"});		
		StatsDAO dao = new StatsDAO(this);
		for (int i = 0; i < subTotauxQCM.length; i++) {		
			subTotauxQCM[i]=0;
		}			
		for (String[] strings : patterns) {
			for (String string : strings) {
				int count = dao.compterEntreesCorrection("QCM", string);
//				Methodes.alert("string="+string+", string.length()-1="+(string.length()-1)+", count="+count+", tmpMapp="+tmpMapp+", tmpMapp.length="+tmpMapp.length);
				if(tmpMapp[string.length()-1]==null)
					tmpMapp[string.length()-1]=new TreeMap<String, Integer>();
				tmpMapp[string.length()-1].put(string, count);
//				tmpMap.put(string, count);
				subTotauxQCM[string.length()-1]+=count;
			}
		}	
		for (int subTotal : subTotauxQCM) {
			totalQCM+=subTotal;
		}
		
		return tmpMapp;
	}

	private TreeMap<String, Integer> getMapQCS() {
		totalQCS=0;
		TreeMap<String, Integer> tmpMap = new TreeMap<String, Integer>();
		String patterns[] = new String[]{"a","b","c","d","e"};
		StatsDAO dao = new StatsDAO(this);
		for (String string : patterns) {
			int count = dao.compterEntreesCorrection("QCS", string);
			tmpMap.put(string, count);
			totalQCS+=count;
		}
		return tmpMap;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//TODO option de tri !
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_horoscope, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(SharedMenu.onOptionsItemSelected(item, this) == false) {
			switch (item.getItemId()) {
			case R.id.menu_trier_lettre: 
				optionTri="lettre";
				setActivityView();
				return true;
			case R.id.menu_trier_nombre: 
				optionTri="nombre";
				setActivityView();
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	class ValueComparator implements Comparator<String> {
	    Map<String, Integer> base;
	    public ValueComparator(Map<String, Integer> base) {
	        this.base = base;
	    }
	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    @Override
		public int compare(String a, String b) {
	        if (base.get(a) >= base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
}