package com.example.mymovie.controllers;

import android.content.Intent;
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
import android.widget.TextView;

import com.example.mymovie.GridViewAdapter;
import com.example.mymovie.R;
import com.example.mymovie.Service.FetchDataFromAPI;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;
import com.example.mymovie.models.MyList;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements AbsListView.OnScrollListener, View.OnClickListener, AdapterView.OnItemClickListener
{
    private TextView input;
    private Button searchBtn;
    private GridView mGridView;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;
    private Button goToTopBtn;

    private MyList myList;

    private int currentPage = 1;
    private int counter = 20;
    private int currentPosition = 0;
    private String query;
    private FetchDataFromAPI fetchDataFromAPI;

    private List<Movie> movies;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_discover:
                    Intent showDiscoverActivity = new Intent(SearchActivity.this, MainActivity.class);
                    startActivity(showDiscoverActivity);
                    break;
                case R.id.navigation_myList:
                    Intent showMyListActivity = new Intent(SearchActivity.this, MyListActivity.class);
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
        setContentView(R.layout.activity_search);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_search); //  set focus on this menu option

        movies = new ArrayList<>();

        mGridView = (GridView) findViewById(R.id.gridViewSearch);
        mGridView.setOnScrollListener(this);
        mGridView.setOnItemClickListener(this);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridAdapter);

        this.goToTopBtn = findViewById(R.id.goToTopBtn);
        this.goToTopBtn.setOnClickListener(this);

        myList = getIntent().getParcelableExtra("SAVED_MOVIES");

        this.input = findViewById(R.id.editTextInput);
        this.searchBtn = findViewById(R.id.searchBtn);
        this.searchBtn.setOnClickListener(this);

        fetchDataFromAPI = new FetchDataFromAPI(getApplicationContext(), mGridAdapter, mGridData, mProgressBar, movies);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.goToTopBtn:
                this.goToTopBtn.setVisibility(View.GONE);
                this.goToTopBtn.setVisibility(View.INVISIBLE);
                this.mGridView.smoothScrollToPosition(0);
                break;


            case R.id.searchBtn:
                this.query = this.input.getText().toString();
                if (query != null && query.length() > 0)
                {
                    String correctUrlFormat = query.replaceAll(" ", "+");
                    resetPage();
                    fetchDataFromAPI.execute("https://api.themoviedb.org/3/search/multi?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&query=" + correctUrlFormat + "&page=" + currentPage +"&include_adult=false");
                    //new download().execute("https://api.themoviedb.org/3/search/multi?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&query=" + correctUrlFormat + "&page=" + currentPage +"&include_adult=false");
                    this.mProgressBar.setVisibility(View.VISIBLE);
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
            String correctUrlFormat = query.replaceAll(" ", "+");
            fetchDataFromAPI.execute("https://api.themoviedb.org/3/search/multi?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&query=" + correctUrlFormat + "&page=" + currentPage +"&include_adult=false");
            //new download().execute("https://api.themoviedb.org/3/search/multi?api_key=81a6f060b362e563f6557d4c74ab2e27&language=en-US&query=" + query + "&page=" + currentPage + "&include_adult=false");
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void resetPage()
    {
        currentPage = 1;
        counter = 20;
        mGridAdapter.reset();
        movies.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), Details.class);
        i.putExtra("MOVIE", movies.get(position));
        i.putExtra("SAVED_MOVIES", myList);
        startActivityForResult(i, 1);
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
