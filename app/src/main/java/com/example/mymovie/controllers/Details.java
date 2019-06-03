package com.example.mymovie.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mymovie.R;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;
import com.example.mymovie.models.MyList;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity implements View.OnClickListener
{

    private Movie movie;
    private MyList myList;

    private ImageView moviePoster;
    private FloatingActionButton addBtn;
    private boolean isAddedToList = false;

    private TextView title;
    private TextView voteCont;
    private TextView voteAverage;
    private TextView releaseDate;
    private TextView overview;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        this.sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

        init();
    }

    private void init()
    {
        this.moviePoster = findViewById(R.id.poster);
        this.addBtn = findViewById(R.id.floatingActionButton);
        this.title = findViewById(R.id.titleTextView);
        this.voteCont = findViewById(R.id.voteCount);
        this.voteAverage = findViewById(R.id.voteAverage);
        this.releaseDate = findViewById(R.id.releaseDate);
        this.overview = findViewById(R.id.overview);

        Intent intent = getIntent();
        this.movie = intent.getParcelableExtra("MOVIE");
        this.myList = intent.getParcelableExtra("SAVED_MOVIES");


        if (myList.doesContainId(movie.getId()))
        {
            isAddedToList = true;
        }
        else {
            isAddedToList = false;
        }
        updateAddToListBtn();

        //  load picture again
        GridItem item = new GridItem();

        if (!movie.getPosterPath().equals("null"))
        {
            item.setImage("https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
            Picasso.with(getApplicationContext()).load(item.getImage()).into(moviePoster);
        }
        else {
            moviePoster.setImageResource(R.drawable.movieiconmenu);
        }


        this.title.setText(movie.getTitle());
        this.voteAverage.setText(String.valueOf(movie.getVoteAverage()));
        this.voteCont.setText("Votes Count: " + String.valueOf(movie.getVoteCount()));
        this.releaseDate.setText(movie.getReleaseDate());
        this.overview.setText(movie.getOverview());

        this.addBtn.setOnClickListener(this);
    }

    private void updateAddToListBtn()
    {
        if (isAddedToList)
        {
            // update button to show delete
            addBtn.setImageResource(R.drawable.removeicon);
            addBtn.setBackgroundColor(Color.RED);
        }
        else
        {
            addBtn.setImageResource(R.drawable.addicon);
            addBtn.setBackgroundColor(Color.BLUE);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.floatingActionButton:
                if (isAddedToList)
                {
                    Toast.makeText(Details.this, "Removed from list.", Toast.LENGTH_SHORT).show();
                    myList.removeMovieById(movie.getId());

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String savedMovies = gson.toJson(myList.getMovies());
                    editor.putString("SAVED_MOVIES", savedMovies).apply();

                    isAddedToList = false;
                    updateAddToListBtn();
                }
                else
                {
                    Toast.makeText(Details.this, "Added to list.", Toast.LENGTH_SHORT).show();
                    myList.add(movie);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String savedMovies = gson.toJson(myList.getMovies());
                    editor.putString("SAVED_MOVIES", savedMovies).apply();

                    isAddedToList = true;
                    updateAddToListBtn();
                }

        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("SAVED_MOVIES", myList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
