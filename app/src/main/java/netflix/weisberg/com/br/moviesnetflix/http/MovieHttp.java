package netflix.weisberg.com.br.moviesnetflix.http;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

import netflix.weisberg.com.br.moviesnetflix.model.Movie;


public class MovieHttp {

    public static Movie[] obterDiscosDoServidor(String ator) throws Exception {

        String url = String.format("http://netflixroulette.net/api/api.php?actor=%s", ator);

        String json = getResponse(url);
        Gson gson = new Gson();
        Movie[] result = gson.fromJson(json, Movie[].class);

        return result;

    }

    private static String getResponse(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
