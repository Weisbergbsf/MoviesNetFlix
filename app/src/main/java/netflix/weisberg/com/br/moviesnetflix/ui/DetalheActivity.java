package netflix.weisberg.com.br.moviesnetflix.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import netflix.weisberg.com.br.moviesnetflix.R;
import netflix.weisberg.com.br.moviesnetflix.model.Movie;

public class DetalheActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";

    @Bind(R.id.fabFavorito)
    FloatingActionButton mFabFavorito;
    @Bind(R.id.poster)
    ImageView mImgPoster;
    @Bind(R.id.txtTitulo)
    TextView mTxtTitulo;
    @Bind(R.id.txtAno)
    TextView mTxtAno;
    @Bind(R.id.txtDiretor)
    TextView mTxtDiretor;
    @Bind(R.id.txtElenco)
    TextView mTxtElenco;
    @Bind(R.id.txtSummary)
    TextView mTxtSummary;

    @Nullable
    @Bind(R.id.coordinator)
    CoordinatorLayout mCoordinator;
    @Nullable
    @Bind(R.id.appBar)
    AppBarLayout mAppBar;
    @Nullable
    @Bind(R.id.collapseToolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    Target mPicassoTarget;
    MovieDb mMovieDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);
        ButterKnife.bind(this);

        Movie movie = (Movie)getIntent().getSerializableExtra(EXTRA_MOVIE);
        preencherCampos(movie);
        configurarBarraDeTitulo(movie.showTitle);
        carregarPoster(movie);
        configurarAnimacaoEntrada();
        mMovieDb = new MovieDb(this);
        configurarFab(movie);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, compartilharConteudoDoFilme());
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.opcoes_compartilhamento)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent it = super.getParentActivityIntent();
        if(it != null){
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return it;
    }

    private String compartilharConteudoDoFilme()  {
        Movie movie = (Movie)getIntent().getSerializableExtra(EXTRA_MOVIE);
        return getString(R.string.texto_compartilhar, movie.showTitle, movie.poster);
    }

    private void configurarFab(final Movie movie) {
        boolean  favorito = mMovieDb.favorito(movie);
        mFabFavorito.setImageDrawable(getFabIcone(favorito));
        mFabFavorito.setBackgroundTintList(getFabBackground(favorito));
        mFabFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean favorito = mMovieDb.favorito(movie);
                if(favorito){
                    mMovieDb.excluir(movie);
                    desfazerExclusao(favorito, movie);
                }else{
                    mMovieDb.inserir(movie);
                }
                mFabFavorito.setImageDrawable(getFabIcone(!favorito));
                mFabFavorito.setBackgroundTintList(getFabBackground(!favorito));

                ((MovieApp)getApplication()).getBus().post(new MovieEvento(movie));
            }
        });
    }

    private void desfazerExclusao(final boolean favorito, final Movie movie) {
        Snackbar snackbar = Snackbar.make(mCoordinator, R.string.snackbar_item_deletado, Snackbar.LENGTH_LONG)
        .setAction(R.string.snackbar_desfazer, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMovieDb.inserir(movie);
                        mFabFavorito.setImageDrawable(getFabIcone(favorito));
                        mFabFavorito.setBackgroundTintList(getFabBackground(favorito));
                        ((MovieApp)getApplication()).getBus().post(new MovieEvento(movie));
                    }
                }
        );
        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    private ColorStateList getFabBackground(boolean favorito) {
        return getResources().getColorStateList(favorito ? R.color.bg_fab_delete : R.color.bg_fab_favorito);
    }

    private Drawable getFabIcone(boolean favorito) {
        return getResources().getDrawable(favorito ? R.drawable.ic_delete : R.drawable.ic_favorito);
    }

    private void configurarAnimacaoEntrada() {
        ViewCompat.setTransitionName(mImgPoster, "poster");
        ViewCompat.setTransitionName(mTxtTitulo, "showTitle");
        ViewCompat.setTransitionName(mTxtAno, "releaseYear");
        ActivityCompat.postponeEnterTransition(this);
    }

    private void carregarPoster(Movie movie) {
        if(mPicassoTarget == null){
            mPicassoTarget = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mImgPoster.setImageBitmap(bitmap);
                    iniciaAnimacaoDeEntrada(mCoordinator);
                    definirCores(bitmap);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    iniciaAnimacaoDeEntrada(mCoordinator);
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
        }
        Picasso.with(this).load(movie.poster).into(mPicassoTarget);
    }

    private void definirCores(Bitmap bitmap){
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(Color.BLACK);
                int darkVibrantColor = palette.getDarkVibrantColor(Color.BLACK);
                int darkMutedColor = palette.getDarkMutedColor(Color.BLACK);
                int lightMutedColor = palette.getLightMutedColor(Color.WHITE);
                mTxtTitulo.setTextColor(vibrantColor);
                if(mAppBar != null){
                    mAppBar.setBackgroundColor(vibrantColor);
                }else{
                    mToolbar.setBackgroundColor(Color.TRANSPARENT);
                }
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    getWindow().setNavigationBarColor(darkMutedColor);
                }
                if(mCollapsingToolbarLayout != null){
                    mCollapsingToolbarLayout.setContentScrimColor(darkVibrantColor);
                }
                mCoordinator.setBackgroundColor(lightMutedColor);
                iniciaAnimacaoDeEntrada(mCoordinator);
            }
        });
    }

    private void iniciaAnimacaoDeEntrada(final View sharedElement){
        sharedElement.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(DetalheActivity.this);
                return true;
            }
        });
    }

    private void configurarBarraDeTitulo(String showTitle) {
        setSupportActionBar(mToolbar);
        if(mAppBar != null){
            if(mAppBar.getLayoutParams() instanceof CoordinatorLayout.LayoutParams){
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)mAppBar.getLayoutParams();
                lp.height = getResources().getDisplayMetrics().widthPixels;
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(mCollapsingToolbarLayout != null){
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            mCollapsingToolbarLayout.setTitle(showTitle);
        }else{
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
    private void preencherCampos(Movie movie){
        mTxtTitulo.setText(movie.showTitle);
        mTxtAno.setText(String.valueOf(movie.releaseYear));
        mTxtDiretor.setText(movie.diretor);
        mTxtElenco.setText(movie.cast);
        mTxtSummary.setText(movie.summary);
    }
}
