package com.shoji.example.android.popularmoviesstage1.data;

import android.os.Parcel;
import android.os.Parcelable;

public class YoutubeTrailerData implements Parcelable {
    private String title;
    private String key;

    public YoutubeTrailerData() {}

    protected YoutubeTrailerData(Parcel in) {
        title = in.readString();
        key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<YoutubeTrailerData> CREATOR = new Creator<YoutubeTrailerData>() {
        @Override
        public YoutubeTrailerData createFromParcel(Parcel in) {
            return new YoutubeTrailerData(in);
        }

        @Override
        public YoutubeTrailerData[] newArray(int size) {
            return new YoutubeTrailerData[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Trailer title:"+title+
                "\nkey:"+key;
    }
}
