package com.shoji.example.android.popularmoviesstage1.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieData implements Parcelable {
    private String themoviedb_id;
    private String vote_average;
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;
    private String duration;
    private int isFavorite;

    public MovieData() {}




    private MovieData(Parcel in) {
        themoviedb_id = in.readString();
        vote_average = in.readString();
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        duration = in.readString();
        isFavorite = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(themoviedb_id);
        dest.writeString(vote_average);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(duration);
        dest.writeInt(isFavorite);
    }

    public static final Creator<MovieData> CREATOR = new Creator<MovieData>() {
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() { return themoviedb_id; }

    public void setId(String id) { this.themoviedb_id = id; }

    public String getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    /* auto generated equals method */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieData movieData = (MovieData) o;

        return themoviedb_id.equals(movieData.themoviedb_id);
    }

    @Override
    public int hashCode() {
        return themoviedb_id.hashCode();
    }

    @Override
    public String toString() {
        return "Title: " + title +
                "\nID: " + themoviedb_id +
                "\nPoster Path: " + poster_path +
                "\nOverview: " + overview +
                "\nVote Average: "+ vote_average +
                "\nRelease Date: "+ release_date +
                "\nDuration (min): "+ duration +
                "\nisFavorite: "+ isFavorite;
    }
}
