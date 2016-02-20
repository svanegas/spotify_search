package com.svanegas.spotifysearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Artist implements Parcelable {

  private String id;
  private String href;
  private List<Image> images;
  private String name;
  private int popularity;
  private String type;
  private String uri;
  private int followers;


  public Artist() {

  }

  public Artist(Parcel in) {
    readFromParcel(in);
  }

  public String getMediumImageURL() {
    if (images != null && !images.isEmpty()) {
      if (images.size() > 1) return images.get(1).getUrl();
      else return images.get(0).getUrl();
    }
    return "";
  }

  public void readFromParcel(Parcel in) {
    images = new ArrayList<>();

    id = in.readString();
    href = in.readString();
    in.readTypedList(images, Image.CREATOR);
    name = in.readString();
    popularity = in.readInt();
    type = in.readString();
    uri = in.readString();
    followers = in.readInt();
  }

  public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
    public Artist createFromParcel(Parcel in) {
      return new Artist(in);
    }

    public Artist[] newArray(int size) {
      return new Artist[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(href);
    dest.writeTypedList(images);
    dest.writeString(name);
    dest.writeInt(popularity);
    dest.writeString(type);
    dest.writeString(uri);
    dest.writeInt(followers);
  }

  public void setJSONImages(JSONArray images) throws JSONException {
    this.images = new ArrayList<>();
    for (int i = 0; i < images.length(); ++i) {
      JSONObject image = images.getJSONObject(i);
      this.images.add(new Image(image));
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public List<Image> getImages() {
    return images;
  }

  public void setImages(List<Image> images) {
    this.images = images;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPopularity() {
    return popularity;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public int getFollowers() {
    return followers;
  }

  public void setFollowers(int followers) {
    this.followers = followers;
  }
}
