package it.unimib.sal.one_two_trip.model;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;
@Entity
public class News implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String author;
    private String title;
    @Embedded(prefix = "source_")
    private NewsSource source;
    private String description;
    private String url;
    @ColumnInfo(name = "url_to_image")
    private String urlToImage;
    @SerializedName("publishedAt")
    @ColumnInfo(name = "published_at")
    private String date;
    private String content;

    @ColumnInfo(name = "is_favorite")
    private boolean isFavorite;

    @ColumnInfo(name = "is_synchronized")
    private boolean isSynchronized;

    public News() {}

    public News(String author, String title, NewsSource source, String description, String url,
                String urlToImage, String date, String content, boolean isFavorite, boolean isSynchronized) {
        this.author = author;
        this.title = title;
        this.source = source;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.date = date;
        this.content = content;
        this.isFavorite = isFavorite;
        this.isSynchronized = isSynchronized;
    }

    public News(String author, String title, NewsSource source, String date) {
        this(author, title, source, null, null, null, date,
                null, false, false);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NewsSource getSource() {
        return source;
    }

    public void setSource(NewsSource source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Exclude
    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", source=" + source +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", urlToImage='" + urlToImage + '\'' +
                ", date='" + date + '\'' +
                ", content='" + content + '\'' +
                ", isFavorite=" + isFavorite +
                ", isSynchronized=" + isSynchronized +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(author, news.author) && Objects.equals(title, news.title) && Objects.equals(source, news.source) && Objects.equals(description, news.description) && Objects.equals(url, news.url) && Objects.equals(urlToImage, news.urlToImage) && Objects.equals(date, news.date) && Objects.equals(content, news.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, title, source, description, url, urlToImage, date, content);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.author);
        dest.writeString(this.title);
        dest.writeParcelable(this.source, flags);
        dest.writeString(this.description);
        dest.writeString(this.url);
        dest.writeString(this.urlToImage);
        dest.writeString(this.date);
        dest.writeString(this.content);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSynchronized ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.author = source.readString();
        this.title = source.readString();
        this.source = source.readParcelable(NewsSource.class.getClassLoader());
        this.description = source.readString();
        this.url = source.readString();
        this.urlToImage = source.readString();
        this.date = source.readString();
        this.content = source.readString();
        this.isFavorite = source.readByte() != 0;
        this.isSynchronized = source.readByte() != 0;
    }

    protected News(Parcel in) {
        this.id = in.readLong();
        this.author = in.readString();
        this.title = in.readString();
        this.source = in.readParcelable(NewsSource.class.getClassLoader());
        this.description = in.readString();
        this.url = in.readString();
        this.urlToImage = in.readString();
        this.date = in.readString();
        this.content = in.readString();
        this.isFavorite = in.readByte() != 0;
        this.isSynchronized = in.readByte() != 0;
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

}
