package com.example.mymovie.Service;

import com.example.mymovie.R;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseJason {

    /**
     * Parsing the feed results and get the list
     * @param result
     */
    public static List<Movie> parseResult(String result, ArrayList<GridItem> gridItems, List<Movie> mMovies) {

        try {
            JSONObject response = new JSONObject(result);
            JSONArray movies = response.optJSONArray("results");
            GridItem item;


            for (int i = 0; i < movies.length(); i++)
            {
                JSONObject m = movies.optJSONObject(i);

                Movie movie = null;


                if (m.has("release_date"))
                {
                    //  its a movie
                    movie = mapToMovie(m);
                }
                else if (m.has("first_air_date"))
                {
                    //  its a TV show
                    movie = mapToTV(m);
                }else if (m.getString("media_type").equals("person"))
                {
                    break;
                }

                mMovies.add(movie);
                item = new GridItem();


                if (!movie.getPosterPath().equals("null"))
                {
                    item.setImage("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                }
                else {
                    item.setImage(null);
                    item.setTitle(movie.getTitle());
                }

                gridItems.add(item);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return mMovies;
    }

    private static Movie mapToMovie(JSONObject m)
    {
        Movie movie = null;
        try{
            movie = new Movie();
            movie.setId(m.getInt("id"));
            movie.setTitle(m.getString("title"));
            movie.setOverview(m.getString("overview"));
            movie.setReleaseDate("Release Date: " + m.getString("release_date"));
            movie.setPosterPath(m.getString("poster_path"));
            movie.setVoteCount(m.getInt("vote_count"));
            movie.setVoteAverage(m.getDouble("vote_average"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return movie;
    }

    private static Movie mapToTV(JSONObject m)
    {
        Movie movie = null;
        try{
            movie = new Movie();
            movie.setId(m.getInt("id"));
            movie.setTitle(m.getString("name"));
            movie.setOverview(m.getString("overview"));
            movie.setReleaseDate("First Air Date: " + m.getString("first_air_date"));
            movie.setPosterPath(m.getString("poster_path"));
            movie.setVoteCount(m.getInt("vote_count"));
            movie.setVoteAverage(m.getDouble("vote_average"));
        }catch (JSONException e){
            e.printStackTrace();
        }
        return movie;
    }
}
