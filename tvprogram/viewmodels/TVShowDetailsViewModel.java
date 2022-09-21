package com.example.tvprogram.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tvprogram.database.TVShowDatabase;
import com.example.tvprogram.models.TVShow;
import com.example.tvprogram.repositories.TVShowDetailsRepository;
import com.example.tvprogram.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowDatabase tvShowDatabase;

    public TVShowDetailsViewModel(@NonNull Application application){
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId){
        return  tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().addToWatchlist(tvShow);
    }

    public Flowable<TVShow>getTVShowFromWatchlist(String tvShowId){
        return tvShowDatabase.tvShowDao().getTVShowFromWatchlist(tvShowId);
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow){
        return tvShowDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }

}
