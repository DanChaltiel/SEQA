package com.dan.seqa.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dan.seqa.fragments.StatistiqueSessionFragment;
import com.dan.seqa.fragments.StatistiqueThemeFragment;

import java.util.ArrayList;
import java.util.List;

public class StatsPagerAdapter extends FragmentPagerAdapter {

	private final List<Fragment> fragments;
	
	/**
	 * Constructeur
	 * @return La liste des fragments (implémentant l'interface)
	 */
	public StatsPagerAdapter(FragmentManager fm) {
		super(fm);
		List<Fragment> tmpFragments = new ArrayList<Fragment>();
		
		tmpFragments.add(StatistiqueSessionFragment.newInstance());
		tmpFragments.add(StatistiqueThemeFragment.newInstance());

		this.fragments = tmpFragments;
	}

	
	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	
	@Override
	public String getPageTitle(int i) {
		switch (i) {
		case 0:
			return "Par Session";
		case 1:
			return "Par Thème";
		default://erreur
			return null;
		}	
	}
}