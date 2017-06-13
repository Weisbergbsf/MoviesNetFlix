package netflix.weisberg.com.br.moviesnetflix.ui;

import android.provider.BaseColumns;

public interface MovieContract  extends BaseColumns{

    String TABELA_MOVIE = "movies";
    String COL_MOVIE_ID = "movie_id";
    String COL_TITULO = "showTitle";
    String COL_ANO = "releaseYear";
    String COL_DIRETOR = "diretor";
    String COL_POSTER = "poster";
    String COL_SUMMARY = "summary";
    String COL_CAST = "cast";

}
