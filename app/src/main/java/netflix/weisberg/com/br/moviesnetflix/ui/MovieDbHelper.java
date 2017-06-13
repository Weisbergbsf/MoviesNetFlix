package netflix.weisberg.com.br.moviesnetflix.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "moviesDB";
    public static final int DB_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ MovieContract.TABELA_MOVIE+"("+
                        MovieContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        MovieContract.COL_TITULO +" TEXT NOT NULL UNIQUE, "+
                        MovieContract.COL_ANO +" INTEGER, "+
                        MovieContract.COL_DIRETOR +" TEXT, "+
                        MovieContract.COL_POSTER +" TEXT, "+
                        MovieContract.COL_SUMMARY +" TEXT, "+
                        MovieContract.COL_CAST +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
