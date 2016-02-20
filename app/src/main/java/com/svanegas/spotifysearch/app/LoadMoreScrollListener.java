package com.svanegas.spotifysearch.app;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Taken from: https://gist.github.com/ssinss/e06f12ef66c51252563e
 */
public abstract class LoadMoreScrollListener extends RecyclerView.OnScrollListener {
  public static String TAG = LoadMoreScrollListener.class.getSimpleName();

  private int previousTotal = 0; // The total number of items in the dataset after the last load
  private boolean loading = true; // True if we are still waiting for the last set of data to load.
  private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
  int firstVisibleItem, visibleItemCount, totalItemCount;

  private int currentPage = 1;

  private LinearLayoutManager linearLayoutManager;

  public LoadMoreScrollListener(LinearLayoutManager linearLayoutManager) {
    this.linearLayoutManager = linearLayoutManager;
  }

  @Override
  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    visibleItemCount = recyclerView.getChildCount();
    totalItemCount = linearLayoutManager.getItemCount();
    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

    if (loading) {
      if (totalItemCount > previousTotal) {
        loading = false;
        previousTotal = totalItemCount;
      }
    }
    if (!loading && (totalItemCount - visibleItemCount)
            <= (firstVisibleItem + visibleThreshold)) {
      // End has been reached

      // Do something
      currentPage++;

      onLoadMore(currentPage);

      loading = true;
    }
  }

  public abstract void onLoadMore(int currentPage);
}