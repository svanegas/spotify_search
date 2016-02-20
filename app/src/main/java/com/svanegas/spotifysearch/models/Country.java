package com.svanegas.spotifysearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {

  private String code;

  public Country() {

  }

  public Country(String code) {
    this.code = code;
  }

  public Country(Parcel in) {
    readFromParcel(in);
  }

  private void readFromParcel(Parcel in) {
    code = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(code);
  }

  public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
    public Country createFromParcel(Parcel in) {
      return new Country(in);
    }

    public Country[] newArray(int size) {
      return new Country[size];
    }
  };

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
