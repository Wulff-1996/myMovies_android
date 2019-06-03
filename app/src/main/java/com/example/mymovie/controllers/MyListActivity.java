package com.example.mymovie.controllers;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.example.mymovie.GridViewAdapter;
import com.example.mymovie.R;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;
import com.example.mymovie.models.MyList;

import java.util.ArrayList;

public class MyListActivity extends AppCompatActivity implements View.OnClickListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener
{
    private MyList myList;

    private GridView mGridView;
    private GridItem mGridItem;
    private ProgressBar mProgressBar;
    private GridViewAdapter mGridAdapter;
    private ArrayList<GridItem> mGridData;

    private Button goToTopBtn;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_discover:
                    Intent showDiscoverActivity = new Intent(MyListActivity.this, MainActivity.class);
                    startActivity(showDiscoverActivity);
                    break;
                case R.id.navigation_search:
                    Intent showSearchActivity = new Intent(MyListActivity.this, SearchActivity.class);
                    showSearchActivity.putExtra("SAVED_MOVIES", myList);
                    startActivity(showSearchActivity);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_myList);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mGridView = (GridView) findViewById(R.id.gridView);
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

        loadPictures();
    }

    private void loadPictures()
    {
        for (Movie m: myList.getMovies()) {
            //  load picture again
            GridItem item = new GridItem();
            if (!m.getPosterPath().equals("null"))
            {
                item.setImage("https://image.tmdb.org/t/p/w500" + m.getPosterPath());
            }
            else
            {
                item.setImage(null);
                item.setTitle(m.getTitle());
            }
            mGridData.add(item);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem > 3) {
            this.goToTopBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getApplicationContext(), Details.class);
        i.putExtra("MOVIE", myList.getMovies().get(position));
        i.putExtra("SAVED_MOVIES", myList);
        startActivityForResult(i, 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.goToTopBtn:
                this.goToTopBtn.setVisibility(View.GONE);
                this.goToTopBtn.setVisibility(View.INVISIBLE);
                mGridView.smoothScrollToPosition(0);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                myList = data.getParcelableExtra("SAVED_MOVIES");
                mGridData.clear();
                loadPictures();
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }
}
