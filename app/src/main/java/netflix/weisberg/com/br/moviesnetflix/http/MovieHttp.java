package netflix.weisberg.com.br.moviesnetflix.http;

import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

import netflix.weisberg.com.br.moviesnetflix.model.Movie;


public class MovieHttp {

    public static final String BASE_URL = "http://netflixroulette.net/api/api.php?actor=Arnold";

    public static Movie[] obterDiscosDoServidor(){
        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(5, TimeUnit.SECONDS);
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                .url(BASE_URL)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, Movie[].class);
        } catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }

}
