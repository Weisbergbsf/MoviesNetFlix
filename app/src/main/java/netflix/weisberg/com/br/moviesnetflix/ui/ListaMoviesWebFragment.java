package netflix.weisberg.com.br.moviesnetflix.ui;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import netflix.weisberg.com.br.moviesnetflix.R;
import netflix.weisberg.com.br.moviesnetflix.http.MovieHttp;
import netflix.weisberg.com.br.moviesnetflix.model.Movie;

public class ListaMoviesWebFragment extends Fragment implements MovieAdapter.AoClicarNoMovieListener, SearchView.OnQueryTextListener{

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    Movie[] mMovies;

    public ListaMoviesWebFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lista_movies, container, false);
        ButterKnife.bind(this, v);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        if(mMovies != null) {
            atualizarLista();
        }
    }

    @Override
    public void aoClicarNoMovie(View v, int position, Movie movie) {

        ActivityOptionsCompat options =  ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        Pair.create(v.findViewById(R.id.poster), "poster"),
                        Pair.create(v.findViewById(R.id.txtTitulo), "showTitle"),
                        Pair.create(v.findViewById(R.id.txtAno), "releaseYear")
                );
        Intent it = new Intent(getActivity(), DetalheActivity.class);
        it.putExtra(DetalheActivity.EXTRA_MOVIE, movie);
        ActivityCompat.startActivity(getActivity(), it, options.toBundle());
    }

    private void atualizarLista(){
        MovieAdapter adapter = new MovieAdapter(getActivity(), mMovies);
        adapter.setAoClicarNoMovieListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.length() < 5){
            Toast.makeText(getActivity(), R.string.msg_erro_texto_consulta, Toast.LENGTH_LONG).show();
        }else {
            new MovieSearchTask().execute(query);
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    class MovieSearchTask extends AsyncTask<String, Void, Movie[]>{

        @Override
        protected Movie[] doInBackground(String... params) {
            try {
                return  MovieHttp.obterDiscosDoServidor(params[0]);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies){
            super.onPostExecute(movies);
            mSwipe.setRefreshing(false);
            if(movies != null){
                mMovies = movies;
                atualizarLista();
            }else{
                Toast.makeText(getContext(), R.string.msg_nenhum_resultado, Toast.LENGTH_SHORT).show();

            }
        }

    }

}
