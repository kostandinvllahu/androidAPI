package com.example.tvprogram.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.tvprogram.database.TVShowDatabase;
import com.example.tvprogram.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchlistViewModel extends AndroidViewModel {

    private TVShowDatabase tvShowDatabase;

    public WatchlistViewModel(@NonNull Application application){
        super(application);
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }
    public Flowable<List<TVShow>> loadWatchlist(){
        return tvShowDatabase.tvShowDao().getWatchlist();
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
