package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Mohamed on 2/16/2018.
 *
 * Made for ease of Url building from the API
 */
public class NetworkUtils {

    // TODO Put API-Key here instead of the empty string
    private static final String THE_MOVIE_DB_API_KEY = "";
    private static final String QUERY_PARAM_API_KEY = "api_key";

    private static final String POSTER_PATH_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String POSTER_PATH_RECOMMENDED_SIZE = "w185";

    private static final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";

    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";

    private static URL buildUrl(String path){
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(QUERY_PARAM_API_KEY, THE_MOVIE_DB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static String getResponseFromHttpUrl(@Nullable URL url){
        if (url == null)
            return null;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();

            if (responseBody != null){
                return responseBody.string();
            }else {
                Log.e(NetworkUtils.class.getName(), "NULL Response Body");

                return null;
            }
        }catch (IOException e){
            e.printStackTrace();

            return null;
        }
    }

    public static String getJsonResponseFromPath(String path){
        URL url = buildUrl(path);

        return getResponseFromHttpUrl(url);
    }

    /**
     * Searched From Stack-Overflow post
     * PLUS ==> I added .isConnected() as we need to be connected to perform the request
     * @return true if device has online network, false otherwise.
     */
    public static boolean isCurrentlyOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = null;
        if (cm != null)
            netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting() && netInfo.isConnected();
    }

    public static String fullPosterPathFromThirdPartPath(String thirdPartPath){
        return POSTER_PATH_BASE_URL
                + POSTER_PATH_RECOMMENDED_SIZE
                + thirdPartPath;
    }
}
