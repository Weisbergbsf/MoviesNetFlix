package netflix.weisberg.com.br.moviesnetflix.ui;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import netflix.weisberg.com.br.moviesnetflix.R;
import netflix.weisberg.com.br.moviesnetflix.http.MovieHttp;
import netflix.weisberg.com.br.moviesnetflix.model.Movie;

public class ListaMoviesWebFragment extends Fragment implements MovieAdapter.AoClicarNoMovieListener{

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipe;
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    Movie[] mMovies;
    MoviesDownloadTask mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lista_movies, container, false);
        ButterKnife.bind(this, v);
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTask = new MoviesDownloadTask();
                mTask.execute();
            }
        });
        mRecyclerView.setTag("web");
        mRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        if(mMovies == null){
            if(mTask == null){
                mTask = new MoviesDownloadTask();
                mTask.execute();
            }else if(mTask.getStatus() == AsyncTask.Status.RUNNING){
                exibirProgresso();
            }
        }else{
            atualizarLista();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mTask != null) mTask.cancel(true);
    }

    @Override
    public void aoClicarNoMovie(View v, int position, Movie movie) {
//        ActivityOptionsCompat options =  ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        getActivity(),
//                        Pair.create(v.findViewById(R.id.imgCapa), "capa"),
//                        Pair.create(v.findViewById(R.id.txtTitulo), "titulo"),
//                        Pair.create(v.findViewById(R.id.txtAno), "ano")
//                );
//        Intent it = new Intent(getActivity(), DetalheActivity.class);
//        it.putExtra(DetalheActivity.EXTRA_DISCO, disco);
//        ActivityCompat.startActivity(getActivity(), it, options.toBundle());
    }



    private void atualizarLista(){
        MovieAdapter adapter = new MovieAdapter(getActivity(), mMovies);
        adapter.setAoClicarNoMovieListener(this);
        mRecyclerView.setAdapter(adapter);
    }

    private void exibirProgresso(){
        mSwipe.post(new Runnable() {
            @Override
            public void run() {
                mSwipe.setRefreshing(true);
            }
        });
    }

    class MoviesDownloadTask extends AsyncTask<Void, Void, Movie[]>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            exibirProgresso();
        }

        @Override
        protected Movie[] doInBackground(Void... params) {
            return MovieHttp.obterDiscosDoServidor();
        }

        @Override
        protected void onPostExecute(Movie[] movies){
            super.onPostExecute(movies);
            mSwipe.setRefreshing(false);
            if(movies != null){
                mMovies = movies;
                atualizarLista();
            }
        }
    }
}
