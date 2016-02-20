package com.svanegas.spotifysearch.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.svanegas.spotifysearch.R;
import com.svanegas.spotifysearch.exceptions.EmptyResponseException;
import com.svanegas.spotifysearch.helpers.FeedbackHelper;
import com.svanegas.spotifysearch.helpers.HttpHelper;
import com.svanegas.spotifysearch.models.Artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private static String TAG = MainActivity.class.getSimpleName();

  private CoordinatorLayout coordinatorLayout;
  private Toolbar toolbar;
  private EditText searchText;
  private Button searchButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    searchText = (EditText) findViewById(R.id.search_text);
    searchButton = (Button) findViewById(R.id.search_button);

    searchButton.setOnClickListener(this);
  }

  private void retrieveArtists(String query) {
    FeedbackHelper.showDialog(this);
    Map<String, String> queries = new HashMap<String, String>();
    queries.put("type", "artist");
    queries.put("query", query);

    String url = HttpHelper.buildUrl(queries, "search");

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
                                                         successResponse, errorResponse);

    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(jsonObjReq);
  }

  private Artist parseArtist(JSONObject response) {
    try {
      JSONObject artists = response.getJSONObject("artists");
      JSONArray items = artists.getJSONArray("items");

      if (items.length() == 0) {
        throw new EmptyResponseException(getResources().getString(R.string.no_results));
      }

      Artist artist = new Artist();
      JSONObject artistJson = items.getJSONObject(0);
      artist.setId(artistJson.getString("id"));
      artist.setHref(artistJson.getString("href"));
      artist.setName(artistJson.getString("name"));
      artist.setPopularity(artistJson.getInt("popularity"));
      artist.setType(artistJson.getString("type"));
      artist.setUri(artistJson.getString("uri"));
      artist.setJSONImages(artistJson.getJSONArray("images"));
      artist.setFollowers(artistJson.getJSONObject("followers").getInt("total"));
      return artist;
    } catch (JSONException je) {
      je.printStackTrace();
      FeedbackHelper.showSnack(coordinatorLayout, getResources().getString(R.string.generic_error));
    }
    catch (EmptyResponseException er) {
      er.printStackTrace();
      FeedbackHelper.showSnack(coordinatorLayout, er.getMessage());
    }
    FeedbackHelper.hideDialog();
    return null;
  }

  private void goToArtist(Artist artist) {
    Intent intent = new Intent(this, ArtistActivity.class);
    intent.putExtra("artist", artist);
    startActivity(intent);
    FeedbackHelper.hideDialog();
  }

  Response.Listener<JSONObject> successResponse = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
      Artist artist = parseArtist(response);
      if (artist != null) goToArtist(artist);
    }
  };

  Response.ErrorListener errorResponse = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      FeedbackHelper.hideDialog();
      FeedbackHelper.showSnack(coordinatorLayout,
                               getResources().getString(R.string.could_not_process_request));
    }
  };

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.search_button:
        retrieveArtists(searchText.getText().toString().trim());
        break;
    }
  }
}
