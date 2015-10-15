package com.dan.seqa.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AccueilActivity;
import com.dan.seqa.activity.annales.AnnaleThemeActivity;
import com.dan.seqa.bdd.AnnalesDAO;

import java.util.List;

public class ChoixThemeDialogFragment extends DialogFragment {

	private Spinner spinnerThemes;
	private static AnnaleThemeActivity caller;
	
	public static ChoixThemeDialogFragment newInstance(String title, AnnaleThemeActivity pCaller) {
		ChoixThemeDialogFragment dialog = new ChoixThemeDialogFragment();
		Bundle args = new Bundle();
		args.putString("title", title);
		dialog.setArguments(args);
		caller=pCaller;
		return dialog;
	}

    @SuppressLint("InflateParams")
	private View getTheView(){
		AnnalesDAO dao = new AnnalesDAO(caller);
		List<String> themes = dao.getDifferent(AnnalesDAO.CATEGORIE);
		dao.close(); View v = caller.getLayoutInflater().inflate( R.layout.dialog_choix_theme, null);

		String[] anneesArray = new String[themes.size()];
		spinnerThemes = (Spinner) v.findViewById(R.id.spinnerThemes);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, themes.toArray(anneesArray));
		spinnerThemes.setAdapter(adapter);
		return v;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
		.setTitle(getArguments().getString("title"))
		.setView(getTheView())
		.setCancelable(false)
		.setPositiveButton("OK",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {                    	
				caller.choixThemeOK(""+spinnerThemes.getSelectedItem());
			}
		})
		.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(caller, AccueilActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				caller.startActivity(intent);
			}
		})
		.create();
	}
}