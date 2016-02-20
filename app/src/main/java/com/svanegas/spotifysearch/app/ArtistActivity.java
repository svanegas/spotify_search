package com.svanegas.spotifysearch.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.svanegas.spotifysearch.R;
import com.svanegas.spotifysearch.exceptions.EmptyResponseException;
import com.svanegas.spotifysearch.helpers.FeedbackHelper;
import com.svanegas.spotifysearch.helpers.HttpHelper;
import com.svanegas.spotifysearch.models.Album;
import com.svanegas.spotifysearch.models.Artist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ArtistActivity extends AppCompatActivity {

  private static String TAG = ArtistActivity.class.getSimpleName();

  private ImageLoader imageLoader;

  private CoordinatorLayout coordinatorLayout;
  private Toolbar toolbar;

  private NetworkImageView artistImage;
  private TextView popularity;
  private TextView followers;

  private Artist artist;

  private RecyclerView recyclerView;
  private AlbumsAdapter albumsAdapter;
  private List<Album> albums = new ArrayList<>();

  private String nextUrl = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_artist);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

    imageLoader = AppController.getInstance().getImageLoader();

    artist = getIntent().getParcelableExtra("artist");

    toolbar.setTitle(artist.getName());
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    populateArtistInfo(artist);

    populateAlbums();
  }

  private void populateArtistInfo(Artist artist) {
    artistImage = (NetworkImageView) findViewById(R.id.artist_image);
    popularity = (TextView) findViewById(R.id.popularity);
    followers = (TextView) findViewById(R.id.followers);

    setArtistImage(artist.getMediumImageURL());
    popularity.setText(String.valueOf(artist.getPopularity() + "%"));
    computeFollowers(artist.getFollowers());
  }

  private void computeFollowers(int followers) {
    if (followers < 1000) {
      this.followers.setText(String.valueOf(followers));
    }
    else if (followers >= 1000 && followers < 1000000) {
      String text = getResources().getString(R.string.thousands_followers, followers / 1000f);
      this.followers.setText(text);
    }
    else {
      String text = getResources().getString(R.string.millions_followers, followers / 1000000f);
      this.followers.setText(text);
    }
  }

  private void setArtistImage(String imageURL) {
    artistImage.setImageUrl(imageURL, imageLoader);
    artistImage.setDefaultImageResId(R.mipmap.default_user);
    artistImage.setErrorImageResId(R.mipmap.default_user);
  }

  private void populateAlbums() {
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    albumsAdapter = new AlbumsAdapter(albums, this);
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(albumsAdapter);
    recyclerView.addOnScrollListener(new LoadMoreScrollListener((LinearLayoutManager) layoutManager) {
      @Override
      public void onLoadMore(int currentPage) {
        if (nextUrl != null) {
          retrieveAlbums(nextUrl);
        }
      }
    });
    recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
            recyclerView, new ClickListener() {
      @Override
      public void onClick(View view, int position) {
        Album album = albums.get(position);
        openExternalAlbumLink(album);
      }

      @Override
      public void onLongClick(View view, int position) {

      }
    }));

    retrieveAlbums();
  }

  private void openExternalAlbumLink(Album album) {
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(album.getExternalUrl()));
    startActivity(browserIntent);
  }

  private void retrieveAlbums() {
    String url = HttpHelper.buildUrl(null, "artists", artist.getId(), "albums");
    retrieveAlbums(url);
  }

  private void retrieveAlbums(String url) {
    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null,
            successResponse, errorResponse);
    AppController.getInstance().addToRequestQueue(jsonObjReq);
  }

  private void updateNextUrl(JSONObject response) {
    try {
      if (!response.isNull("next")) nextUrl = response.getString("next");
      else nextUrl = null;
    }
    catch (JSONException e) {
      nextUrl = null;
    }
  }

  private void parseAlbums(JSONObject response) {
    try {
      updateNextUrl(response);
      JSONArray items = response.getJSONArray("items");

      if (items.length() == 0) {
        throw new EmptyResponseException(getResources().getString(R.string.no_results));
      }

      for (int i = 0; i < items.length(); ++i) {
        Album album = new Album();
        JSONObject albumJson = items.getJSONObject(i);
        album.setId(albumJson.getString("id"));
        album.setHref(albumJson.getString("href"));
        album.setName(albumJson.getString("name"));
        album.setType(albumJson.getString("type"));
        album.setUri(albumJson.getString("uri"));
        album.setJSONImages(albumJson.getJSONArray("images"));
        album.setJSONCountries(albumJson.getJSONArray("available_markets"));
        album.setExternalUrl(albumJson.getJSONObject("external_urls").getString("spotify"));
        albums.add(album);
      }
      albumsAdapter.notifyDataSetChanged();
    } catch (JSONException je) {
      je.printStackTrace();
      FeedbackHelper.showSnack(coordinatorLayout, getResources().getString(R.string.generic_error));
    }
    catch (EmptyResponseException er) {
      er.printStackTrace();
      FeedbackHelper.showSnack(coordinatorLayout, er.getMessage());
    }
  }

  Response.Listener<JSONObject> successResponse = new Response.Listener<JSONObject>() {
    @Override
    public void onResponse(JSONObject response) {
      parseAlbums(response);
    }
  };

  Response.ErrorListener errorResponse = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      FeedbackHelper.showSnack(coordinatorLayout,
              getResources().getString(R.string.could_not_process_request));
    }
  };

  public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
  }

  public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector gestureDetector;
    private ArtistActivity.ClickListener clickListener;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ArtistActivity.ClickListener clickListener) {
      this.clickListener = clickListener;
      gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
          return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
          View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
          if (child != null && clickListener != null) {
            clickListener.onLongClick(child, recyclerView.getChildPosition(child));
          }
        }
      });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

      View child = rv.findChildViewUnder(e.getX(), e.getY());
      if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
        clickListener.onClick(child, rv.getChildPosition(child));
      }
      return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
