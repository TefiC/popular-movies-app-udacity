package com.example.android.favoritemoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.example.android.favoritemoviesapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static MovieAdapter mMovieAdapter;
    private GridView mainGridView;
    private MenuItem spinner;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private String mSearchCriteria;
    private Spinner spinnerObject;

    final static String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    final static String IMAGE_SIZE = "w185";

    private boolean spinnerClicked = false;

    private ArrayList<Movie> mMoviesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Check if there is a previous state to be restored
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            String defaultCriteria = "popular";
            try {
                makeSearchQuery(defaultCriteria);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mMoviesArray = savedInstanceState.getParcelableArrayList("movies");
            setAdapter();

            Log.v(TAG, Integer.toString(savedInstanceState.getInt("sortBy")));

            //TODO (1.1): FIX BUG CAUSING SPINNER TO REFRESH ON ROTATION
//            Spinner spinnerWidget = (Spinner) spinner;
//            spinnerWidget.setSelection(savedInstanceState.getInt("sortBy"));
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
     * Saves the current moviesArray to avoid fetching data from API on rotation
     *
     * @param outState The state that will be passed to onCreate
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMoviesArray);

        //TODO (1.2): FIX BUG CAUSING SPINNER TO REFRESH ON ROTATION

        super.onSaveInstanceState(outState);
    }

    /**
     * Makes a query to the MoviesDB API
     *
     * @throws IOException
     */
    public void makeSearchQuery(String searchCriteria) throws IOException {
        // TODO: REMOVE THIS DEBUGGING STATEMENT
//        Log.v(TAG, "QUERY!!!!!!!!!!!!!!!!!!");
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
     *
     * @param JSONString the JSON response in String format
     */
    public void createMovieObjects(String JSONString) {

        ArrayList<Movie> movieArray = new ArrayList<Movie>();

        try {
            JSONObject JSONObject = new JSONObject(JSONString);
            JSONArray resultsArray = JSONObject.optJSONArray("results");

            int i;
            for (i = 0; i < resultsArray.length(); i++) {

                JSONObject movie = resultsArray.getJSONObject(i);
                Movie movieObject = createMovie(movie);

                if (movieObject != null) {
                    movieArray.add(createMovie(movie));
                }
            }

            if (movieArray.size() > 0) {
                setMovieArray(movieArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Movie object from a JSON Object fetched from the network
     *
     * @param movie a JSONObject containing the data for a movie
     * @return Movie object
     */
    private Movie createMovie(JSONObject movie) {
        try {
            String title = movie.getString("title");
            String posterPath = MOVIEDB_POSTER_BASE_URL + IMAGE_SIZE + movie.getString("poster_path");
            String plot = movie.getString("overview");
            String releaseDate = movie.getString("release_date");
            Double voteAverage = movie.getDouble("vote_average");

            return new Movie(title, releaseDate, posterPath, voteAverage, plot);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the Movie Adapter to the GridView, the main layout that will contain movie posters
     */
    private void setAdapter() {
        mMovieAdapter = new MovieAdapter(MainActivity.this, mMoviesArray, this);
        mMovieAdapter.notifyDataSetChanged();

        mainGridView = (GridView) findViewById(R.id.root_grid_view);
        mainGridView.invalidateViews();
        mainGridView.setAdapter(mMovieAdapter);
    }

    /**
     * Implementation of the onClick method in the MovieAdapter class
     * It lanches an activity passing the corresponding movie object
     *
     * @param movie A Movie instance that corresponds to the item clicked
     */

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationActivity = DetailsActivity.class;
        Intent intent = new Intent(context, destinationActivity);
        intent.putExtra("movieObject", movie);
        startActivity(intent);
    }

    // Menu methods ========================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        createSpinner(menu);
        return true;
    }

    private void createSpinner(Menu menu) {

        spinner = menu.findItem(R.id.sort_spinner);

        spinnerObject = (Spinner) findViewById(R.id.sort_spinner);

        Spinner spinnerView = (Spinner) spinner.getActionView();

        spinnerView.setOnItemSelectedListener(this);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_options_array, R.layout.spinner_item);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerView.setAdapter(spinnerAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String searchCriteria = parent.getItemAtPosition(pos).toString();

        mSearchCriteria = searchCriteria;

        switch (searchCriteria) {
            case "Top Rated":
                try {
                    makeSearchQuery(searchCriteria);
                    spinnerClicked = true;
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            case "Most Popular":
                //Avoid making extra query the first time app is launched
                if (spinnerClicked) {
                    try {
                        makeSearchQuery(searchCriteria);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //
    }
}
