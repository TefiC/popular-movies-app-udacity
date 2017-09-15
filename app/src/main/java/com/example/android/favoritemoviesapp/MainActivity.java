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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.android.favoritemoviesapp.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    /**
     * Fields
     */

    private String mSearchCriteria = "Most Popular"; // Default sort criteria
    private ArrayList<Movie> mMoviesArray = null;
    private GridView mMainGridView;
    private MovieAdapter mMovieAdapter;

    /**
     * Constants
     */

    // Tag for logging
    private static final String TAG = MainActivity.class.getSimpleName();

    // Constants to form the movie poster URL
    private static final String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String IMAGE_SIZE = "w185";


    ProgressBar mProgressBar;

    /**
     * Methods
     */

    // Methods that request data and update ========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Check if there is a previous state to be restored
        if (savedInstanceState == null
                || !savedInstanceState.containsKey("movies")
                || !savedInstanceState.containsKey("criteria")
                || !savedInstanceState.containsKey("gridScroll")) {

            try {
                makeSearchQuery(mSearchCriteria);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //Retrieve data
            mMoviesArray = savedInstanceState.getParcelableArrayList("movies");
            mSearchCriteria = savedInstanceState.getString("criteria");

            // Prevent cases where there was no internet connection,
            // no data was loaded previously but the user rotates device
            if (mMoviesArray != null) {
                setAdapter();
                restoreScrollPosition(savedInstanceState);
            }
        }
    }

    /**
     * Getters
     */

    public ArrayList<Movie> getMovieArray() {
        return mMoviesArray;
    }

    /**
     * Setters
     */

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
        outState.putString("criteria", mSearchCriteria);

        // TODO: (1) Pass GridView first visible position (int)
        if (mMainGridView != null) {
            outState.putInt("gridScroll", mMainGridView.getFirstVisiblePosition());
        }

        super.onSaveInstanceState(outState);
    }

    private void restoreScrollPosition(Bundle savedInstanceState) {
        //Get scroll position
        int position = savedInstanceState.getInt("gridScroll");
        mMainGridView.smoothScrollToPosition(position);
    }

    /**
     * Makes a query to the MoviesDB API
     *
     * @throws IOException
     */
    public void makeSearchQuery(String searchCriteria) throws IOException {
        Log.v(TAG, "MAKING QUERY");
        if (NetworkUtils.isNetworkAvailable(this)) {
            URL searchURL = NetworkUtils.buildURL(searchCriteria);
            new QueryTask().execute(searchURL);
        } else {
            NetworkUtils.createNoConnectionDialog(this);
            mMoviesArray = null;
        }
    }

    /**
     * An AsyncTask to handle network requests to MovieDB API
     */
    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

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
            mProgressBar.setVisibility(View.INVISIBLE);
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

        mMainGridView = (GridView) findViewById(R.id.root_grid_view);
        mMainGridView.invalidateViews();
        mMainGridView.setAdapter(mMovieAdapter);
    }

    /**
     * Implementation of the onClick method in the MovieAdapter class
     * It launches an activity passing the corresponding movie object
     *
     * @param movie A Movie instance that corresponds to the item clicked
     */

    @Override
    public void onClick(Movie movie) {
        // Only respond to click if the poster was loaded correctly
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

    /**
     * Creates a Spinner feature in the menu bar with custom layout and format
     * that shows the corresponding selection, even after rotation.
     *
     * @param menu The menu being created
     */

    private void createSpinner(Menu menu) {

        // Get spinner and spinner view
        MenuItem spinner = menu.findItem(R.id.sort_spinner);
        Spinner spinnerView = (Spinner) spinner.getActionView();

        // Set listener
        spinnerView.setOnItemSelectedListener(this);

        // Create spinner adapter
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_options_array, R.layout.spinner_item);

        // Custom dropdown layout
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        spinnerView.setAdapter(spinnerAdapter);

        // To make sure that on device rotation the previous selection is kept
        spinnerView.setSelection(spinnerAdapter.getPosition(mSearchCriteria));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        String searchCriteria = parent.getItemAtPosition(pos).toString();

        // If the query is not searching the same criteria already selected
        if (!mSearchCriteria.equals(searchCriteria)) {

            mSearchCriteria = searchCriteria;

            switch (searchCriteria) {
                case "Top Rated":
                    try {
                        makeSearchQuery(searchCriteria);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case "Most Popular":
                    //Avoid making extra query the first time app is launched
                    try {
                        makeSearchQuery(searchCriteria);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //
    }

    // Activity lifecycle methods ========================================================

    /**
     * Lifecycle method to handle cases where the user was initially
     * offline and no data was fetched and then the user reconnects
     * and resumes the app. To handle fetching automatically without
     * user intervention.
     */
    @Override
    public void onRestart() {
        super.onRestart();
        if (mMoviesArray == null) {
            try {
                makeSearchQuery(mSearchCriteria);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

