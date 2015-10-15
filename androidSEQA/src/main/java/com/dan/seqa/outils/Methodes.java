package com.dan.seqa.outils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Methodes {

	private Methodes(){/*on déclare un constructeur vide en private pour emp�cher les instanciations*/}

	/**
	 * 
	 * @param is : getAssets().open("file_name.json");
	 * @param encodage : "UTF-8"
	 * @return
	 */
	public static String loadJSONFromAsset(InputStream is, String encodage) {
		String json = null;
		try {
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, encodage);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;

	}


	/**
	 * @author Tom Esterez (http://stackoverflow.com/questions/4946295/android-expand-collapse-animation/13381228#13381228)
	 * Pour créer un accordéon
	 */
	public static void expand(final View v) {
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);
		Animation a = new Animation()
		{
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				v.getLayoutParams().height = interpolatedTime == 1 ? 
						LayoutParams.WRAP_CONTENT : 
							(int)(targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		// 1dp/ms
		a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}
	/**
	 * @author Tom Esterez (http://stackoverflow.com/questions/4946295/android-expand-collapse-animation/13381228#13381228)
	 * Pour créer un accordéon
	 */
	public static void collapse(final View v) {
		final int initialHeight = v.getMeasuredHeight();
		Animation a = new Animation() {
			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				} else{
					v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
					v.requestLayout();
				}
			}
			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		// 1dp/ms
		a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
		v.startAnimation(a);
	}

	public static String capitalize(String input) {
		switch (input.length()) {
		case 0:
			return "";
		case 1:
			return input.toUpperCase(Locale.getDefault());
		default:
			return input.substring(0, 1).toUpperCase(Locale.getDefault()) + input.substring(1);
		}
	}

	/**
	 * Pour faire un linearLayout multiligne à partir d'un array de vues
	 * @param linearLayout
	 * @param views : The views to wrap within LinearLayout
	 * @param context
	 * @param extraView : An extra view that may be to the right or left of your LinearLayout. (null sinon)
	 * @author Karim Varela --> http://stackoverflow.com/questions/6996837/android-multi-line-linear-layout
	 **/
	public static void populateViews(LinearLayout linearLayout, View[] views, Activity context, View extraView)
	{
		int extraviewPadding=0;
		if(extraView!=null) {
			extraView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			extraviewPadding=extraView.getMeasuredWidth();
		}
		// kv : May need to replace 'getSherlockActivity()' with 'this' or 'getActivity()'
		Display display = context.getWindowManager().getDefaultDisplay();
		linearLayout.removeAllViews();
		@SuppressWarnings("deprecation")
		int maxWidth = display.getWidth() - extraviewPadding - 20;

		linearLayout.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams params;
		LinearLayout newLL = new LinearLayout(context);
		newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		newLL.setGravity(Gravity.CENTER);
		newLL.setOrientation(LinearLayout.HORIZONTAL);

		int widthSoFar = 0;

		for (View view : views) {
			LinearLayout LL = new LinearLayout(context);
			LL.setOrientation(LinearLayout.HORIZONTAL);
			LL.setGravity(Gravity.CENTER | Gravity.BOTTOM);
			LL.setLayoutParams(new ListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			view.measure(0, 0);
			params = new LinearLayout.LayoutParams(view.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
			params.setMargins(5, 0, 5, 0);

			LL.addView(view, params);
			LL.measure(0, 0);
			widthSoFar += view.getMeasuredWidth();
			if (widthSoFar >= maxWidth) {
				linearLayout.addView(newLL);

				newLL = new LinearLayout(context);
				newLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				newLL.setOrientation(LinearLayout.HORIZONTAL);
				newLL.setGravity(Gravity.CENTER);
				params = new LinearLayout.LayoutParams(LL.getMeasuredWidth(), LL.getMeasuredHeight());
				newLL.addView(LL, params);
				widthSoFar = LL.getMeasuredWidth();
			} else {
				newLL.addView(LL);
			}
		}
		linearLayout.addView(newLL);
	}

	/**
	 * Computes the widest view in an adapter, best used when you need to wrap_content on a ListView, please be careful
	 * and don't use it on an adapter that is extremely numerous in items or it will take a long time.
	 *
	 * @param context Some context
	 * @param adapter The adapter to process
	 * @return The pixel width of the widest View
	 */
	public static int getWidestView(Context context, Adapter adapter) {
		int maxWidth = 0;
		View view = null;
		FrameLayout fakeParent = new FrameLayout(context);
		for (int i=0, count=adapter.getCount(); i<count; i++) {
			view = adapter.getView(i, view, fakeParent);
			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			int width = view.getMeasuredWidth();
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	public static String formatDates(long timestamp) {		
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp*1000);
		String jour = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" : "") + cal.get(Calendar.DAY_OF_MONTH),
				mois = ((cal.get(Calendar.MONTH) < 10) ? "0" : "") + cal.get(Calendar.MONTH);
		return jour+"/"+mois+"/"+cal.get(Calendar.YEAR);
	}

	/** Case insensitive, sinon switcher les commentaires*/
	public static void hightlightTextView(TextView prose, String text, int color) {
		text=text.trim();
		Spannable raw=new SpannableString((prose.getText()));
		//		int index=TextUtils.indexOf(raw, text);
		int index=TextUtils.indexOf(prose.getText().toString().toLowerCase(Locale.getDefault()), text);
		while (index >= 0) {
			raw.setSpan(new BackgroundColorSpan(color), index, index
					+ text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			index=TextUtils.indexOf(raw, text, index + text.length());
		}
		prose.setText(raw);
	}

	public static String getAdminInfos(Context ctx){
		String resolution="";
		switch (ctx.getResources().getDisplayMetrics().densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			resolution= "ldpi (Low Density)"; break;
		case DisplayMetrics.DENSITY_MEDIUM:
			resolution= "mdpi (Medium Density)"; break;
		case DisplayMetrics.DENSITY_HIGH:
			resolution= "hdpi (High Density)"; break;
		case DisplayMetrics.DENSITY_XHIGH:
			resolution= "xhdpi (XHigh Density)"; break;
		case DisplayMetrics.DENSITY_XXHIGH:
			resolution= "xxhdpi (XXHigh Density)"; break;
		case DisplayMetrics.DENSITY_TV:
			resolution= "television (TV Density)"; break;
		default:
			resolution= "Densité d'écran inconnue"; break;
		}

		String aboveVersionsInfos="";
		try{
			aboveVersionsInfos= " \nSERIAL : "+android.os.Build.SERIAL
					+" \nHARDWARE : "+android.os.Build.HARDWARE
					+" \nBOOTLOADER : "+android.os.Build.BOOTLOADER
					+" \nUNKNOWN : "+android.os.Build.UNKNOWN;
		} catch (NoSuchFieldError e){
			Methodes.alert("android.os.Build --> NoSuchFieldError (Android 2.1 ?)");
		}
		return "CODENAME : "+ android.os.Build.VERSION.CODENAME+
				" \nINCREMENTAL : "+android.os.Build.VERSION.INCREMENTAL+
				" \nRELEASE : "+android.os.Build.VERSION.RELEASE+
				" \nSDK_INT : "+android.os.Build.VERSION.SDK_INT+
				" \nVERSION_CODENAME : "+Codenames.getCodename()+
				" \nRÉSOLUTION D'ÉCRAN : "+resolution+
				" \n"+
				" \nBOARD : "+android.os.Build.BOARD+
				" \nBRAND : "+android.os.Build.BRAND+
				" \nDEVICE : "+android.os.Build.DEVICE+
				" \nDISPLAY : "+android.os.Build.DISPLAY+
				" \nFINGERPRINT : "+android.os.Build.FINGERPRINT+
				" \nHOST : "+android.os.Build.HOST+
				" \nID : "+android.os.Build.ID+
				" \nMANUFACTURER : "+android.os.Build.MANUFACTURER+
				" \nMODEL : "+android.os.Build.MODEL+
				" \nPRODUCT : "+android.os.Build.PRODUCT+
				" \nTAGS : "+android.os.Build.TAGS+
				" \nTIME : "+android.os.Build.TIME+
				" \nTYPE : "+android.os.Build.TYPE+
				" \nUSER : "+android.os.Build.USER+
				" \n"+
				//" \ngetRadioVersion : "+android.os.Build.getRadioVersion()+ // API level 14...
				""+aboveVersionsInfos; //pour les anté gingerbread mais post éclair
	}


	public static void testReseauxSociaux(Activity caller){
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		List<ResolveInfo> resInfo = caller.getPackageManager().queryIntentActivities(shareIntent, 0);
		if (!resInfo.isEmpty()){
			for (ResolveInfo resolveInfo : resInfo) {
				String packageName = resolveInfo.activityInfo.packageName;
				Intent targetedShareIntent = new Intent(Intent.ACTION_SEND);
				targetedShareIntent.setType("text/plain");
				targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, "subject to be shared");
				if (packageName.equals("com.facebook.katana")){
					targetedShareIntent.putExtra(Intent.EXTRA_TEXT, "http://link-to-be-shared.com");
				}else{
					//targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "text message to shared");
				}

				targetedShareIntent.setPackage(packageName);
				targetedShareIntents.add(targetedShareIntent);
			}
			Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Select app to share");

			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

			caller.startActivity(chooserIntent);
		}
	}

	public static List<String> listPreferences(Context ctx) {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		Set<?>  allPref = preferences.getAll().entrySet();
		List<String> tmpList = new ArrayList<String>();
		for (Object  object : allPref) {
			@SuppressWarnings("unchecked")
			Entry<String, ?> tmp = (Entry<String, ?>)object;
			tmpList.add(tmp.getKey()+" -> "+tmp.getValue());
		}
		Collections.sort(tmpList);//Bon bah pas de moyen plus clair pour ranger un set<?>...
		return tmpList;
	}

	/**
	 * Arrondit un Double à n chiffres après la virgule
	 */
	public static double arrondir(double value, int n) {
		return (Math.round(value * Math.pow(10, n))) / (Math.pow(10, n));
	}

	public static void reload(Activity activity) {

		Intent intent = activity.getIntent();
		activity.overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		activity.finish();

		activity.overridePendingTransition(0, 0);
		activity.startActivity(intent);
	}

	public static void alert(String message){
		String classe = ((new Exception().getStackTrace())[1]).getClassName();
		classe = classe.replaceAll("fr\\.pharmadescartes\\.champignons\\..*\\.", ""); //.getClass().getSimpleName() ne marche pas
		int line = ((new Exception().getStackTrace())[1]).getLineNumber();
		//Log.e("Debug : "+classe+" l."+String.valueOf(line), message);
		Log.e(classe+" l."+String.valueOf(line), message);
	}

	/**
	 * Renverse l'ordre de tri de l'array
	 * @param array
	 * @return
	 */
	public static Object[] reverseArray(Object[] array){
		for(int i = 0; i < array.length/2; i++)
		{
			Object temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
		return array;
	}
	/*
	 * M�thodes statiques utilis�es dans les Classes
	 */
	/**
	 * Lance un Chooser proposant diverses applications pour envoyer le message,
	 * pas forc�ment les plus adapt�es
	 * @param ctx l'activit� propri�taire du lancement
	 * @param adresse l'adresse e-mail
	 * @param sujet	le sujet du message
	 * @param message le corps du message
	 * @see Methodes#emailSendTo
	 */
	public static void emailSend(Context ctx, String adresse, String sujet, String message){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		//emailIntent.setType("text/html");
		//emailIntent.setType("text/plain");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { adresse, ""});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, sujet);
		emailIntent.putExtra(Intent.EXTRA_TITLE, "huh ?");
		emailIntent.putExtra(Intent.EXTRA_TEXT, message);
		//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, Html.fromHtml("<b>Je suis le html du message</b>"));
		ctx.startActivity(Intent.createChooser(emailIntent, "Avec quelle application envoyer cet e-mail ?"));
	}

	/**
	 * Lance directement l'application mail par d�faut, mais ne permet pas
	 * d'ins�rer le symbole "+" dans le sujet ou le corps pr�d�finis du message
	 * @param ctx l'activit� propri�taire du lancement
	 * @param adresse l'adresse e-mail
	 * @param sujet	le sujet du message
	 * @param message le corps du message
	 * @see Methodes#emailSend
	 */
	public static void emailSendTo(Context ctx, String adresse, String sujet, String message){
		try {
			String uriText = "mailto:" + adresse +
					"?subject=" + URLEncoder.encode(sujet, "UTF-8").replace("+", "%20") +
					"&body=" + URLEncoder.encode(message, "UTF-8").replace("+", "%20");
			Uri uri = Uri.parse(uriText);
			Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
			//sendIntent.setDataAndNormalize(uri); API 16...
			sendIntent.setData(uri);
			ctx.startActivity(Intent.createChooser(sendIntent, "Send email"));
		} catch (Exception e) {//enfin une exception sur du encode utf8 c'est quasiment impossible apparemment...
			Methodes.alert(e.getMessage());
		}
	}


	/**
	 * Paramterized method to sort Map e.g. HashMap or Hashtable in Java
	 * throw NullPointerException if Map contains null key
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
		List<K> keys = new LinkedList<K>(map.keySet());
		Collections.sort(keys);
		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K,V> sortedMap = new LinkedHashMap<K,V>();
		for(K key: keys){
			sortedMap.put(key, map.get(key));
		}
		return sortedMap;
	}

	/**
	 * Java method to sort Map in Java by value e.g. HashMap or Hashtable
	 * throw NullPointerException if Map contains null values
	 * It also sort values even if they are duplicates
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByValues(Map<K,V> map){
		List<Entry<K,V>> entries = new LinkedList<Entry<K,V>>(map.entrySet());
		Collections.sort(entries, new Comparator<Entry<K,V>>() {
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K,V> sortedMap = new LinkedHashMap<K,V>();
		for(Entry<K,V> entry: entries){
			sortedMap.put(entry.getKey(), entry.getValue());
		}      
		return sortedMap;
	}


}
