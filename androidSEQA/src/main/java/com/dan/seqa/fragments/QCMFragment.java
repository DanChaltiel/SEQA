package com.dan.seqa.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.dan.newseqa.R;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.modeles.AnnaleInterface;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.modeles.Item;
import com.dan.seqa.modeles.QuestionFragmentsInterface;

public class QCMFragment extends AbstractQuestionFragment implements QuestionFragmentsInterface{

	private CheckBox qcmArray[];
	//Protected : mItem, mModel, containerLayout & colorLayout

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {	
		View tmpView = super.onCreateView(inflater, container, savedInstanceState);
		View itemContainer = inflater.inflate(R.layout.fragment_item_qcm, containerLayout, false);
		colorLayout= (RelativeLayout) tmpView.findViewById(R.id.layout_item);
		CheckBox qcm[] = {(CheckBox) itemContainer.findViewById(R.id.choix1),
				(CheckBox) itemContainer.findViewById(R.id.choix2),
				(CheckBox) itemContainer.findViewById(R.id.choix3),
				(CheckBox) itemContainer.findViewById(R.id.choix4),
				(CheckBox) itemContainer.findViewById(R.id.choix5),
		};
		containerLayout.addView(itemContainer);
		this.qcmArray = qcm;
		for (int i = 0; i < qcm.length; i++) {
			qcm[i].setText(mItem.getQcmArray()[i]);
		}
		//Gestion du modèle
		for (CheckBox checkBox : qcm) {
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {					
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mModel.setUserInput(Integer.parseInt(mItem.getOrderNumero()), getSelectedRadioLetters());
					((AnnaleInterface) getActivity()).updateViewOnFragmentChange();					
				}
			});
		}
		//si le fragment est construit mais déjà répondu
		String userInput = mModel.getUserInput(Integer.parseInt(mItem.getOrderNumero()));
		if(userInput!=null) {
			char abcde[] = {'a','b','c','d','e'};
			for (int i = 0; i < abcde.length; i++) {
				qcm[i].setChecked(userInput.indexOf(abcde[i])!=-1);
			}
		}			
		this.setGoodColor();
		return tmpView;
	}


	private String getSelectedRadioLetters() {
		String tmp ="";
		if(qcmArray[0].isChecked())	tmp+="a";
		if(qcmArray[1].isChecked())	tmp+="b";
		if(qcmArray[2].isChecked())	tmp+="c";
		if(qcmArray[3].isChecked())	tmp+="d";
		if(qcmArray[4].isChecked())	tmp+="e";
		return tmp;
	}

	public static QCMFragment newInstance(Item item, AnnaleModel model) {		
		QCMFragment fragment = new QCMFragment();
		Bundle args = new Bundle();
		args.putParcelable(AnnalePagerAdapter.KEY_ITEM, item);
		args.putParcelable(AnnalePagerAdapter.KEY_MODEL, model);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void setGoodColor() {
		if(mModel!=null) {
			String reponseModel = mModel.getUserInput(Integer.parseInt(mItem.getOrderNumero()));
			boolean modelNonVide = reponseModel!=null && reponseModel!="" && reponseModel!="null";		
			if(modelNonVide && mModel.isCorrected()){
				int nbErreurs = 0;
				char abcde[] = {'a','b','c','d','e'};
				for (char c : abcde) {
					if(reponseModel.matches(".*"+c+".*")!=mItem.getCorrection().matches(".*"+c+".*"))
						nbErreurs++;
				}
				switch (nbErreurs) {
				case 0:	colorLayout.setBackgroundColor(Color.GREEN);	break;
				case 1:	colorLayout.setBackgroundColor(Color.YELLOW);	break;
				case 2:	colorLayout.setBackgroundColor(Color.argb(255, 255, 130, 40));	break; //Orange pas dans android.graphics.Color
				default : colorLayout.setBackgroundColor(Color.RED);	break;

				}	
			}
		}
	}

	@Override
	public Item getItem(){
		return mItem;
	}
}
