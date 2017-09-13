package com.example.android.favoritemoviesapp;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a MovieAdapter
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private final Context context;
    private ArrayList<Movie> mMoviesArray;
    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(Activity context, ArrayList<Movie> movies, MovieAdapterOnClickHandler clickHandler) {
        super(context, 0, movies);
        this.context = context;
        mMoviesArray = movies;
        mClickHandler = clickHandler;
    }

    /**
     * Interface to implement onClick handler
     */

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie, boolean posterLoaded);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView movieView;

        if (convertView == null) {
            movieView = (ImageView) LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item, parent, false);
        } else {
            movieView = (ImageView) convertView;
        }

        final Movie movie = getItem(position);


        String posterPath = movie.getMoviePosterPath();

        final AtomicBoolean loaded = new AtomicBoolean(true);

        if (posterPath != null) {
            Picasso.with(context)
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder2)
                    .fit()
                    .error(R.drawable.error)
                    .into(movieView, new Callback() {
                        @Override
                        public void onSuccess() {
                            //
                        }

                        @Override
                        public void onError() {
                            loaded.set(false);
                        }
                    });
        }

        movieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onClick(movie, loaded.get());
            }
        });

        return movieView;
    }

    @Override
    public int getCount() {
        return mMoviesArray.size();
    }

    /**
     * Method that searches the Movie at the current adapter position
     * in the mMoviesArray array list.
     *
     * @param position Index of the Movie we need to retrieve
     * @return the Movie instance at position in mMoviesArray
     */

    @Nullable
    @Override
    public Movie getItem(int position) {
        return mMoviesArray.get(position);
    }
}
