package com.example.tvprogram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvprogram.R;
import com.example.tvprogram.adapters.EpisodesAdapter;
import com.example.tvprogram.adapters.ImageSliderAdapter;
import com.example.tvprogram.databinding.ActivityTvshowDetailsBinding;
import com.example.tvprogram.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.tvprogram.models.TVShow;
import com.example.tvprogram.utilities.TempDataHolder;
import com.example.tvprogram.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitalization();
    }

    private void doInitalization(){
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShows");
        checkTVShowInWatchlist();
       getTVShowDetails();
    }

    private void checkTVShowInWatchlist(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTVShowAvailableInWatchlist = true;
                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                }));
    }

    private void getTVShowDetails(){
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());//String.valueOf(getIntent().getIntExtra("id",-1));
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(
                this, tvShowDetailsResponse -> {
                    activityTvshowDetailsBinding.setIsLoading(false);
                        if(tvShowDetailsResponse.getTvShowDetails() != null) {
                            if (tvShowDetailsResponse.getTvShowDetails().getPicture() != null) {
                                loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPicture());
                            }
                            activityTvshowDetailsBinding.setTvShowImageURL(
                                    tvShowDetailsResponse.getTvShowDetails().getImagePath()
                            );
                            activityTvshowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.setDescription(
                                    String.valueOf(
                                            HtmlCompat.fromHtml(
                                                    tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                                    HtmlCompat.FROM_HTML_MODE_LEGACY
                                            )
                                    )
                            );
                            activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.textReadMore.setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onClick(View view) {
                                    if(activityTvshowDetailsBinding.textReadMore.getText().toString().equals("Read More")){
                                        activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                        activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_less);
                                    }else{
                                        activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                                        activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                        activityTvshowDetailsBinding.textReadMore.setText(R.string.read_more);
                                    }
                                }
                            });
                            activityTvshowDetailsBinding.setRating(
                                    String.format(
                                            Locale.getDefault(),
                                            "%.2f",
                                            Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                                    )
                            );
                            if(tvShowDetailsResponse.getTvShowDetails().getGenre() != null){
                                activityTvshowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenre()[0]);
                            }else{
                                activityTvshowDetailsBinding.setGenre("N/A");
                            }
                            activityTvshowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + "Min");
                            activityTvshowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                                    startActivity(intent);
                                }
                            });
                            activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                            activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(episodesBottomSheetDialog == null){
                                        episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                                LayoutInflater.from(TVShowDetailsActivity.this),
                                                R.layout.layout_episodes_bottom_sheet,
                                                findViewById(R.id.episodesContainer),
                                                false
                                        );
                                        episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                        layoutEpisodesBottomSheetBinding.episodesRecycleView.setAdapter(
                                                new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                                        );
                                        layoutEpisodesBottomSheetBinding.textTitle.setText(
                                                String.format("Episodes | %s", tvShow.getName())
                                        );
                                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                episodesBottomSheetDialog.dismiss();
                                            }
                                        });
                                        FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                                com.google.android.material.R.id.design_bottom_sheet
                                        );
                                        if(frameLayout != null){
                                            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        }
                                        episodesBottomSheetDialog.show();
                                    }
                                }
                            });
                            activityTvshowDetailsBinding.imageWatchlist.setOnClickListener(view ->{
                                CompositeDisposable compositeDisposable = new CompositeDisposable();
                                if(isTVShowAvailableInWatchlist){
                                compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVShowAvailableInWatchlist = false;
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_watchlist);
                                            Toast.makeText(getApplicationContext(),"Removed from watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        }));
                                }else {
                                    compositeDisposable.add(tvShowDetailsViewModel.addToWatchlist(tvShow)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                                activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                                Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_LONG).show();
                                                compositeDisposable.dispose();
                                            })
                                    );
                                }
                            });
                            activityTvshowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                            loadBasicTVShowDetails();
                        }
                }
        );
    }

    private void loadImageSlider(String[] sliderImages){
        activityTvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImages.length);
        activityTvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }
    private void setupSliderIndicators(int count){
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i=0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }
    private void setCurrentSliderIndicator(int position){
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicators.getChildCount();
        for(int i=0; i<childCount;i++){
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if(i==position){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            }else{
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }
    private void loadBasicTVShowDetails(){
        activityTvshowDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + "(" +
                        tvShow.getCountry() + ")"
        );
        activityTvshowDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailsBinding.setStartDate(tvShow.getStartDate());
        activityTvshowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStarted.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
    }
}