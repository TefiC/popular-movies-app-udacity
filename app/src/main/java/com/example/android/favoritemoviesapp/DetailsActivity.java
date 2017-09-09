package com.example.android.favoritemoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intentThatStartedThisActivity = getIntent();

        TextView view = (TextView) findViewById(R.id.movie_details_title);

        if (intentThatStartedThisActivity.hasExtra("movieObject")) {
            Movie movie = intentThatStartedThisActivity.getExtras().getParcelable("movieObject");
            view.setText(movie.getMovieTitle());
        }
    }
}
