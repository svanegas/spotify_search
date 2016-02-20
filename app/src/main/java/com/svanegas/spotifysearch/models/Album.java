package com.svanegas.spotifysearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable {

  private String id;
  private String href;
  private List<Image> images;
  private String name;
  private String type;
  private String uri;
  private List <Country> availableCountries;
  private String externalUrl;

  public Album() {

  }

  public Album(Parcel in) {
    readFromParcel(in);
  }

  public String getMediumImageURL() {
    if (images != null && !images.isEmpty()) {
      if (images.size() > 1) return images.get(1).getUrl();
      else return images.get(0).getUrl();
    }
    return "";
  }

  public void setJSONImages(JSONArray images) throws JSONException {
    this.images = new ArrayList<>();
    for (int i = 0; i < images.length(); ++i) {
      JSONObject image = images.getJSONObject(i);
      this.images.add(new Image(image));
    }
  }

  public void setJSONCountries(JSONArray countries) throws JSONException {
    this.availableCountries = new ArrayList<>();
    for (int i = 0; i < countries.length(); ++i) {
      String countryCode = countries.getString(i);
      this.availableCountries.add(new Country(countryCode));
    }
  }

  public void readFromParcel(Parcel in) {
    images = new ArrayList<>();
    availableCountries = new ArrayList<>();

    id = in.readString();
    href = in.readString();
    in.readTypedList(images, Image.CREATOR);
    name = in.readString();
    type = in.readString();
    uri = in.readString();
    in.readTypedList(availableCountries, Country.CREATOR);
    externalUrl = in.readString();
  }


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
    dest.writeString(type);
    dest.writeString(uri);
    dest.writeTypedList(availableCountries);
    dest.writeString(externalUrl);
  }

  public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
    public Album createFromParcel(Parcel in) {
      return new Album(in);
    }

    public Album[] newArray(int size) {
      return new Album[size];
    }
  };

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

  public List<Country> getAvailableCountries() {
    return availableCountries;
  }

  public void setAvailableCountries(List<Country> availableCountries) {
    this.availableCountries = availableCountries;
  }

  public String getExternalUrl() {
    return externalUrl;
  }

  public void setExternalUrl(String externalUrl) {
    this.externalUrl = externalUrl;
  }
}
