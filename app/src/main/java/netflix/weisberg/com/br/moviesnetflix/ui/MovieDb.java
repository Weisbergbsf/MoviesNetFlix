package netflix.weisberg.com.br.moviesnetflix.ui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import netflix.weisberg.com.br.moviesnetflix.model.Movie;

public class MovieDb {

    private MovieDbHelper mDbHelper;

    public MovieDb(Context context){
        this.mDbHelper = new MovieDbHelper(context.getApplicationContext());
    }

    public void inserir(Movie movie){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try{
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.COL_TITULO, movie.showTitle);
            cv.put(MovieContract.COL_ANO, movie.releaseYear);
            cv.put(MovieContract.COL_DIRETOR, movie.diretor);
            cv.put(MovieContract.COL_POSTER, movie.poster);
            cv.put(MovieContract.COL_SUMMARY, movie.summary);
            cv.put(MovieContract.COL_CAST, movie.cast);

            db.beginTransaction();
            db.insert(MovieContract.TABELA_MOVIE, null, cv);
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
        db.close();
    }

    public boolean favorito(Movie movie){
        boolean existe;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " +MovieContract._ID+
                " FROM " +MovieContract.TABELA_MOVIE+
                " WHERE " + MovieContract.COL_TITULO+ " = ? ", new String[]{movie.showTitle}
        );
        existe = cursor.getCount() > 0;
        db.close();

        return existe;
    }

    public void excluir(Movie movie){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(MovieContract.TABELA_MOVIE,
                MovieContract.COL_TITULO+ "= ?", new String[]{movie.showTitle});
        db.close();
    }

    public List<Movie> getMovies(){
        List<Movie> movies = new ArrayList<>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursorMovies = db.rawQuery("SELECT * FROM "+ MovieContract.TABELA_MOVIE + " ORDER BY "+
                MovieContract.COL_ANO, null);

        while (cursorMovies.moveToNext()){
            Movie movie = new Movie();

            movie.showTitle = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COL_TITULO));
            movie.releaseYear = cursorMovies.getInt(cursorMovies.getColumnIndex(MovieContract.COL_ANO));
            movie.diretor = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COL_DIRETOR));
            movie.poster = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COL_POSTER));
            movie.summary = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COL_SUMMARY));
            movie.cast = cursorMovies.getString(cursorMovies.getColumnIndex(MovieContract.COL_CAST));
            movies.add(movie);

        }

        cursorMovies.close();
        db.close();
        return movies;
    }

}
