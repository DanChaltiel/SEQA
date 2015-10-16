package com.dan.seqa.bdd;

import android.app.Activity;
import android.content.res.AssetManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dan.seqa.outils.Methodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFeeder{

	//TODO traduire en SQLite 3.7 quand HoneyCoombs n'existera plus... (bientot...)
        //SQLite 3.7.4 et SQLite 3.6.22 avant HoneyCoombs (2.3.x et avant)


    /**
     * Teste si chaque fichier sql des assets est bien entré dans la base de données
     * @param activity pour le dao et les assets
     * @return la liste des assets qui manquent à la base de données
     */
    public static List<String> test(Activity activity){
        Long debut = System.currentTimeMillis();

        AnnalesDAO dao = new AnnalesDAO(activity);
        int databaseCount = dao.getCount(AnnalesDAO.TABLE_NAME);
        AssetManager mgr = activity.getAssets();
        Methodes.alert("databaseCount="+ databaseCount);
        List<String> assetToRead = new ArrayList<>();
        try {
            for (String session : mgr.list("sessions")) {
                session = session.replace(".sql", "");
                if(!dao.sessionExists(session))
                    assetToRead.add(session);
            }
        } catch (IOException e) {
            Methodes.alert(e.getMessage());
        }

        Methodes.alert("Check de la base de donnée : OK (temps="+(System.currentTimeMillis()-debut)+"ms, count="+ databaseCount +")");
        return assetToRead;
    }

    /**
     * Lit un fichier sql dans les assets et l'execute dans la BDD
     * @param activity pour le dao et les assets
     * @param assetFile le nom du fichier sql issu des assets
     */
    public static void feed(Activity activity, String assetFile){
        AnnalesDAO dao = new AnnalesDAO(activity);
        AssetManager mgr = activity.getAssets();
        Long debut3 = System.currentTimeMillis();
        try {
            InputStream	in = mgr.open(assetFile);
            String[] statements = FileHelper.parseSqlFile(in);
            dao.mDb.execSQL("BEGIN TRANSACTION;");
            for (String statement : statements) {
                dao.mDb.execSQL(statement);
            }
            dao.mDb.execSQL("COMMIT;");
            Methodes.alert("Execution du fichier " + assetFile + " : " + statements.length + " instructions exécutées en " + (System.currentTimeMillis() - debut3) + " ms");
            in.close();
        } catch (IOException e) {
            Methodes.alert(e.getMessage());
        }
    }
}