package com.shoji.example.android.popularmoviesstage1.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReviewData implements Parcelable {
    private String author;
    private String content;

    public MovieReviewData() {}

    protected MovieReviewData(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieReviewData> CREATOR = new Creator<MovieReviewData>() {
        @Override
        public MovieReviewData createFromParcel(Parcel in) {
            return new MovieReviewData(in);
        }

        @Override
        public MovieReviewData[] newArray(int size) {
            return new MovieReviewData[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String name) {
        this.author = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Movie review author: "+author+
             "\n            content: "+content;
    }
}
