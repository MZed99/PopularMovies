package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Intent.ACTION_VIEW;
import static android.view.View.VISIBLE;

public class FilmDetails extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private TextView mOverviewTextView;
    private TextView mTitleTextView;
    private ImageView mThumbnailImageView;
    private TextView mUsersRateTextView;
    private TextView mReleaseDateTextView;
    private Button mAddtoFavButton;
    private RecyclerView mReviewsRV, mTrailersRV;
    private ArrayList<Review> mReviews;
    private TextView mErrorTextView, mTrailerErrorTextView;
    private ProgressBar mLoadingProgressBarR, mLoadingProgressBarT;
    private String FILMREQUEST_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private String API_KEY = "api_key";
    private URL urlReviews, urlTrailer;
    private String txtJsonToParseReview, txtJsonToParseTrailer;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    private LinearLayoutManager mLayoutManager, mLayoutManagerT;

    private Film filmDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);

        mErrorTextView = (TextView) findViewById(R.id.reviews_error_message);
        mLoadingProgressBarR = (ProgressBar) findViewById(R.id.rev_loading_indicator);
        mLoadingProgressBarT = (ProgressBar) findViewById(R.id.tra_loading_indicator);
        mReviews = new ArrayList<>();
        mOverviewTextView = (TextView) findViewById(R.id.film_overview);
        mThumbnailImageView = (ImageView) findViewById(R.id.thumbnail_details);
        mTitleTextView = (TextView) findViewById(R.id.title_details);
        mUsersRateTextView = (TextView) findViewById(R.id.film_rating);
        mReleaseDateTextView = (TextView) findViewById(R.id.film_release);
        mTrailerErrorTextView = (TextView) findViewById(R.id.trailers_error_message);

        Intent intentThatStarted = getIntent();
        filmDetails = intentThatStarted.getExtras().getParcelable("film");

        mOverviewTextView.setText(filmDetails.getOverview());
        Picasso.with(this).load(filmDetails.getThumbnailString()).fit().into(mThumbnailImageView);
        mTitleTextView.setText(filmDetails.getTitle());
        mUsersRateTextView.setText(filmDetails.getUsersRate());
        mUsersRateTextView.setText(filmDetails.getUsersRate());
        mReleaseDateTextView.setText(filmDetails.getReleaseDate());
        mAddtoFavButton = (Button) findViewById(R.id.add_favourite_button);

        mTrailersRV = (RecyclerView) findViewById(R.id.trailers_recycler_view);
        mTrailerAdapter = new TrailerAdapter(this, this);
        mLayoutManagerT = new LinearLayoutManager(this);
        mTrailersRV.setLayoutManager(mLayoutManagerT);
        mTrailersRV.setAdapter(mTrailerAdapter);

        mLayoutManager = new LinearLayoutManager(this);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewsRV = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        mReviewsRV.setLayoutManager(mLayoutManager);
        mReviewsRV.setAdapter(mReviewAdapter);

        new FetchTrailersTask().execute(FILMREQUEST_BASE_URL);
        new FetchReviewsTask().execute(FILMREQUEST_BASE_URL);
    }

    public void onClickFavourites(View view) {

        if (mAddtoFavButton.getText().toString() == getString(R.string.add_fav)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, filmDetails.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, filmDetails.getId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, filmDetails.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, filmDetails.getUsersRate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, filmDetails.getThumbnailString());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, filmDetails.getOverview());

            Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            Toast.makeText(this, "URI: " + uri, Toast.LENGTH_SHORT).show();
            mAddtoFavButton.setText(getString(R.string.added_fav));
        }
    }

    public class FetchReviewsTask extends AsyncTask<String, Void, ArrayList<Review>> {

        @Override
        protected void onPreExecute() {
            Log.v("MainActivity", "ON PRE EXECUTE LAUNCHED    ");
            super.onPreExecute();
            mLoadingProgressBarR.setVisibility(VISIBLE);
        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            Log.v("MainActivity", params[0]);
            Uri builtUriReviews = Uri.parse(params[0])
                    .buildUpon()
                    .appendPath(filmDetails.getId())
                    .appendPath("reviews")
                    .appendQueryParameter(API_KEY, "002ed60f63b250ec738584988bb5b97f")
                    .build();
            try {
                urlReviews = new URL(builtUriReviews.toString());
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            if (params.length == 0) {
                return null;
            }
            Log.v("DO IN BACKGROUND URI: ", urlReviews.toString());
            txtJsonToParseReview = new JSONTask().JSONTask(urlReviews);


            Log.v("MAIN ACTIVITY", "txtJsonToParse = " + txtJsonToParseReview);
            if (txtJsonToParseReview != null) {
                try {
                    JSONObject jsonObject = new JSONObject(txtJsonToParseReview);
                    JSONArray listOfReviews = jsonObject.getJSONArray("results");

                    ArrayList<Review> reviews = new ArrayList<>();

                    for (int i = 0; i < listOfReviews.length(); i++) {
                        JSONObject currentReview = listOfReviews.getJSONObject(i);
                        String author = currentReview.getString("author");
                        String content = currentReview.getString("content");
                        reviews.add(new Review(author, content));

                    }

                    return reviews;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);
            Log.v("POST EXECUTE", "reviews = " + reviews);
            mLoadingProgressBarR.setVisibility(View.INVISIBLE);
            if (reviews != null) {
                showReviewDataView();
                mReviewAdapter.setReviewData(reviews);

            } else {
                showErrorMessage();

            }
        }
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            Log.v("MainActivity", "ON PRE EXECUTE LAUNCHED    ");
            super.onPreExecute();
            mLoadingProgressBarT.setVisibility(VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            Log.v("MainActivity", params[0]);
            Uri builtUriTrailer = Uri.parse(params[0])
                    .buildUpon()
                    .appendPath(filmDetails.getId())
                    .appendPath("trailers")
                    .appendQueryParameter(API_KEY, "002ed60f63b250ec738584988bb5b97f")
                    .build();
            try {
                urlTrailer = new URL(builtUriTrailer.toString());
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            if (params.length == 0) {
                return null;
            }
            Log.v("DO IN BACKGROUND URI: ", urlTrailer.toString());
            txtJsonToParseTrailer = new JSONTask().JSONTask(urlTrailer);


            Log.v("MAIN ACTIVITY", "txtJsonToParse = " + txtJsonToParseReview);
            if (txtJsonToParseTrailer != null) {
                try {
                    JSONObject jsonObject = new JSONObject(txtJsonToParseTrailer);
                    JSONArray listOfTrailers = jsonObject.getJSONArray("youtube");

                    ArrayList<String> trailers = new ArrayList<>();

                    for (int i = 0; i < listOfTrailers.length(); i++) {
                        JSONObject currentTrailer = listOfTrailers.getJSONObject(i);
                        String yturl = currentTrailer.getString("source");
                        trailers.add(yturl);
                    }

                    return trailers;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> trailers) {
            super.onPostExecute(trailers);
            Log.v("POST EXECUTE", "reviews = " + trailers);
            mLoadingProgressBarT.setVisibility(View.INVISIBLE);
            if (trailers != null) {
                showTrailerDataView();
                mTrailerAdapter.setTrailerData(trailers);

            } else {
                showTrailerErrorMessage();

            }
        }
    }


    @Override
    public void onClick(String url) {
        Context context = this;
        Log.v("CASLDKA ASLK :  ", url);
        context.startActivity(new Intent(ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+url)));
    }

    private void showTrailerDataView() {
        mTrailersRV.setVisibility(VISIBLE);
        mLoadingProgressBarT.setVisibility(View.INVISIBLE);
    }

    private void showTrailerErrorMessage() {
        mTrailersRV.setVisibility(View.INVISIBLE);
        mTrailerErrorTextView.setVisibility(VISIBLE);
    }

    private void showReviewDataView() {
        mReviewsRV.setVisibility(VISIBLE);
        mLoadingProgressBarR.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mReviewsRV.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(VISIBLE);
    }


}



