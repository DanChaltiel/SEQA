package com.dan.seqa.activity;


import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dan.newseqa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dan.seqa.outils.Methodes;

/**
 * Activité de Foire aux Questions
 * Dépend de faq.json dans les assets
 */
public class FAQActivity extends AbstractActivity{

    @Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_liste);
        ListView vue = (ListView) findViewById(R.id.liste);

		final List<HashMap<String, String>> liste = new ArrayList<>();
		
		try {
			InputStream is = getAssets().open("faq.json");
			JSONObject obj = new JSONObject(Methodes.loadJSONFromAsset(is, "UTF-8"));
			JSONArray m_jArry = obj.getJSONArray("faq");
			for (int i = 0; i < m_jArry.length(); i++) 	{
				final JSONObject categorie = m_jArry.getJSONObject(i);
				liste.add(new HashMap<String, String>() {{
					put("QUESTION", categorie.getString("question"));
					put("REPONSE", categorie.getString("reponse"));
				}});
			}
		} catch (JSONException e) {
			Methodes.alert("JSONException : "+e.getMessage());
		} catch (IOException e) {
			Methodes.alert("IOException : "+e.getMessage());
		}
		
		vue.setAdapter(new SimpleAdapter(this,
                liste,
                R.layout.liste_simple,//Layout par défaut pour avoir deux textes l'un au-dessus de l'autre
                new String[]{"QUESTION", "REPONSE"}, //clés entrée dans la méthode privée
                new int[]{android.R.id.text1, android.R.id.text2})); //id par défaut du package android

	}
}