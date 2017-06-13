package netflix.weisberg.com.br.moviesnetflix.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import netflix.weisberg.com.br.moviesnetflix.R;
import netflix.weisberg.com.br.moviesnetflix.model.Movie;

public class ListaMoviesFavoritosFragment extends Fragment implements MovieAdapter.AoClicarNoMovieListener{

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    MovieDb mMovieDb;
    List<Movie> mMovies;
    Bus mBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBus = ((MovieApp)getActivity().getApplication()).getBus();
        mBus.register(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mBus.unregister(this);
        mBus = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.lista_movies, container, false);

        ButterKnife.bind(this, v);
        mSwipe.setEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        }
        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mMovieDb = new MovieDb(getActivity());
        if(mMovies == null){
            mMovies = mMovieDb.getMovies();
        }
        atualizarLista();
    }

    private void atualizarLista() {
        Movie[] array = new Movie[mMovies.size()];
        mMovies.toArray(array);
        MovieAdapter adapter = new MovieAdapter(getActivity(), array);
        adapter.setAoClicarNoMovieListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void aoClicarNoMovie(View v, int position, Movie movie) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                Pair.create(v.findViewById(R.id.poster), "poster"),
                Pair.create(v.findViewById(R.id.txtTitulo), "showTitle"),
                Pair.create(v.findViewById(R.id.txtAno), "releaseYear")
        );
        Intent intent = new Intent(getActivity(), DetalheActivity.class);
        intent.putExtra(DetalheActivity.EXTRA_MOVIE, movie);
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    @Subscribe
    public void atualizarLista(MovieEvento event){
        mMovies = mMovieDb.getMovies();
        atualizarLista();
    }

}
