package com.example.android.favoritemoviesapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Represents a MovieAdapter
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private final Context context;
    private ArrayList<Movie> mMoviesArray;
    private static final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
        this.mMoviesArray = movies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView movieView;

        if (convertView == null) {
            movieView = (ImageView) LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        } else {
            movieView = (ImageView) convertView;
        }

        Movie movie = getItem(position);

        String posterPath = movie.getMoviePosterPath();

        if (posterPath != null) {
            Picasso.with(context)
                    .setLoggingEnabled(true);

            Picasso.with(context)
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .error(R.drawable.error)
                    .into(movieView);
        }

        return movieView;

    }

    @Override
    public int getCount() {
        return mMoviesArray.size();
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        return mMoviesArray.get(position);
    }
}
