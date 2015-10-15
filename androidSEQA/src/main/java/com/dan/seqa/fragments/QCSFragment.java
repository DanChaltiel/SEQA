package com.dan.seqa.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.adapters.AnnalePagerAdapter;
import com.dan.seqa.modeles.AnnaleInterface;
import com.dan.seqa.modeles.AnnaleModel;
import com.dan.seqa.modeles.Item;
import com.dan.seqa.modeles.QuestionFragmentsInterface;

public class QCSFragment extends AbstractQuestionFragment implements QuestionFragmentsInterface {
	
	private RadioGroup mGroup;
	//Protected : mItem, mModel, containerLayout & colorLayout
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View tmpView = super.onCreateView(inflater, container, savedInstanceState);
		
		mGroup= (RadioGroup) inflater.inflate(R.layout.fragment_item_qcs,containerLayout, false);
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				mModel.setUserInput(Integer.parseInt(mItem.getOrderNumero()), getSelectedRadioLetter());
				((AnnaleInterface) getActivity()).updateViewOnFragmentChange();
			}
		});
		containerLayout.addView(mGroup);
		TextView qcs[] = {(TextView) mGroup.findViewById(R.id.choix1),
				(TextView) mGroup.findViewById(R.id.choix2),
				(TextView) mGroup.findViewById(R.id.choix3),
				(TextView) mGroup.findViewById(R.id.choix4),
				(TextView) mGroup.findViewById(R.id.choix5),
		};
		for (int i = 0; i < qcs.length; i++) {
			qcs[i].setText(mItem.getQcmArray()[i]);
		}
		//si le fragment est construit mais déjà répondu (onSaved ou onKill...)
		String userInput = mModel.getUserInput(Integer.parseInt(mItem.getOrderNumero()));
		if(userInput!=null) {
			char selected = userInput.charAt(0);
			int id[] = {R.id.choix1,R.id.choix2,R.id.choix3,R.id.choix4,R.id.choix5};
			mGroup.check(id["abcde".indexOf(selected)]);
		}	
		this.setGoodColor();
		return tmpView;
	}


	public static QCSFragment newInstance(Item item, AnnaleModel model) {		
		QCSFragment fragment = new QCSFragment();
		Bundle args = new Bundle();
		args.putParcelable(AnnalePagerAdapter.KEY_ITEM, item);
		args.putParcelable(AnnalePagerAdapter.KEY_MODEL, model);
		fragment.setArguments(args);
		return fragment;
	}

	private String getSelectedRadioLetter(){
		switch (mGroup.getCheckedRadioButtonId()) {
			case R.id.choix1:return"a";
			case R.id.choix2:return"b";
			case R.id.choix3:return"c";
			case R.id.choix4:return"d";
			case R.id.choix5:return"e";
			default:return"";
		}	
	}

	@Override
	public void setGoodColor() {
		if(mModel!=null) {
			String reponseModel = mModel.getUserInput(Integer.parseInt(mItem.getOrderNumero()));
			boolean modelNonVide = reponseModel!=null && reponseModel!="" && reponseModel!="null";
			if(modelNonVide && mModel.isCorrected()){
				int goodColor = reponseModel.equals(mItem.getCorrection())?Color.GREEN:Color.RED;			
				colorLayout.setBackgroundColor(goodColor);		
			}
		}
	}

	@Override
	public Item getItem(){
		return mItem;
	}
}
