package it.unimib.sal.one_two_trip.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResponse implements Parcelable {
    private boolean isLoading;

    @SerializedName("articles")
    private List<News> newsList;

    public NewsResponse() {}

    public NewsResponse(List<News> newsList) {
        this.newsList = newsList;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public String toString() {
        return "NewsResponse{" +
                "newsList=" + newsList +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isLoading ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.newsList);
    }

    public void readFromParcel(Parcel source) {
        this.isLoading = source.readByte() != 0;
        this.newsList = source.createTypedArrayList(News.CREATOR);
    }

    protected NewsResponse(Parcel in) {
        this.isLoading = in.readByte() != 0;
        this.newsList = in.createTypedArrayList(News.CREATOR);
    }

    public static final Parcelable.Creator<NewsResponse> CREATOR = new Parcelable.Creator<NewsResponse>() {
        @Override
        public NewsResponse createFromParcel(Parcel source) {
            return new NewsResponse(source);
        }

        @Override
        public NewsResponse[] newArray(int size) {
            return new NewsResponse[size];
        }
    };

}
