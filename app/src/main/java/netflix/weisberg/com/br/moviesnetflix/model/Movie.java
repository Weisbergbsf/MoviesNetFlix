package netflix.weisberg.com.br.moviesnetflix.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Movie implements Serializable{

    @SerializedName("show_title")
    public String showTitle;
    @SerializedName("release_year")
    public int releaseYear;
    @SerializedName("poster")
    public String poster;
    @SerializedName("summary")
    public String summary;

}
