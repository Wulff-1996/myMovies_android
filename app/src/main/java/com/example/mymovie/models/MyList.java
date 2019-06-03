package com.example.mymovie.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MyList implements Parcelable
{

    private List<Movie> movies;

    public MyList()
    {
        this.movies = new ArrayList<>();
    }

    protected MyList(Parcel in) {
        movies = in.createTypedArrayList(Movie.CREATOR);
    }

    public static final Creator<MyList> CREATOR = new Creator<MyList>() {
        @Override
        public MyList createFromParcel(Parcel in) {
            return new MyList(in);
        }

        @Override
        public MyList[] newArray(int size) {
            return new MyList[size];
        }
    };

    public void setMovies(List<Movie> movies)
    {
        this.movies = movies;
    }

    public void add(Movie movie)
    {
        this.movies.add(movie);
    }

    public boolean doesContainId(int id)
    {
        boolean doesContainId = false;

        for (Movie m: movies) {
            if (m.getId() == id)
            {
                doesContainId = true;
                break;
            }
        }
        return doesContainId;
    }

    public void removeMovieById(int id)
    {
        for (Movie m:movies) {
            if (m.getId() == id)
            {
                movies.remove(m);
                break;
            }
        }
    }

    public List<Movie> getMovies()
    {
        return this.movies;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(movies);
    }
}
