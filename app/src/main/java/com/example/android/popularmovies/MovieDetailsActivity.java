package com.example.android.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.popularmovies.databinding.ActivityMovieDetailsBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * No need to handle {@link #onSaveInstanceState(Bundle)} as rubric says so
 * Also as it will store same data as intent
 * so, In my opinion it will be duplicate-like code with same efficiency.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    public static final String INTENT_KEY_MOVIE_OBJECT = "INTENT_KEY_MOVIE_OBJECT";

    private ActivityMovieDetailsBinding binding;

    /** When get back to {@link MainActivity}, Loader won't make request for movies if true */
    public static final String INTENT_KEY_DO_NOT_REFRESH_MOVIES = "INTENT_KEY_DO_NOT_REFRESH_MOVIES";

    private boolean doNotRefreshMovies = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        // now you can handle up click in onOptionItemSelected(MenuItem) android.R.id.home
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null
                && intent.hasExtra(INTENT_KEY_MOVIE_OBJECT)){
            Movie movie = intent.getParcelableExtra(INTENT_KEY_MOVIE_OBJECT);

            if (intent.hasExtra(INTENT_KEY_DO_NOT_REFRESH_MOVIES)){
                doNotRefreshMovies = intent.getBooleanExtra(
                        INTENT_KEY_DO_NOT_REFRESH_MOVIES, false);
            }

            populateUi(movie);
        }else {
            String detailedError;
            if (intent == null)
                detailedError = "null Intent";
            else {
                detailedError = "intent doesn't have INTENT_KEY_MOVIE_OBJECT";
            }

            Log.e(MovieDetailsActivity.class.getName(), detailedError);
            // inform user error has occurred that's why no details showed
            Toast.makeText(this, "Error Occurred Showing Movie Details",
                    Toast.LENGTH_SHORT).show();

            finish();
        }
    }

    private void populateUi(Movie movie){
        String originalTitle = movie.getOriginalTitle();
        if (originalTitle == null || originalTitle.isEmpty())
            originalTitle = "Unknown Title";
        setTitle(originalTitle);

        String fullPosterPath = NetworkUtils.fullPosterPathFromThirdPartPath(movie.getPosterPath());
        Picasso.with(this)
                .load(fullPosterPath)
                .error(getResources().getDrawable(R.drawable.error_poster_image_load))
                .into(binding.posterImageView);

        binding.releaseDate.setText(polishReleaseDate(movie.getReleaseDate()));

        setupRating(movie.getVoteAverage());

        String overview = movie.getOverview();
        if (overview != null)
            binding.overview.setText(overview);
    }

    /** {@link #populateUi(Movie)} Methods */

    private String polishReleaseDate(String releaseDate){
        releaseDate = releaseDate.replaceAll("-", " / ");

        return releaseDate;
    }

    private void setupRating(double originalRating){
        // check data validity
        if (originalRating > 10 || originalRating < 0)
            return;
        // rating in API max(10) in stars view I made max(5) so divide by 2
        double rating = originalRating /  2;
        // round to nearest 1 decimal
        rating = (double) Math.round(rating * 10) / 10;
        // first show exact number in rating label
        String ratingLabelWIthValue = getString(R.string.rating_label) + " " + rating;
        binding.ratingLabel.setText(ratingLabelWIthValue);
        // then stars view giving ratingLabelWIthValue certainly in format #.#
        // any filled or half filled colored as colorPrimaryDark, otherwise black for better UI
        String ratingString = String.valueOf(rating);
        int starsNumber = Integer.parseInt(ratingString.substring(0, 1));
        int halfStar = Integer.parseInt(ratingString.substring(2));

        ImageView imageView;
        int halfStarIndex = 0; // initiated as zero case if rate was 0.5 / 5
        Drawable drawable;
        int srcColor;
        for (int i = 0; i < starsNumber; i++){
            imageView = (ImageView) binding.ratingContainerLinearLayout.getChildAt(i);

            drawable = getResources().getDrawable(R.drawable.ic_star_black_24dp);
            srcColor = getResources().getColor(R.color.colorPrimaryDark);
            drawable.setColorFilter(srcColor, PorterDuff.Mode.SRC_ATOP);
            imageView.setImageDrawable(drawable);

            halfStarIndex = i + 1;
        }

        if (halfStar >= 5){
            imageView = (ImageView) binding.ratingContainerLinearLayout.getChildAt(halfStarIndex);

            drawable = getResources().getDrawable(R.drawable.ic_star_half_black_24dp);
            srcColor = getResources().getColor(R.color.colorPrimaryDark);
            drawable.setColorFilter(srcColor, PorterDuff.Mode.SRC_ATOP);
            imageView.setImageDrawable(drawable);
        }
    }

    // ---- menu setups

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (doNotRefreshMovies){
                    // no loading will happen again, as it already has been loaded before
                    onBackPressed();
                }else {
                    // Below code makes onCreate() in MainActivity be called again
                    // so, Data will be reloaded from internet (from API)
                    NavUtils.navigateUpFromSameTask(this);

                    // Note this might be needed for ex. if another app launches this activity
                    // to show movie details so when get back to MainActivity you will need
                    // to load data from API as there was no data there
                    // I know that doesn't happen here but that is for general rule handling
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
