package com.example.android.favoritemoviesapp;

import android.app.Activity;
import android.content.Context;
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
    private static final String TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, 0, movies);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        }

        Movie movie = getItem(position);

        if( movie!= null) {
            String posterPath = movie.getMoviePosterPath();

            Picasso.with(context)
                    .load(posterPath)
                    .into((ImageView) convertView);
        }

        return convertView;
    }

}
