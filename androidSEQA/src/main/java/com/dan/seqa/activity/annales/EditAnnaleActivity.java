package com.dan.seqa.activity.annales;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.modeles.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dan.seqa.outils.Methodes;

public class EditAnnaleActivity extends AbstractActivity{

	private Item inputItem;
	private Item outputItem;
	private EditedAnnalesDAO editedAnnalesDAO;
	private AnnalesDAO annalesDAO;
	private TextView textViewSession;
	private TextView textViewCategorie;
	private TextView textViewCorrection;
	private TextView textViewCommentaire;
	private TextView textViewQuestion;
	private TextView[] choix = new TextView[5];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		editedAnnalesDAO = new EditedAnnalesDAO(this);
		annalesDAO = new AnnalesDAO(this);
		Bundle extraBundle = getIntent().getExtras();
		if(savedInstanceState==null) {
			if(extraBundle!=null && !extraBundle.isEmpty()){
				this.inputItem=extraBundle.getParcelable("ITEM");
			}  else {
				Methodes.alert("Erreur !! pas d'inputItem dans le bundle !!");
			}
		} else {
			this.inputItem = savedInstanceState.getParcelable("ITEM");
		}		
		outputItem = new Item(inputItem);
		Button cancelButton = (Button)findViewById(R.id.button_annuler);
		Button saveButton = (Button)findViewById(R.id.button_save);

		textViewSession = (TextView)findViewById(R.id.session);
		textViewCategorie =	(TextView)findViewById(R.id.QCM_categorie);
		textViewCorrection = (TextView)findViewById(R.id.correction);
		textViewQuestion = (TextView)findViewById(R.id.QCM_question);
		textViewCommentaire = (TextView)findViewById(R.id.commentaire);
		choix[0] = (TextView) findViewById(R.id.choix1);
		choix[1] = (TextView) findViewById(R.id.choix2);
		choix[2] = (TextView) findViewById(R.id.choix3);
		choix[3] = (TextView) findViewById(R.id.choix4);
		choix[4] = (TextView) findViewById(R.id.choix5);

		textViewQuestion.setOnClickListener(new EditListener(EditedAnnalesDAO.QUESTION));
		textViewCorrection.setOnClickListener(new CorrectionListener());
		textViewCategorie.setOnClickListener(new CategorieListener());
		textViewCommentaire.setOnClickListener(new EditListener(EditedAnnalesDAO.COMMENTAIRE));
		choix[0].setOnClickListener(new EditListener(EditedAnnalesDAO.QCM_A));
		choix[1].setOnClickListener(new EditListener(EditedAnnalesDAO.QCM_B));
		choix[2].setOnClickListener(new EditListener(EditedAnnalesDAO.QCM_C));
		choix[3].setOnClickListener(new EditListener(EditedAnnalesDAO.QCM_D));
		choix[4].setOnClickListener(new EditListener(EditedAnnalesDAO.QCM_E));
		cancelButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Toast.makeText(EditAnnaleActivity.this, "Modification annulée", Toast.LENGTH_SHORT).show();
				finish();
			}
		});		
		saveButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
                Item originalItem = annalesDAO.selectItem(outputItem);
                if(outputItem.equals(inputItem)) {
                    Toast.makeText(EditAnnaleActivity.this, "Aucun modification effectuée", Toast.LENGTH_SHORT).show();
                } else if (originalItem.equals(outputItem)) {
                    editedAnnalesDAO.retirer(outputItem);
					Toast.makeText(EditAnnaleActivity.this, "Modifications annulées, retour à l'item officiel", Toast.LENGTH_SHORT).show();
                } else {
                    editedAnnalesDAO.modifierOuAjouter(outputItem);
                    Toast.makeText(EditAnnaleActivity.this, "Modification enregistrée (valable la prochaine fois)", Toast.LENGTH_SHORT).show();
                }
                finish();
			}
		});

		updateView();
	}

	private void updateView() {
		textViewSession.setText(outputItem.getSession());
		textViewCategorie.setText(outputItem.getCategorie());
		textViewQuestion.setText(outputItem.getQuestion());
		textViewCorrection.setText("Réponse : "+outputItem.getCorrection());
		if(outputItem.getCommentaire()!=null)
			textViewCommentaire.setText("Commentaire : "+outputItem.getCommentaire());
		char[] abcde = new char[]{'A','B','C','D','E'};
		for (int i = 0; i < choix.length; i++) {
			choix[i].setText(abcde[i]+" - "+outputItem.getQcmArray()[i]);
		}
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		outState.putParcelable("ITEM", outputItem);
	}
	
	private class EditListener implements OnClickListener{
		
		private String type;
		public EditListener(String type) {
			this.type=type;
		}
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(EditAnnaleActivity.this);
			builder.setTitle("Modifier le texte");
			
			final EditText input = new EditText(EditAnnaleActivity.this);
			input.setInputType(InputType.TYPE_CLASS_TEXT);
			input.setText(getDefaultText());
			builder.setView(input);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					updateItem(input.getText().toString());
					updateView();
				}
			});
			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			
			builder.show();
		}	
		
		private void updateItem(String inputText) {
			switch (type) {
				case EditedAnnalesDAO.QUESTION:
					outputItem.setQuestion(inputText);
					break;
				case EditedAnnalesDAO.CATEGORIE:
					outputItem.setCategorie(inputText);
					break;
				case EditedAnnalesDAO.QCM_A:
					outputItem.setQcmA(inputText);
					break;
				case EditedAnnalesDAO.QCM_B:
					outputItem.setQcmB(inputText);
					break;
				case EditedAnnalesDAO.QCM_C:
					outputItem.setQcmC(inputText);
					break;
				case EditedAnnalesDAO.QCM_D:
					outputItem.setQcmD(inputText);
					break;
				case EditedAnnalesDAO.QCM_E:
					outputItem.setQcmE(inputText);
					break;
				case EditedAnnalesDAO.COMMENTAIRE:
					outputItem.setCommentaire(inputText);
					break;
				case EditedAnnalesDAO.CORRECTION:
					boolean ok1 = true;
					boolean ok2 = true;
					char[] inputArray = inputText.trim().toCharArray();
					Character[] valid = {'a', 'b', 'c', 'd', 'e'};
					if (inputArray.length > 1 && outputItem.getType().equals(Item.QCS))
						ok1 = false;
					else
						for (char charac : inputArray) {
							if (!Arrays.asList(valid).contains(charac)) {
								Methodes.alert("charac = '" + charac + "'");
								Methodes.alert("inputArray = '" + Arrays.toString(inputArray) + "'");
								Methodes.alert("valid = '" + Arrays.toString(valid) + "'");
								ok2 = false;
							}
						}
					if (!ok1)
						Toast.makeText(EditAnnaleActivity.this, "Erreur : la correction du QCS a plus d'une lettre", Toast.LENGTH_SHORT).show();
					else if (!ok2)
						Toast.makeText(EditAnnaleActivity.this, "Erreur : la correction doit être du type \"abcd\"", Toast.LENGTH_SHORT).show();
					else
						outputItem.setCorrection(inputText);

					break;
			}
		}
		
		private String getDefaultText() {
			switch (type) {
				case EditedAnnalesDAO.QUESTION:
					return outputItem.getQuestion();
				case EditedAnnalesDAO.CORRECTION:
					return outputItem.getCorrection();
				case EditedAnnalesDAO.COMMENTAIRE:
					return outputItem.getCommentaire();
				case EditedAnnalesDAO.CATEGORIE:
					return outputItem.getCategorie();
				case EditedAnnalesDAO.QCM_A:
					return outputItem.getQcmA();
				case EditedAnnalesDAO.QCM_B:
					return outputItem.getQcmB();
				case EditedAnnalesDAO.QCM_C:
					return outputItem.getQcmC();
				case EditedAnnalesDAO.QCM_D:
					return outputItem.getQcmD();
				case EditedAnnalesDAO.QCM_E:
					return outputItem.getQcmE();
				default:
					return "";
			}
		}
	}

	private class CorrectionListener implements OnClickListener{
		
		@SuppressLint("InflateParams")
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(EditAnnaleActivity.this);
			builder.setTitle("Modifier la correction");
			View view = null;
			switch (outputItem.getType()) {
				case Item.QCS:
					view = getLayoutInflater().inflate(R.layout.dialog_edit_correction_qcs, null);
					break;
				case Item.QCM:
					view = getLayoutInflater().inflate(R.layout.dialog_edit_correction_qcm, null);
					break;
				default:
					Methodes.alert("type = " + outputItem.getType());
					break;
			}
			builder.setView(view);
			
			final View[] neoChoix = new View[5];
            assert view != null;
            neoChoix[0] = view.findViewById(R.id.choix1);
			neoChoix[1] = view.findViewById(R.id.choix2);
			neoChoix[2] = view.findViewById(R.id.choix3);
			neoChoix[3] = view.findViewById(R.id.choix4);
			neoChoix[4] = view.findViewById(R.id.choix5);
			
			
			// Set up the buttons
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String reponseEntree="";
					for (View view2 : neoChoix) {
						CompoundButton cb = (CompoundButton) view2;
						if(cb.isChecked())
							reponseEntree+=cb.getText();
					}
					outputItem.setCorrection(reponseEntree);
					updateView();
				}
			});
			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

			builder.show();
		}	
	}

	private class CategorieListener implements OnClickListener{
        private LinearLayout resultLayout;
        private List<String> listItem;

        @SuppressLint("InflateParams")
		@Override
		public void onClick(View v) {
            listItem = new ArrayList<>();
			AlertDialog.Builder builder = new AlertDialog.Builder(EditAnnaleActivity.this);
			builder.setTitle("Modifier les catégories");
			View view = getLayoutInflater().inflate(R.layout.dialog_edit_categorie, null);
            assert view != null;
			builder.setView(view);

            List<String> listTotal = editedAnnalesDAO.getDifferent(EditedAnnalesDAO.CATEGORIE);
            resultLayout = (LinearLayout) view.findViewById(R.id.resultLayout);
            String categories[]=outputItem.getCategorie().split("-");
            listItem.addAll(Arrays.asList(categories));
            updateLayout();

            final AutoCompleteTextView actv = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
            actv.setAdapter(new ArrayAdapter<>(EditAnnaleActivity.this, android.R.layout.simple_dropdown_item_1line, listTotal));
            actv.setThreshold(1);
            actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listItem.add(((TextView) view).getText().toString());
                    actv.setText("");
                    updateLayout();
                }
            });


			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
                    String rtn = "";
                    for (int i = 0; i < listItem.size(); i++) {
                        String s = listItem.get(i);
                        if(i!=listItem.size()-1)
                            rtn+= s+" - ";
                        else
                            rtn+= s;
                    }
					outputItem.setCategorie(rtn);
					updateView();
				}
			});
			builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			builder.show();
		}


        private void updateLayout(){
            resultLayout.removeAllViews();
            for (final String text : listItem) {
                final TextView txtView = new TextView(EditAnnaleActivity.this);
                txtView.setText(text.trim());
                txtView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listItem.contains(text))
                            listItem.remove(text);
                        else
                            Methodes.alert("Erreur : " + text);
//                        resultLayout.removeView(v);
                        updateLayout();
//                        return false;
                    }
                });
                resultLayout.addView(txtView);
            }
        }
	}

}