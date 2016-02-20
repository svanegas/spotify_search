package com.svanegas.spotifysearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Image implements Parcelable {

  private int width;
  private int height;
  private String url;

  public Image() {

  }

  public Image(Parcel in) {
    readFromParcel(in);
  }

  public Image(JSONObject image) throws JSONException {
    this.width = image.getInt("height");
    this.height = image.getInt("width");
    this.url = image.getString("url");
  }

  private void readFromParcel(Parcel in) {
    width = in.readInt();
    height = in.readInt();
    url = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(width);
    dest.writeInt(height);
    dest.writeString(url);
  }

  public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
    public Image createFromParcel(Parcel in) {
      return new Image(in);
    }

    public Image[] newArray(int size) {
      return new Image[size];
    }
  };

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
