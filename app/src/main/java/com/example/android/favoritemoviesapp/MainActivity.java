package com.example.android.favoritemoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.example.android.favoritemoviesapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    final static String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE = "w185";

    private ArrayList<Movie> mMoviesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            makeSearchQuery();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Getters

    public ArrayList<Movie> getMovieArray() {
        return mMoviesArray;
    }

    //Setters
    public void setMovieArray(ArrayList<Movie> moviesArray) {
        mMoviesArray = moviesArray;
    }

    /**
     * Makes a query to the MoviesDB API
     * @throws IOException
     */
    public void makeSearchQuery() throws IOException {
        String searchCriteria = "popular";
        URL searchURL = NetworkUtils.buildURL(searchCriteria);
        new QueryTask().execute(searchURL);
    }

    /**
     * An AsyncTask to handle network requests to MovieDB API
     */
    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;

            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            createMovieObjects(s);
            setAdapter();
        }
    }

    /**
     * From the data received from the network request, create an array of Movie objects
     * to store in a class member variable
     * @param JSONString the JSON response in String format
     */
    public void createMovieObjects(String JSONString) {

        ArrayList<Movie> movieArray = new ArrayList<Movie>();

        try {
            JSONObject JSONObject = new JSONObject(JSONString);
            JSONArray resultsArray = JSONObject.optJSONArray("results");

            int i;
            for(i=0; i<resultsArray.length(); i++) {

                JSONObject movie = resultsArray.getJSONObject(i);
                Movie movieObject = createMovie(movie);

                if(movieObject != null) {
                    movieArray.add(createMovie(movie));
                }
            }

            if(movieArray.size() > 0) {
                setMovieArray(movieArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Movie object from a JSON Object fetched from the network
     * @param movie a JSONObject containing the data for a movie
     * @return Movie object
     */
    private Movie createMovie(JSONObject movie) {
        try {
            String title = movie.getString("title");
            String posterPath =  MOVIEDB_POSTER_BASE_URL + IMAGE_SIZE + movie.getString("poster_path");
            String plot = movie.getString("overview");
            String releaseDate = movie.getString("release_date");
            Double voteAverage = movie.getDouble("vote_average");

            return new Movie(title, releaseDate, posterPath, voteAverage, plot);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    };

    /**
     * Sets the Movie Adapter to the GridView, the main layout that will contain movie posters
     */
    private void setAdapter() {
        MovieAdapter movieAdapter = new MovieAdapter(MainActivity.this, mMoviesArray);
        GridView mainGridView = (GridView) findViewById(R.id.root_grid_view);
        mainGridView.setAdapter(movieAdapter);
    }
}
