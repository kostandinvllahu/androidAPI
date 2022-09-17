package com.example.tvprogram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvprogram.repositories.MostPopularTVShowsRepository;
import com.example.tvprogram.responses.TVShowsResponses;

public class MostPopularTVShowsViewModel extends ViewModel {

    private MostPopularTVShowsRepository mostPopularTVShowsRepository;

    public MostPopularTVShowsViewModel(){
        mostPopularTVShowsRepository = new MostPopularTVShowsRepository();
    }

    public LiveData<TVShowsResponses> getMostPopularTVShows(int page){
        return  mostPopularTVShowsRepository.getMostPopularTVShows(page);
    }
}
