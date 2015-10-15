package com.dan.seqa.activity.donnees;

import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.modeles.Item;

import java.util.Iterator;
import java.util.Map.Entry;

import com.dan.seqa.outils.Methodes;

/**
 * Activité de navigation des éditions de QCM
 */
public class BrowseEditionsActivity extends AbstractActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_editions);
		LinearLayout layout = (LinearLayout) findViewById(R.id.browse_edition_container);
		EditedAnnalesDAO editedDao = new EditedAnnalesDAO(this);
		AnnalesDAO normalDao = new AnnalesDAO(this);

		LinearLayout.LayoutParams sessionLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		sessionLP.setMargins(0, 25, 0, 0);
		LinearLayout.LayoutParams diffLP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		diffLP.setMargins(50, 0, 0, 0);

		Methodes.alert("size"+editedDao.selectAll().size());

		for (Item editedItem : editedDao.selectAll()) {
			Item normalItem = normalDao.selectItem(editedItem);
			TextView sessionTV = new TextView(this);
			sessionTV.setText(editedItem.getSession()+"");
			sessionTV.setLayoutParams(sessionLP);
			layout.addView(sessionTV);

			Iterator<Entry<String, String>> it = normalItem.getMap().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> normalPairs = it.next();
				String editedValue = editedItem.getMap().get(normalPairs.getKey());
				String normalValue = normalPairs.getValue();
				normalValue=normalValue==null?"":normalValue;
				editedValue=editedValue==null?"":editedValue;
				if(!editedValue.equals(normalValue)) {
					TextView diffTV = new TextView(this);
					diffTV.setText(normalPairs.getKey()+" : '"+normalValue+"' --> '"+editedValue+"'");
					diffTV.setLayoutParams(diffLP);
					layout.addView(diffTV);
					
//					TODO : surligner les différences entre le normal et le édité	
//					DiffMatchPatch to = new DiffMatchPatch();
//					Methodes.alert(editedItem.getSession()+" - Key = "+normalPairs.getKey());	
//					LinkedList<Diff> yo = to.diff_main(normalValue, editedValue);
//					for (Diff diff : yo) {
//						if(diff.operation!=Operation.EQUAL) {
//							TextView differenceTV = new TextView(this);
//							differenceTV.setText(diff.operation+" --> "+diff.text);
//							differenceTV.setLayoutParams(buttonLP);
//							colorLayout.addView(differenceTV);
//						}
//					}
					it.remove(); // avoids a ConcurrentModificationException
				}
			}
		}

	}
}