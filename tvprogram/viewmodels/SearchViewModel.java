package com.example.tvprogram.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvprogram.repositories.SearchTVShowRepository;
import com.example.tvprogram.responses.TVShowsResponses;

public class SearchViewModel extends ViewModel {

    private SearchTVShowRepository searchTVShowRepository;

    public SearchViewModel(){
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowsResponses> searchTVShow(String query, int page){
        return searchTVShowRepository.searchTVShow(query,page);
    }
}
