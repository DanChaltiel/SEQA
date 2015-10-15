package com.dan.seqa.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AccueilActivity;
import com.dan.seqa.activity.annales.AnnaleSessionActivity;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.StatsDAO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dan.seqa.outils.Methodes;

public class ChoixSessionDialogFragment extends DialogFragment {

	private Spinner spinnerAnnees, spinnerRegion;
	private TextView textRegion;
	private static AnnaleSessionActivity caller;
	private StatsDAO dao;
	private static final Map<String, String> REGION_CODE;
	static {
		REGION_CODE = new HashMap<String, String>();
		REGION_CODE.put("Nord", "N");
		REGION_CODE.put("Sud", "S");
		REGION_CODE.put("Mai", "M");
		REGION_CODE.put("Décembre", "D");
	}
	
	public static ChoixSessionDialogFragment newInstance(String title, AnnaleSessionActivity pCaller) {
		ChoixSessionDialogFragment dialog = new ChoixSessionDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		dialog.setArguments(args);
		caller=pCaller;
		return dialog;
	}

	private View getTheView(){
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_choix_session, null);

		dao = new StatsDAO(getActivity()); //StatsDAO ou AnnalesDAO, les 2 ont la méthode
		List<String> sessionsList = dao.getDifferent(AnnalesDAO.ANNEE);
		Collections.reverse(sessionsList);
		
		String[] anneesArray = new String[sessionsList.size()];
		//Les années
		spinnerAnnees = (Spinner) v.findViewById(R.id.spinnerAnnees);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, sessionsList.toArray(anneesArray));
		spinnerAnnees.setAdapter(adapter);
		//Les régions
		spinnerRegion = (Spinner) v.findViewById(R.id.spinnerRegion);
		textRegion = (TextView) v.findViewById(R.id.textRegion);
		final ArrayAdapter<CharSequence> adapterRegions = ArrayAdapter.createFromResource(this.getActivity(), R.array.regions, android.R.layout.simple_spinner_item);
		final ArrayAdapter<CharSequence> adapterRegions2014 = ArrayAdapter.createFromResource(this.getActivity(), R.array.regions2014, android.R.layout.simple_spinner_item);
		adapterRegions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// Specify the colorLayout to use when the list of choices appears
		spinnerRegion.setAdapter(adapterRegions);
		//les régions que y'a pas certaines années
		spinnerAnnees.setOnItemSelectedListener(new OnItemSelectedListener() {
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String anneeSelected =((TextView)selectedItemView).getText().toString();
				boolean is2014 = Integer.parseInt(anneeSelected)==2014;
				ArrayAdapter<CharSequence> goodAdapter = is2014?adapterRegions2014:adapterRegions;
				spinnerRegion.setAdapter(goodAdapter);
				if (dao.isRegionised(anneeSelected) || is2014) { //si c'est juste une année on cache la région
					textRegion.setVisibility(View.VISIBLE);
					spinnerRegion.setVisibility(View.VISIBLE);
				}
				else{
					textRegion.setVisibility(View.GONE);
					spinnerRegion.setVisibility(View.GONE);
				}
				Methodes.alert(spinnerAnnees.getSelectedItem().toString()+"-"+spinnerRegion.getSelectedItem().toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {}
		});
		return v;
	}
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString("title"))
        		.setView(getTheView())
        		.setCancelable(false)
                .setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
						public void onClick(DialogInterface dialog, int whichButton) {
                    		String region = "";
                    		if(spinnerRegion.getVisibility()==View.VISIBLE) {
								region = REGION_CODE.get(spinnerRegion.getSelectedItem().toString());
							}
                            caller.choixSessionOK(""+spinnerAnnees.getSelectedItem(), region);
                        }
                    }
                )
                .setNegativeButton("Annuler", 
                	new DialogInterface.OnClickListener() {
	                	@Override
	                	public void onClick(DialogInterface dialog, int whichButton) {
	        				Intent intent = new Intent(caller, AccueilActivity.class);
	        	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        				caller.startActivity(intent);
	                	}
	                }
                )
                .create();
    }
}