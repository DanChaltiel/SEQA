package com.dan.seqa.activity.donnees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.activity.annales.EditAnnaleActivity;
import com.dan.seqa.bdd.AnnalesDAO;
import com.dan.seqa.bdd.EditedAnnalesDAO;
import com.dan.seqa.modeles.Item;

import java.util.ArrayList;

import com.dan.seqa.outils.Methodes;

public class RechercheActivity extends AbstractActivity{


	public boolean afficherCorrection = false;
	
	private String inputString;
	private AnnalesDAO annalesDAO;
    private EditedAnnalesDAO editedAnnalesDAO;
	private ExpandableListView expendableListView;
	private TextView errorMessage;
	private ItemsExpandableAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recherche);
		//Variables
		annalesDAO = new AnnalesDAO(this);
        editedAnnalesDAO = new EditedAnnalesDAO(this);
		adapter = new ItemsExpandableAdapter(this, new ArrayList<ExpandListGroup>());
		expendableListView = (ExpandableListView) findViewById(R.id.rechercheListView);
        expendableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpandListChild child = (ExpandListChild) adapter.getChild(groupPosition, childPosition);
                Methodes.alert("session="+child.item.getSession());

                Bundle bundle = new Bundle();
                bundle.putParcelable("ITEM", child.item);
                Intent secondeActivite = new Intent(RechercheActivity.this, EditAnnaleActivity.class);
                secondeActivite.putExtras(bundle);
                startActivity(secondeActivite);
                return false;
            }
        });


                errorMessage = (TextView) findViewById(R.id.errorMessage);
		final EditText input = (EditText) findViewById(R.id.rechercheInput);
		final Button rechercheButton = (Button) findViewById(R.id.rechercheButton);
		final CheckBox correctionButton = (CheckBox) findViewById(R.id.afficherCorrectionCB);
		//Bundle
		if(savedInstanceState!=null) {
			inputString = savedInstanceState.getString("INPUTSTRING");
			input.setText(inputString);
			onClickAction(input);
			Methodes.alert("onCreate savedInstanceState inputString="+inputString);
		}
		//Listeners
		input.setOnEditorActionListener(new OnEditorActionListener() {			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				onClickAction(input);
				return false;
			}
		});		
		rechercheButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				onClickAction(input);
			}
		});		
		correctionButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				afficherCorrection=isChecked;
				expendableListView.invalidate();				
				for (int i = 0; i <adapter.getGroupCount(); i++)
					expendableListView.collapseGroup(i);
			}
		});	
//		annalesDAO.close(); //???
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("INPUTSTRING", inputString);
	}

    @Override
    protected void onResume(){
        Methodes.alert("prout");
        if(inputString!=null) {
            updateListView();
        }
        super.onResume();
    }

	private void onClickAction(EditText input) {
		inputString=input.getText().toString();
		if(!inputString.equals("")) {
			updateListView();
			if(getCurrentFocus()!=null) {
				InputMethodManager imn = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imn.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
			}
		}
	}
    //TODO : peutetre revoir l'architecture du truc...
    //en tout cas, la modification marche, mais pas si on cherche par catégorie et qu'on modifie la catégorie
	private void updateListView() {
		adapter = new ItemsExpandableAdapter(RechercheActivity.this, new ArrayList<ExpandListGroup>());
		ArrayList<Item> itemList = annalesDAO.selectByKeyWord(inputString);
		if(itemList.size()!=0) {
			errorMessage.setVisibility(View.GONE);
			for (Item item : itemList) {
                Item editedItem = editedAnnalesDAO.selectItem(item);
                if(editedItem!=null) item=editedItem;
				ExpandListGroup group = new ExpandListGroup(item);
				ExpandListChild child = new ExpandListChild(item);
				adapter.addItem(child, group);
			}
			expendableListView.setAdapter(adapter);
		} else {
			errorMessage.setVisibility(View.VISIBLE);
		}
	}
		
	private class ItemsExpandableAdapter extends BaseExpandableListAdapter{

		private Context context;
		private ArrayList<ExpandListGroup> groups;
		public ItemsExpandableAdapter(Context context, ArrayList<ExpandListGroup> groups) {
			this.context = context;
			this.groups = groups;
		}
		
		public void addItem(ExpandListChild item, ExpandListGroup group) {
			if (!groups.contains(group)) {
				groups.add(group);
			}
			int index = groups.indexOf(group);
			ArrayList<ExpandListChild> ch = groups.get(index).getItems();
			ch.add(item);
			groups.get(index).setItems(ch);
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
			return chList.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {return childPosition;}//?????

		@SuppressLint("InflateParams")
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
			ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
			if (view == null) {
				LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = infalInflater.inflate(R.layout.expandlist_child_item, null);
			}
            Item childItem = child.item;
			TextView question = (TextView) view.findViewById(R.id.question);
			question.setText(childItem.getQuestion());
			TextView qcma = (TextView) view.findViewById(R.id.propositionA);
			qcma.setText(childItem.getQcmA());
			TextView qcmb = (TextView) view.findViewById(R.id.propositionB);
			qcmb.setText(childItem.getQcmB());
			TextView qcmc = (TextView) view.findViewById(R.id.propositionC);
			qcmc.setText(childItem.getQcmC());
			TextView qcmd = (TextView) view.findViewById(R.id.propositionD);
			qcmd.setText(childItem.getQcmD());
			TextView qcme = (TextView) view.findViewById(R.id.propositionE);
			qcme.setText(childItem.getQcmE());
			TextView correction = (TextView) view.findViewById(R.id.correction);
			if(afficherCorrection) {
				correction.setText(childItem.getCorrection());
			} else {
				correction.setText("");
			}
			Methodes.hightlightTextView(question, inputString, Color.RED);
			Methodes.hightlightTextView(qcma, inputString, Color.RED);
			Methodes.hightlightTextView(qcmb, inputString, Color.RED);
			Methodes.hightlightTextView(qcmc, inputString, Color.RED);
			Methodes.hightlightTextView(qcmd, inputString, Color.RED);
			Methodes.hightlightTextView(qcme, inputString, Color.RED);
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			ArrayList<ExpandListChild> chList = groups.get(groupPosition).getItems();
			return chList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {return groups.get(groupPosition);}
		@Override
		public int getGroupCount() {return groups.size();}
		@Override
		public long getGroupId(int groupPosition) {return groupPosition;}

		@SuppressLint("InflateParams")
		@Override
		public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
			ExpandListGroup group = (ExpandListGroup) getGroup(groupPosition);
			if (view == null) {
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.expandlist_group_item, null);
			}
			TextView session = (TextView) view.findViewById(R.id.session);
			TextView categorie = (TextView) view.findViewById(R.id.QCM_categorie);
			session.setText(group.getSession());
			categorie.setText(group.getCategorie());
			return view;
		}

		@Override
		public boolean hasStableIds() {return true;}
		@Override
		public boolean isChildSelectable(int arg0, int arg1) {return true;}		
	}

	private class ExpandListChild {

		public Item item;
		
		public ExpandListChild(Item pItem) {
			this.item = pItem;
		}
	}
	
	private class ExpandListGroup {
		 
		public String session;
		public String categorie;
		private ArrayList<ExpandListChild> Items;
		
		public ExpandListGroup(Item item) {
			this.session=item.getSession()+" ("+item.getType()+")";
			this.categorie=item.getCategorie();
		}
		
		public String getSession() {return session;}
		public String getCategorie() {return categorie;}

		public ArrayList<ExpandListChild> getItems() {
			if(Items!=null)
				return Items;
			else
				return new ArrayList<>();
		}
		public void setItems(ArrayList<ExpandListChild> Items) {
			this.Items = Items;
		}
		
		
	}
}