package com.dan.seqa.outils;

import android.os.Build;

public enum Codenames
{
	NON_REFERENCE,
	BASE, BASE_1,
	CUPCAKE, 
	DONUT, 
	ECLAIR, ECLAIR_MR1, ECLAIR_MR2, 
	FROYO,
	GINGERBREAD, GINGERBREAD_MR1, 
	HONEYCOMB, HONEYCOMB_MR1, HONEYCOMB_MR2,
	ICE_CREAM_SANDWICH, ICE_CREAM_SANDWICH_MR1,
	JELLY_BEAN, 
	KITKAT,
	LOLLIPOP,
	MARSHMALLOW,
	CUR_DEVELOPMENT;

	public static Codenames getCodename()
	{
		int api = Build.VERSION.SDK_INT;

		switch (api) {
			case Build.VERSION_CODES.BASE: 		return BASE;
			case Build.VERSION_CODES.BASE_1_1: 	return BASE_1;
			case Build.VERSION_CODES.CUPCAKE: 	return CUPCAKE;
			case Build.VERSION_CODES.DONUT: 	return DONUT;
			case Build.VERSION_CODES.ECLAIR: case Build.VERSION_CODES.ECLAIR_0_1: case Build.VERSION_CODES.ECLAIR_MR1: 	//return ECLAIR_MR1;
												return ECLAIR;
			case Build.VERSION_CODES.FROYO: 	return FROYO;
			case Build.VERSION_CODES.GINGERBREAD: case Build.VERSION_CODES.GINGERBREAD_MR1: 	
												return GINGERBREAD;
			case Build.VERSION_CODES.HONEYCOMB: case Build.VERSION_CODES.HONEYCOMB_MR1: case Build.VERSION_CODES.HONEYCOMB_MR2: 	//return HONEYCOMB_MR1; //return HONEYCOMB_MR2;
												return HONEYCOMB;
			case Build.VERSION_CODES.ICE_CREAM_SANDWICH: case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:	//return ICE_CREAM_SANDWICH_MR1;
												return ICE_CREAM_SANDWICH;			 	
			case Build.VERSION_CODES.JELLY_BEAN: case Build.VERSION_CODES.JELLY_BEAN_MR1:	case Build.VERSION_CODES.JELLY_BEAN_MR2:	
												return JELLY_BEAN;
			case Build.VERSION_CODES.KITKAT: 	return KITKAT;
			case Build.VERSION_CODES.LOLLIPOP: case Build.VERSION_CODES.LOLLIPOP_MR1:
												return LOLLIPOP;
			case Build.VERSION_CODES.M: 		return MARSHMALLOW;
			case Build.VERSION_CODES.CUR_DEVELOPMENT: 	return CUR_DEVELOPMENT;
			
			default: 	return NON_REFERENCE;
		}
	}

}