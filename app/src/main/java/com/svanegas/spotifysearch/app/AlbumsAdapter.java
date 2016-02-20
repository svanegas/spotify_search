package com.svanegas.spotifysearch.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.svanegas.spotifysearch.R;
import com.svanegas.spotifysearch.models.Album;
import com.svanegas.spotifysearch.models.Country;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

  private List<Album> albums;
  private ImageLoader imageLoader;
  private static final int MAX_COUNTRIES = 5;
  private Context context;

  public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView name, availableCountries;
    public LinearLayout availableCountriesLayout;
    public NetworkImageView image;

    public ViewHolder(View view) {
      super(view);
      name = (TextView) view.findViewById(R.id.name);
      availableCountries = (TextView) view.findViewById(R.id.available_countries);
      availableCountriesLayout = (LinearLayout) view.findViewById(R.id.available_countries_layout);
      image = (NetworkImageView) view.findViewById(R.id.album_image);
    }
  }

  public AlbumsAdapter(List<Album> albums, Context context) {
    this.albums = albums;
    this.context = context;
    imageLoader = AppController.getInstance().getImageLoader();
  }

  private void setAlbumImage(NetworkImageView view, String imageURL) {
    if (imageLoader == null) imageLoader = AppController.getInstance().getImageLoader();
    view.setImageUrl(imageURL, imageLoader);
    view.setDefaultImageResId(R.mipmap.default_album);
    view.setErrorImageResId(R.mipmap.default_album);
  }

  private void renderAvailableCountries(ViewHolder holder, Album album) {
    List <Country> countries = album.getAvailableCountries();
    String text;
    if (countries.isEmpty()) {
      text = context.getResources().getString(R.string.no_available_countries);
      holder.availableCountries.setText(text);
      holder.availableCountries.setVisibility(View.VISIBLE);
      holder.availableCountriesLayout.setVisibility(View.GONE);
    }
    else {
      if (countries.size() < MAX_COUNTRIES) {
        holder.availableCountriesLayout.removeAllViews();
        for (Country country : countries) {
          ImageView imageView = new ImageView(context);
          imageView.setPadding(5, 0, 5, 0);
          String name = country.getCode().toLowerCase();
          int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
          imageView.setImageResource(id);
          holder.availableCountriesLayout.addView(imageView);
        }
        holder.availableCountries.setVisibility(View.GONE);
        holder.availableCountriesLayout.setVisibility(View.VISIBLE);
      }
      else {
        text = context.getResources().getString(R.string.x_countries, countries.size());
        holder.availableCountries.setText(text);
        holder.availableCountries.setVisibility(View.VISIBLE);
        holder.availableCountriesLayout.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.album_list_item, parent, false);
    return new ViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    Album album = albums.get(position);
    holder.name.setText(album.getName());
    setAlbumImage(holder.image, album.getMediumImageURL());
    renderAvailableCountries(holder, album);
  }

  @Override
  public int getItemCount() {
    return albums.size();
  }
}
