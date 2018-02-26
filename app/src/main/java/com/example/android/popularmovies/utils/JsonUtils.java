package com.example.android.popularmovies.utils;

import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Mohamed on 2/17/2018.
 *
 * No need to check if jsonObject has key as we use .opt method instead of .get
 */

public class JsonUtils {

    private static final String JSON_KEY_RESULTS = "results";

    private static final String JSON_KEY_ORIGINAL_TITLE = "original_title";
    private static final String JSON_KEY_POSTER_PATH = "poster_path";
    private static final String JSON_KEY_OVERVIEW = "overview";
    private static final String JSON_KEY_VOTE_AVERAGE = "vote_average";
    private static final String JSON_KEY_RELEASE_DATE = "release_date";

    public static ArrayList<Movie> getMoviesArrayList(String response) {
        try {
            JSONObject rootJsonObject = new JSONObject(response);

            JSONArray results = rootJsonObject.optJSONArray(JSON_KEY_RESULTS);

            if (results != null){
                ArrayList<Movie> movies = new ArrayList<>();

                JSONObject movie;

                String originalTitle;
                String posterPath;
                String overview;
                double voteAverage;
                String releaseDate;

                for (int i=0; i<results.length(); i++){
                    movie = results.optJSONObject(i);
                    if (movie == null)
                        continue;

                    originalTitle = movie.optString(JSON_KEY_ORIGINAL_TITLE);
                    posterPath = movie.optString(JSON_KEY_POSTER_PATH);
                    overview = movie.optString(JSON_KEY_OVERVIEW);
                    voteAverage = movie.optDouble(JSON_KEY_VOTE_AVERAGE);
                    releaseDate = movie.optString(JSON_KEY_RELEASE_DATE);

                    // poster_path MUST be checked if valid
                    if (posterPath.isEmpty())
                        continue;

                    movies.add(new Movie(originalTitle, posterPath, overview, voteAverage,
                            releaseDate));
                }

                return movies;
            }else {
                Log.e(JsonUtils.class.getName(), "Null results JsonArray object");

                return null;
            }
        }catch (JSONException e){
            e.printStackTrace();

            return null;
        }
    }

}