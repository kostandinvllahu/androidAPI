package com.example.tvprogram.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowDetails {
   @SerializedName("url")
   private String url;

   @SerializedName("description")
   private String description;

   @SerializedName("runtime")
   private String runtime;

   @SerializedName("image_path")
   private String imagePath;

   @SerializedName("rating")
   private String rating;

   @SerializedName("genres")
   private String[] genre;

   @SerializedName("picture")
   private String[] picture;

   @SerializedName("episodes")
   private List<Episode> episodes;

   public String getUrl() {
      return url;
   }

   public String getDescription() {
      return description;
   }

   public String getRuntime() {
      return runtime;
   }

   public String getImagePath() {
      return imagePath;
   }

   public String getRating() {
      return rating;
   }

   public String[] getGenre() {
      return genre;
   }

   public String[] getPicture() {
      return picture;
   }

   public List<Episode> getEpisodes() {
      return episodes;
   }
}
