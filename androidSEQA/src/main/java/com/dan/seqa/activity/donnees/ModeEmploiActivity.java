package com.dan.seqa.activity.donnees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dan.seqa.outils.Methodes;

public class ModeEmploiActivity extends AbstractActivity{


	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expendable_liste);
		ExpandableListView listView = (ExpandableListView) findViewById(R.id.liste);

		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> listOfChildGroups = new ArrayList<List<Map<String, String>>>();

		try {
			InputStream is = getAssets().open("modedemploi.json");
			JSONObject obj = new JSONObject(Methodes.loadJSONFromAsset(is, "UTF-8"));
			JSONArray m_jArry = obj.getJSONArray("menuitem");
			for (int i = 0; i < m_jArry.length(); i++) 	{
				final JSONObject categorie = m_jArry.getJSONObject(i);
				groupData.add(new HashMap<String, String>() {{
					put("ROOT_NAME", categorie.getString("titre"));
					put("ROOT_DESC", categorie.getString("soustitre"));
				}});
				JSONArray sub_array = categorie.optJSONArray("sous_menu");
				if(sub_array==null) {
					listOfChildGroups.add(new ArrayList<Map<String, String>>(){{
						add(new HashMap<String, String>() {{
							put("CHILD_NAME", categorie.getString("contenu"));
						}});
					}}); 
				} else {
					ArrayList<Map<String, String>> entry = new ArrayList<Map<String, String>>();
					for (int ii = 0; ii < sub_array.length(); ii++) {
						final JSONObject categorie2 = sub_array.getJSONObject(ii);
						final String texte="<h5>"+categorie2.getString("titre")+"</h5>"+categorie2.getString("contenu");
						entry.add(new HashMap<String, String>() {{
							put("CHILD_NAME", texte); 
						}});
					}
					listOfChildGroups.add(entry);
				}
			}
		} catch (JSONException e) {
			Methodes.alert("JSONException : "+e.getMessage());
		} catch (IOException e) {
			Methodes.alert("IOException : "+e.getMessage());
		}
		listView.setAdapter(new HtmlAdapter(
			this,

			groupData,
			android.R.layout.simple_expandable_list_item_2,
			new String[] {"ROOT_NAME", "ROOT_DESC"},
			new int[] {android.R.id.text1, android.R.id.text2},

			listOfChildGroups,
			R.layout.simple_expandable_list_item_1,//custom !
			new String[] {"CHILD_NAME"},
			new int[] {android.R.id.text1}
		));
	}	
	
	private class HtmlAdapter extends SimpleExpandableListAdapter{
		private List<? extends List<? extends Map<String, ?>>> childData;
		private String[] childFrom;
		private int[] childTo;
		
		public HtmlAdapter(Context context,
				List<? extends Map<String, ?>> groupData, int groupLayout,
				String[] groupFrom, int[] groupTo,
				List<? extends List<? extends Map<String, ?>>> childData,
				int childLayout, String[] childFrom, int[] childTo) {
			super(context, groupData, groupLayout, groupFrom, groupTo, childData,
					childLayout, childFrom, childTo);
			this.childData=childData;
			this.childFrom=childFrom;
			this.childTo=childTo;
		}


	    @Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
	        View v;
	        if (convertView == null) {
	            v = newChildView(isLastChild, parent);
	        } else {
	            v = convertView;
	        }
	        bindView(v, childData.get(groupPosition).get(childPosition), childFrom, childTo);
	        v.setOnTouchListener(new OnTouchListener() {				
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
	        return v;
	    }
	    
		// on peut pas mettre @Override parce que c'est private
		private void bindView(View view, Map<String, ?> data, String[] from, int[] to) {
	        int len = to.length;
	        for (int i = 0; i < len; i++) {
	            TextView v = (TextView)view.findViewById(to[i]);
	            if (v != null) {
	                v.setText(Html.fromHtml((String)data.get(from[i])));
	            }
	        }
	    }
	}
}