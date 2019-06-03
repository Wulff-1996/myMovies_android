package com.example.mymovie.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.mymovie.GridViewAdapter;
import com.example.mymovie.R;
import com.example.mymovie.Service.FetchDataFromAPI;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;
import com.example.mymovie.models.MyList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener
{

    private List<Movie> movies;
    private MyList myList;

    private Button popularBtn;
    private Button upcomingBtn;
    private Button topRatedBtn;
    private Button moviesTV;
    private Button goToTopBtn;

    private int selectedCategory;
    private int selectedEntertainmentType;

    private SharedPreferences sharedPreferences;

    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private int currentPage = 1;
    private int counter = 20;
    private int currentPosition = 0;

    private FetchDataFromAPI fetchDataFromAPI;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    Intent showSearchActivity = new Intent(MainActivity.this, SearchActivity.class);
                    showSearchActivity.putExtra("SAVED_MOVIES", myList);
                    startActivity(showSearchActivity);
                    break;
                case R.id.navigation_myList:
                    Intent showMyListActivity = new Intent(MainActivity.this, MyListActivity.class);
                    showMyListActivity.putExtra("SAVED_MOVIES", myList);
                    startActivity(showMyListActivity);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_discover); //  set focus on this menu option

        myList = new MyList();

        this.sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("SAVED_MOVIES", null);
        List<Movie> savedMovies = gson.fromJson(json, new TypeToken<ArrayList<Movie>>(){}.getType());

        if(savedMovies != null && !savedMovies.isEmpty())
        {
            myList.setMovies(savedMovies);
        }


        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        //  set default selected movie category
        selectedCategory = R.string.POPULAR;
        selectedEntertainmentType = R.string.MOVIES;
        this.movies = new ArrayList<>();

        this.fetchDataFromAPI = new FetchDataFromAPI(getApplicationContext(), mGridAdapter, mGridData, mProgressBar, movies);

        init();

        fetchData();
    }

    private void init()
    {
        this.popularBtn = findViewById(R.id.popularBtn);
        this.upcomingBtn = findViewById(R.id.upcomingBtn);
        this.topRatedBtn = findViewById(R.id.topRatedBtn);
        this.moviesTV = findViewById(R.id.moviesTVBTn);
        this.goToTopBtn = findViewById(R.id.goToTopBtn);

        this.popularBtn.setText(R.string.POPULAR);
        this.upcomingBtn.setText(R.string.UPCOMING);
        this.topRatedBtn.setText(R.string.TOP_RATED);
        this.moviesTV.setText(R.string.MOVIES);
        this.goToTopBtn.setText(R.string.top);

        this.popularBtn.setOnClickListener(this);
        this.upcomingBtn.setOnClickListener(this);
        this.topRatedBtn.setOnClickListener(this);
        this.moviesTV.setOnClickListener(this);
        this.goToTopBtn.setOnClickListener(this);

        updateButtonStyles();
    }

    private void fetchData()
    {
        switch (selectedEntertainmentType)
        {
            case R.string.MOVIES:

                if (selectedCategory == R.string.POPULAR)
                {
                    //new AsyncHttpTask().execute("https://api.themoviedb.org/3/movie/popular?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/movie/popular?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                }else if (selectedCategory == R.string.UPCOMING)
                {
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/movie/upcoming?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (selectedCategory == R.string.TOP_RATED)
                {
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/movie/top_rated?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;

            case R.string.TV:

                if (selectedCategory == R.string.POPULAR)
                {
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/tv/popular?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                }else if (selectedCategory == R.string.UPCOMING)
                {
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/tv/on_the_air?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else if (selectedCategory == R.string.TOP_RATED)
                {
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/tv/top_rated?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&page=" + currentPage);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (firstVisibleItem > 3)
        {
            this.goToTopBtn.setVisibility(View.VISIBLE);
        }
        if (firstVisibleItem + visibleItemCount >= counter)
        {
            currentPage++;
            counter += 20;
            fetchData();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent i = new Intent(getApplicationContext(), Details.class);
        i.putExtra("MOVIE", movies.get(position));
        i.putExtra("SAVED_MOVIES", myList);
        startActivityForResult(i, 1);
    }

    private void resetPage()
    {
        currentPage = 1;
        counter = 20;
        mGridAdapter.reset();
        movies.clear();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.popularBtn:
                if (selectedCategory != R.string.POPULAR)
                {
                    this.selectedCategory = R.string.POPULAR;
                    updateButtonStyles();
                    resetPage();
                    fetchData();
                }
                break;

            case R.id.upcomingBtn:
                if (selectedCategory != R.string.UPCOMING)
                {
                    this.selectedCategory = R.string.UPCOMING;
                    updateButtonStyles();
                    resetPage();
                    fetchData();
                }
                break;

            case R.id.topRatedBtn:
                if (selectedCategory != R.string.TOP_RATED)
                {
                    this.selectedCategory = R.string.TOP_RATED;
                    updateButtonStyles();
                    resetPage();
                    fetchData();
                }
                break;

            case R.id.moviesTVBTn:
                if (this.selectedEntertainmentType == R.string.MOVIES){
                    this.selectedEntertainmentType = R.string.TV;
                    moviesTV.setText(R.string.TV);
                    this.selectedCategory = R.string.POPULAR;
                    this.upcomingBtn.setText(R.string.AIRING_TODAY);
                    updateButtonStyles();
                    resetPage();
                    fetchData();
                }else{
                    this.selectedEntertainmentType = R.string.MOVIES;
                    moviesTV.setText(R.string.MOVIES);
                    this.selectedCategory = R.string.POPULAR;
                    this.upcomingBtn.setText(R.string.UPCOMING);
                    updateButtonStyles();
                    resetPage();
                    fetchData();
                }
                break;

            case R.id.goToTopBtn:
                this.goToTopBtn.setVisibility(View.GONE);
                this.goToTopBtn.setVisibility(View.INVISIBLE);
                mGridView.smoothScrollToPosition(0);
                break;

        }
    }

    private void updateButtonStyles()
    {
        this.popularBtn.setBackgroundColor(Color.BLUE);
        this.popularBtn.setTextColor(Color.RED);

        this.upcomingBtn.setBackgroundColor(Color.BLUE);
        this.upcomingBtn.setTextColor(Color.RED);

        this.topRatedBtn.setBackgroundColor(Color.BLUE);
        this.topRatedBtn.setTextColor(Color.RED);


        if (selectedCategory == R.string.POPULAR){
            this.popularBtn.setBackgroundColor(Color.RED);
            this.popularBtn.setTextColor(Color.BLUE);
        } else if (selectedCategory == R.string.UPCOMING){
            this.upcomingBtn.setBackgroundColor(Color.RED);
            this.upcomingBtn.setTextColor(Color.BLUE);
        } else if (selectedCategory == R.string.TOP_RATED){
            this.topRatedBtn.setBackgroundColor(Color.RED);
            this.topRatedBtn.setTextColor(Color.BLUE);
        }
    }

    /// save state for gridview

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
    {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("grid_position", mGridView.getFirstVisiblePosition());
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey("grid_position")) {
            currentPosition = savedInstanceState.getInt("grid_position");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGridView.smoothScrollToPosition(currentPosition);
    }

    @Override
    protected void onPause() {
        // set in onPause() too since onRestoreInstanceState() is only
        // called when the Activity is destroyed and recreated.
        super.onPause();
        currentPosition = mGridView.getFirstVisiblePosition();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                myList = data.getParcelableExtra("SAVED_MOVIES");
            }
        }
    }
}
