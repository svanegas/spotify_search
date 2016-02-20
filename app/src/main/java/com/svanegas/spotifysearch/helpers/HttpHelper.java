package com.svanegas.spotifysearch.helpers;

import android.net.Uri;

import java.util.Map;

public class HttpHelper {

  private static final String SCHEME = "https";
  private static final String PREFIX = "v1";
  private static final String URL = "api.spotify.com";

  public static String buildUrl(Map<String, String> queries, String... paths) {
    Uri.Builder builder = new Uri.Builder();
    builder.scheme(SCHEME)
            .authority(URL)
            .appendPath(PREFIX);
    if (paths != null) {
      for (String path : paths) {
        if (path != null) builder.appendPath(path);
      }
    }
    if (queries != null) {
      for (Map.Entry<String, String> query : queries.entrySet()) {
        String key = query.getKey();
        String value = query.getValue();
        builder.appendQueryParameter(key, value);
      }
    }
    return builder.build().toString();
  }
}
