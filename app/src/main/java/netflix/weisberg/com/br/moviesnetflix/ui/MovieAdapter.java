package netflix.weisberg.com.br.moviesnetflix.ui;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import netflix.weisberg.com.br.moviesnetflix.R;
import netflix.weisberg.com.br.moviesnetflix.http.MovieHttp;
import netflix.weisberg.com.br.moviesnetflix.model.Movie;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context mContext;
    private Movie[] mMovies;
    private AoClicarNoMovieListener mListener;

    public MovieAdapter(Context ctx, Movie[] movies){
        mContext = ctx;
        mMovies = movies;
    }

    public void setAoClicarNoMovieListener(AoClicarNoMovieListener l) {
        mListener = l;
    }

    @Override
    public int getItemCount() {
        return mMovies != null ? mMovies.length : 0;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        MovieViewHolder vh = new MovieViewHolder(v);
        v.setTag(vh);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    MovieViewHolder vh = (MovieViewHolder)view.getTag();
                    int position = vh.getAdapterPosition();
                    mListener.aoClicarNoMovie(view, position, mMovies[position]);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = mMovies[position];
        Picasso.with(mContext).load(MovieHttp.BASE_URL + movie.poster).into(holder.poster);
        holder.txtTitulo.setText(movie.showTitle);
        holder.txtAno.setText(String.valueOf(movie.releaseYear));

    }

    public interface AoClicarNoMovieListener{
        void aoClicarNoMovie(View v, int position, Movie movie);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.poster)
        public ImageView poster;
        @Bind(R.id.txtTitulo)
        public TextView txtTitulo;
        @Bind(R.id.txtAno)
        public TextView txtAno;

        public MovieViewHolder(View parent) {
            super(parent);
            ButterKnife.bind(this, parent);
            ViewCompat.setTransitionName(poster, "poster");
            ViewCompat.setTransitionName(txtTitulo, "showTitle");
            ViewCompat.setTransitionName(txtAno, "releaseYear");
        }


    }

}
