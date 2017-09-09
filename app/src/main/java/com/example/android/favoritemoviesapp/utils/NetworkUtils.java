package com.example.android.favoritemoviesapp.utils;

import android.net.Uri;

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

    final static String MOVIEDB_URL = "https://api.themoviedb.org/3/movie/";

    // Parameters to build the URL
    final static String PARAM_QUERY = "q";

    final static String PARAM_KEY = "api_key";
    final static String API_KEY; // TODO: add MoviesDB API key

    private String sortBy;
    private String finalBaseUrl;

    /**
     * Build the URL used to query MovieDB
     *
     * @param sortBy The user's preference of movies to display
     * @return The URL to fetch movies according to user's preferences
     */
    public static URL buildURL(String sortBy) {

        URL url = null;

        if(sortBy == "popular" || sortBy == "top_rated") {

            Uri buildUri = Uri.parse(MOVIEDB_URL + sortBy).buildUpon()
                    .appendQueryParameter(PARAM_KEY, API_KEY)
                    .build();

            try {
                url = new URL(buildUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
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
}
