package com.example.android.favoritemoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    ImageView moviePosterView;
    TextView movieVoteAverageView;
    TextView movieReleaseView;
    TextView moviePlotView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intentThatStartedThisActivity = getIntent();

        //Assign the views that will be populated with the movie's data
        moviePosterView = (ImageView) findViewById(R.id.movie_details_poster_view);
        movieVoteAverageView = (TextView) findViewById(R.id.movie_details_vote_view);
        movieReleaseView = (TextView) findViewById(R.id.movie_details_release_view);
        moviePlotView = (TextView) findViewById(R.id.movie_details_plot_view);

        // Make plot view scrollable
        moviePlotView.setMovementMethod(new ScrollingMovementMethod());

        // Get corresponding movie and fill views with data
        if (intentThatStartedThisActivity.hasExtra("movieObject")) {
            Movie movie = intentThatStartedThisActivity.getExtras().getParcelable("movieObject");
            fillMovieData(movie);
        }
    }

    /**
     * Updates the UI for the Details Activity by setting the text
     * and resources for the corresponding movie
     * @param movie The movie the user clicked on
     */
    private void fillMovieData(Movie movie) {
        String movieTitle = movie.getMovieTitle();
        String posterPath = movie.getMoviePosterPath();
        Double voteAverage = movie.getMovieVoteAverage();
        String releaseDate = movie.getMovieReleaseDate();
        String moviePlot = movie.getMoviePlot();

        // Change activity label to be movie title
        setTitle(movieTitle);

        // Load poster
        Picasso.with(this)
                .load(posterPath)
                .placeholder(R.drawable.placeholder)
                .resize(200,250)
                .error(R.drawable.error)
                .into(moviePosterView);

        // Update textViews
        movieVoteAverageView.setText(voteAverage.toString());
        movieReleaseView.setText(releaseDate);
        moviePlotView.setText(moviePlot);

    }

}
