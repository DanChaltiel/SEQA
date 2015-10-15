package com.dan.seqa.adapters;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dan.newseqa.R;
import com.dan.seqa.modeles.StatModel;

import java.util.List;

public class StatsListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<StatModel> mStatList;
	private String statsTimeChoice;

	/**
	 * Adaptateur de liste de statistiques
	 * @param ctx pour l'inflation
	 * @param pStatList 
	 */
	public StatsListAdapter(Activity ctx, List<StatModel> pStatList) {
		statsTimeChoice = PreferenceManager.getDefaultSharedPreferences(ctx).getString("statsTimeChoice", "mois");
		mInflater=ctx.getLayoutInflater();
		mStatList=pStatList;
	}

	@Override
	public int getCount() {
		return mStatList.size();
	}

	@Override
	public Object getItem(int position) {
		return mStatList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;//utile ?
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null) {// Si la vue n'est pas recyclée
			convertView = mInflater.inflate(R.layout.statistiques_listitem, null);
			holder = new ViewHolder();
			holder.mSession = (TextView) convertView.findViewById(R.id.itemStatSession);
			holder.mBestScore = (TextView) convertView.findViewById(R.id.itemStatBestScore);
			holder.mMoyenneSemaine = (TextView) convertView.findViewById(R.id.itemStatMoyenneSemaine);
			holder.mEssaisSemaine = (TextView) convertView.findViewById(R.id.itemStatEssaisSemaine);
			holder.mEssaisTotal = (TextView) convertView.findViewById(R.id.itemStatEssaisTotal);
			holder.mRatingbar = (RatingBar) convertView.findViewById(R.id.itemStatRatingBar);
			convertView.setTag(holder);
		} else {// Sinon si on recycle la vue, on récupère son holder en tag			
			holder = (ViewHolder)convertView.getTag();
		}

		// Dans tous les cas, on récupère l'item. S'il existe vraiment, on place les informations dans le holder
		StatModel tmpStatModel = (StatModel)getItem(position);
		if(tmpStatModel != null) {
			holder.mSession.setText(tmpStatModel.session);
			String finEssaisTotal = tmpStatModel.nombreEssaisTotal==1?" essai)":" essais)";
			String finEssaisSemaine = tmpStatModel.nombreEssaisSemaine==1?" essai)":" essais)";
			if(tmpStatModel.meilleurScore != null) {
				String str="";
				if(statsTimeChoice.equals("semaine"))
					str="de la semaine";
				else if(statsTimeChoice.equals("mois"))
					str="du mois";
				else if(statsTimeChoice.equals("an"))
					str="de l'année";
				holder.mBestScore.setText		("Meilleur note : "+tmpStatModel.meilleurScore);
				holder.mMoyenneSemaine.setText	("Moyenne "+str+" : "+tmpStatModel.moyenneSemaine);
				holder.mEssaisTotal.setText		("(sur "+tmpStatModel.nombreEssaisTotal+finEssaisTotal);
				holder.mEssaisSemaine.setText	("(sur "+tmpStatModel.nombreEssaisSemaine+finEssaisSemaine);
				holder.mRatingbar.setRating		(tmpStatModel.rating);
				holder.mMoyenneSemaine.setVisibility(View.VISIBLE);
				holder.mRatingbar.setVisibility(View.VISIBLE);
				holder.mEssaisSemaine.setVisibility(View.VISIBLE);
				holder.mEssaisTotal.setVisibility(View.VISIBLE);
				convertView.setClickable(false); //WTF !!!
			} else{
				holder.mBestScore.setText("Pas encore essayé");
				holder.mMoyenneSemaine.setVisibility(View.GONE);
				holder.mRatingbar.setVisibility(View.GONE);
				holder.mEssaisSemaine.setVisibility(View.GONE);
				holder.mEssaisTotal.setVisibility(View.GONE);
				convertView.setClickable(true);
			}
		}		
		return convertView;
	}

	public String getNomSession(int position) {
		return mStatList.get(position).session;
	}
	
	static class ViewHolder {
		public TextView mSession;
		public TextView mBestScore;
		public TextView mMoyenneSemaine;
		public TextView mEssaisSemaine;
		public TextView mEssaisTotal;
		public RatingBar mRatingbar;
	}
}
