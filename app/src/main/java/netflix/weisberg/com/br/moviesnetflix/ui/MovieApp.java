package netflix.weisberg.com.br.moviesnetflix.ui;

import android.app.Application;

import com.squareup.otto.Bus;

public class MovieApp extends Application{

    private Bus bus;

    @Override
    public void onCreate(){
        super.onCreate();
        bus = new Bus();
    }

    public Bus getBus() {
        return bus;
    }
}
