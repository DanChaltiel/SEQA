package com.dan.seqa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.modeles.Item;
import com.dan.seqa.modeles.QuestionFragmentsInterface;

public class QCVideFragment extends Fragment implements QuestionFragmentsInterface{

	private Item mItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View tmpView = inflater.inflate(R.layout.fragment_item_vide,container, false);
		Bundle args = getArguments();
		TextView textViewSession =	(TextView)tmpView.findViewById(R.id.QCVide_session),
				textViewQuestion =	(TextView)tmpView.findViewById(R.id.QCVide_question);
		if(args.containsKey("KEY_ITEM")){
			mItem = (Item)args.getParcelable("KEY_ITEM");
			textViewSession.setText(mItem.getSession());
			textViewQuestion.setText("Question retirée par le jury");
		}
		return tmpView;
	}

	public static QCVideFragment newInstance(Item item) {		
		QCVideFragment fragment = new QCVideFragment();
		Bundle args = new Bundle();
		args.putParcelable("KEY_ITEM", item);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Item getItem(){
		return mItem;
	}

	@Override
	public void setGoodColor() {/*pas besoin*/}
}
