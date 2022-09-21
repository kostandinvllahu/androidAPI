package com.example.tvprogram.listeners;

import com.example.tvprogram.models.TVShow;

public interface WatchlistListener {

    void onTVShowClicked(TVShow tvShow);
    void removeTVShowFromWatchlist(TVShow tvShow, int position);
}
