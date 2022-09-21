package com.example.tvprogram.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import com.example.tvprogram.R;
import com.example.tvprogram.adapters.TVShowAdapters;
import com.example.tvprogram.databinding.ActivityMainBinding;
import com.example.tvprogram.listeners.TVShowListener;
import com.example.tvprogram.models.TVShow;
import com.example.tvprogram.viewmodels.MostPopularTVShowsViewModel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShow = new ArrayList<>();
    private TVShowAdapters tvShowAdapters;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
         getMostPopularTVShows();
         doInitialization();
    }

    private void doInitialization(){
        activityMainBinding.tvShowsRecycleView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowAdapters = new TVShowAdapters(tvShow, this);
        activityMainBinding.tvShowsRecycleView.setAdapter(tvShowAdapters);
       activityMainBinding.tvShowsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
           @Override
           public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               if(!activityMainBinding.tvShowsRecycleView.canScrollVertically(1)){
                   if(currentPage <= totalAvailablePages){
                       currentPage += 1;
                       getMostPopularTVShows();
                   }
               }
           }
       });
       activityMainBinding.imageWatchlist.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), WatchlistActivity.class)));
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows(){
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowsResponse ->{
            toggleLoading();
            if(mostPopularTVShowsResponse != null) {
                totalAvailablePages = mostPopularTVShowsResponse.getTotalPages();
                if (mostPopularTVShowsResponse.getTvShows() != null) {
                    int oldCount = tvShow.size();
                    tvShow.addAll(mostPopularTVShowsResponse.getTvShows());
                    tvShowAdapters.notifyItemRangeInserted(oldCount, tvShow.size());
                }
            }
        });
    }

    private void toggleLoading(){
        if(currentPage == 1){
        if(activityMainBinding.getIsLoading() != null && activityMainBinding.getIsLoading()){
            activityMainBinding.setIsLoading(false);
        }else{
            activityMainBinding.setIsLoading(true);
        }
        }else{
            if(activityMainBinding.getIsLoadingMore() != null && activityMainBinding.getIsLoadingMore()){
                activityMainBinding.setIsLoadingMore(false);
            }else{
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
            intent.putExtra("tvShows", tvShow);
            startActivity(intent);
    }
}