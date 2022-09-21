package com.example.tvprogram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.tvprogram.R;
import com.example.tvprogram.adapters.WatchlistAdapters;
import com.example.tvprogram.databinding.ActivityWatchlistBinding;
import com.example.tvprogram.listeners.WatchlistListener;
import com.example.tvprogram.models.TVShow;
import com.example.tvprogram.utilities.TempDataHolder;
import com.example.tvprogram.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistViewModel viewModel;
    private WatchlistAdapters watchlistAdapters;
    private List<TVShow> watchlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this,R.layout.activity_watchlist);
        doInitialization();
    }

    private void doInitialization(){
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.imageBack.setOnClickListener(view -> onBackPressed());
        watchlist = new ArrayList<>();
        loadWatchlist();
    }

    private void loadWatchlist(){
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchlist().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    activityWatchlistBinding.setIsLoading(false);
                  if(watchlist.size() > 0){
                      watchlist.clear();
                  }
                  watchlist.addAll(tvShows);
                  watchlistAdapters = new WatchlistAdapters(watchlist, this);
                  activityWatchlistBinding.watchlistRecyclerView.setAdapter(watchlistAdapters);
                  activityWatchlistBinding.watchlistRecyclerView.setVisibility(View.VISIBLE);
                  compositeDisposable.dispose();
                }));
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
    Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
    intent.putExtra("tvShows", tvShow);
    startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
    CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
    compositeDisposableForDelete.add(viewModel.removeTVShowFromWatchlist(tvShow)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() ->{
                watchlist.remove(position);
                watchlistAdapters.notifyItemRemoved(position);
                watchlistAdapters.notifyItemRangeChanged(position, watchlistAdapters.getItemCount());
                compositeDisposableForDelete.dispose();
            }));
    }
}