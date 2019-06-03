package com.example.mymovie.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mymovie.GridViewAdapter;
import com.example.mymovie.models.GridItem;
import com.example.mymovie.models.Movie;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

public class FetchDataFromAPI
{
    private Context context;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<GridItem> gridItems;
    private ProgressBar progressBar;
    private List<Movie> movies;

    public FetchDataFromAPI(Context context, GridViewAdapter gridViewAdapter, ArrayList<GridItem> gridItems, ProgressBar progressBar, List<Movie> movies) {
        this.context = context;
        this.gridViewAdapter = gridViewAdapter;
        this.gridItems = gridItems;
        this.progressBar = progressBar;
        this.movies = movies;
    }

    public void execute(String query)
    {
        new AsyncHttpTask().execute(query);
    }

    //Downloading data asynchronously
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            try {
                // Create Apache HttpClient
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(params[0]));
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    String response = StreamToString.streamToString(httpResponse.getEntity().getContent());
                    ParseJason.parseResult(response, gridItems, movies);
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            if (result == 1) {
                gridViewAdapter.setGridData(gridItems);
            } else {
                Toast.makeText(context, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}

