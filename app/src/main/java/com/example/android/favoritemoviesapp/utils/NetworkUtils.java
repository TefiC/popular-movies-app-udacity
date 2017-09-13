package com.example.android.favoritemoviesapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.android.favoritemoviesapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Utilities that will be used to connect to the network
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    final static String MOVIEDB_URL = "https://api.themoviedb.org/3/movie/";

    // Parameters to build the URL
    final static String PARAM_QUERY = "q";

    final static String PARAM_KEY = "api_key";
    //    final static String API_KEY; // TODO: add MoviesDB API key

    /**
     * Build the URL used to query MovieDB
     *
     * @param sortBy The user's preference of movies to display
     * @return The URL to fetch movies according to user's preferences
     */
    public static URL buildURL(String sortBy) {

        URL url = null;

        String criteria = null;

        switch (sortBy) {
            case "Most Popular":
                criteria = "popular";
                break;
            case "Top Rated":
                criteria = "top_rated";
                break;
            default:
                criteria = "popular";
                break;
        }

        Uri buildUri = Uri.parse(MOVIEDB_URL + criteria).buildUpon()
                .appendQueryParameter(PARAM_KEY, API_KEY)
                .build();
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static void createNoConnectionDialog(Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.connection_dialog_title))
                .setMessage(context.getString(R.string.connection_dialog_message))
                .show();
    }

    /**
     * Determine if there is an internet connection available. Copyright (1)
     *
     * @return true if there is, false if there isn't.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

//Copyright

/* (1)
https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
 */