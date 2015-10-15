package com.dan.seqa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.modeles.Item;

import com.dan.seqa.outils.Methodes;

public abstract class AbstractQuestionFragment extends Fragment{

	//Trier du plus grand mot au plus petit !
	protected static final String[] MOTS_A_SURLIGNER = new String[] {
		"vraies", "vraie", "vrais", "vrai", 
		"fausses", "fausse", "faux",  
		"exactes", "exacte", "exacts", "exact", 
		"inexactes", "inexacte", "inexacts", "inexact",
	"pas"};
	protected static final String COULEUR_SURLIGNAGE_VRAIFAUX = "red";
	protected static final String COULEUR_SURLIGNAGE_EDITE = "#FFA500";
	protected Item mItem;
	protected AnnaleModel mModel;
	protected RelativeLayout colorLayout;
	protected LinearLayout containerLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Récupération du Bundle
		Bundle args = getArguments();
		if (!args.containsKey(AnnalePagerAdapter.KEY_ITEM)||!args.containsKey(AnnalePagerAdapter.KEY_MODEL)) {
			Methodes.alert("Erreur Bundle incorrect : manque KEY_ITEM ou KEY_MODEL");
			return null;	
		}
		mItem = args.getParcelable(AnnalePagerAdapter.KEY_ITEM);
		mModel = (AnnaleModel)args.getParcelable(AnnalePagerAdapter.KEY_MODEL);
		EditedAnnalesDAO editDao = new EditedAnnalesDAO(getActivity());
		//Inflation de la view 
		View tmpView = inflater.inflate(R.layout.fragment_item_abstract,container, false);
		colorLayout= (RelativeLayout) tmpView.findViewById(R.id.layout_item);
		containerLayout= (LinearLayout) tmpView.findViewById(R.id.item_scrollview);
		TextView textViewSession = (TextView)tmpView.findViewById(R.id.session),
				textViewCategorie =	(TextView)tmpView.findViewById(R.id.categorie),
				textViewQuestion = (TextView)tmpView.findViewById(R.id.question);
		//Remplissage des champs
		textViewSession.setText(mItem.getSession());
		textViewCategorie.setText(mItem.getCategorie());
		String question = mItem.getQuestion();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean surligner = preferences.getBoolean("surligner", false);
		if(surligner) 
			for (String motASurligner : MOTS_A_SURLIGNER) {
				Methodes.alert("mot="+motASurligner);
				question = question.replaceAll(motASurligner,"<font color='"+COULEUR_SURLIGNAGE_VRAIFAUX+"'>"+motASurligner+"</font>");				
			}
		if(mItem.isEdited(editDao)) {
			question+=" <font color='"+COULEUR_SURLIGNAGE_EDITE+"'>(édité)</font>";
			//icone commentaire
			final String commentaire = editDao.selectItem(mItem).getCommentaire();
			ImageButton commentButton= (ImageButton) tmpView.findViewById(R.id.comment_button);
			if(commentaire!=null && commentaire.length()!=0)
				commentButton.setVisibility(View.VISIBLE);
				commentButton.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setTitle("Commentaire de la question n°"+mItem.getNumero())
						.setMessage(commentaire)
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//do things
							}
						});
						AlertDialog alert = builder.create();
						alert.show();
					}
				});
		}
		textViewQuestion.setText(Html.fromHtml(question));
		return tmpView;
	}

}
