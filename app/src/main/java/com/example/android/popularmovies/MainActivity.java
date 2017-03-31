package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.FilmAdapter.FilmAdapterOnClickHandler;
import com.example.android.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements FilmAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private FilmAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String FILMREQUEST_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private String SORT_BY = "popular";
    private String API_KEY = "api_key";
    private String txtJsonToParse;

    private URL url;
    public boolean isFavo = false;
    private static final int LOADER_ID = 0;


    private String CONST_DOMAIN_FOR_THUMBNAIL = "http://image.tmdb.org/t/p/w185";
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Log.v("MainActivity", "WORKING    ");


        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new GridLayoutManager(this, 2);
        }else mLayoutManager = new GridLayoutManager(this,3);



        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FilmAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        new FetchFilmTask().execute(FILMREQUEST_BASE_URL + SORT_BY);


    }


    @Override
    public void onClick(Film filmData) {
        Context context = this;
        Class classDestination = FilmDetails.class;
        Intent intentToStartFilmDetails = new Intent(context, classDestination);
        intentToStartFilmDetails.putExtra("film", filmData);
        startActivity(intentToStartFilmDetails);
    }

    private void showFilmDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(MainActivity.this) {

            Cursor mFilmData = null;

            @Override
            protected void onStartLoading() {
                Log.v("On start loading", "STARTED");
                if (mFilmData != null) {
                    deliverResult(mFilmData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID);

                } catch (Exception e) {
                    Log.e("MAIN ACTIVITY: ", "Failed loading from database");
                    e.printStackTrace();
                    return null;

                }

            }

            public void deliverResult(Cursor data) {
                mFilmData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<Film> favFilms = new ArrayList<>();
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
            // The Cursor is now set to the right position String filmThumbnail, String filmOverview,String filmUsersRate,String filmTitle,String filmReleaseDate,String id
                /* public static final String TABLE_NAME="movie";
                public static final String COLUMN_MOVIE_ID="id";
                public static final String COLUMN_MOVIE_TITLE = "original_title";
                public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
                public static final String COLUMN_MOVIE_OVERVIEW = "overview";
                public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
                public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
*/
            favFilms.add(new Film(data.getString(data.getColumnIndex("poster_path")),
                    data.getString(data.getColumnIndex("overview")),
                    data.getString(data.getColumnIndex("vote_average")),
                    data.getString(data.getColumnIndex("original_title")),
                    data.getString(data.getColumnIndex("release_date")),
                    data.getString(data.getColumnIndex("id"))));
        }
        if (favFilms != null) {
            Toast.makeText(MainActivity.this, " favourites loaded with no errors", Toast.LENGTH_SHORT).show();
            showFilmDataView();
            mAdapter.setFilmData(favFilms);
        } else {
            showErrorMessage();

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setFilmData(null);
    }


    public class FetchFilmTask extends AsyncTask<String, Void, ArrayList<Film>> {

        @Override
        protected void onPreExecute() {
            Log.v("MainActivity", "ON PRE EXECUTE LAUNCHED    ");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Film> doInBackground(String... params) {
            Log.v("MainActivity", params[0]);
            Uri builtUri = Uri.parse(params[0])
                    .buildUpon()
                    .appendQueryParameter(API_KEY, "002ed60f63b250ec738584988bb5b97f")

                    .build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            if (params.length == 0) {
                return null;
            }

            txtJsonToParse = new JSONTask().JSONTask(url);

            Log.v("MAIN ACTIVITY", "txtJsonToParse = " + txtJsonToParse);
            if (txtJsonToParse != null) {
                try {
                    JSONObject baseJSonResponse = new JSONObject(txtJsonToParse);
                    JSONArray listOfFilms = baseJSonResponse.getJSONArray("results");

                    ArrayList<Film> films = new ArrayList<>();

                    for (int i = 0; i < listOfFilms.length(); i++) {
                        JSONObject currentMovie = listOfFilms.getJSONObject(i);
                        String thumbnailUrl = CONST_DOMAIN_FOR_THUMBNAIL + currentMovie.getString("poster_path");
                        String overview = currentMovie.getString("overview");
                        String rating = currentMovie.getString("vote_average") + ("/10");
                        String title = currentMovie.getString("title");
                        String releaseDate = currentMovie.getString("release_date");
                        String id = currentMovie.getString("id");
                        films.add(new Film(thumbnailUrl, overview, rating, title, releaseDate, id));

                    }

                    return films;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Film> films) {
            super.onPostExecute(films);
            Log.v("POST EXECUTE", "films = " + films);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (films != null) {
                showFilmDataView();
                mAdapter.setFilmData(films);

            } else {
                showErrorMessage();

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.v("ITEM SELECTED", "ITEM ID: " + id);
        if (id == R.id.menuSortPopular) {
            mAdapter.setFilmData(null);
            SORT_BY = "popular";
            new FetchFilmTask().execute(FILMREQUEST_BASE_URL + SORT_BY);
        }
        if (id == R.id.menuSortRating) {
            mAdapter.setFilmData(null);
            SORT_BY = "top_rated";
            new FetchFilmTask().execute(FILMREQUEST_BASE_URL + SORT_BY);
        } else if(id == R.id.menuFavourites){
            isFavo = true;
            mAdapter.setFilmData(null);


            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);

        }
        return super.onOptionsItemSelected(item);
    }

    Parcelable mListState;
    String LIST_STATE_KEY="save";
    @Override
    protected void onSaveInstanceState(Bundle state){
        super.onSaveInstanceState(state);

        mListState=mLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY,mListState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
            if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
                mLayoutManager = new GridLayoutManager(this, 2);
            }else mLayoutManager = new GridLayoutManager(this,3);
        }
    }

}





