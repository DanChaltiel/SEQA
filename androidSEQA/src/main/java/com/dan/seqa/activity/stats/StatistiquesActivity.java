package com.dan.seqa.activity.stats;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;

import com.dan.newseqa.R;
import com.dan.seqa.activity.AbstractActivity;
import com.dan.seqa.adapters.StatsPagerAdapter;

public class StatistiquesActivity extends AbstractActivity implements TabListener{
	//CONSTANTES
	public final static String EXTRA_NOM_SESSION = "com.dan.seqa.nomSession";
	//VARIABLES
	private StatsPagerAdapter mPagerAdapter;
	private ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(getThemeFromPreferences(this));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.statistiques_viewpager);

	    final ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    mPagerAdapter = new StatsPagerAdapter(getSupportFragmentManager());
		pager = (ViewPager) super.findViewById(R.id.statistiquesViewpager);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {//0 = session, 1 = themes
	            actionBar.setSelectedNavigationItem(position);
			}
			/*override :
			 * public abstract void onPageScrollStateChanged(int state): Appelé lorsque l'état du défilement change.
			 * public abstract void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) : Appelé lorsque la page courante est en train d'être modifié.
			 * public abstract void onPageSelected(int position) : Appelé lorsqu'une nouvelle page est sélectionnée.
			 */
			
		});
		// Pour chaque fragment dans le pager, on ajoute un onglet et on le met sous écoute
	    for (int i = 0; i < mPagerAdapter.getCount(); i++) {
	        actionBar.addTab(actionBar.newTab().setText(mPagerAdapter.getPageTitle(i)).setTabListener(this));
	    }
		pager.setAdapter(this.mPagerAdapter);
	}
	

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    pager.setCurrentItem(tab.getPosition());
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	
}