package com.codepath.imagesearch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.model.GoogleImageResult;
import com.codepath.imagesearch.net.AsyncDrawableRequest;
import com.codepath.imagesearch.net.GoogleImageSearchParams;
import com.codepath.imagesearch.net.GoogleImageSearchRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity {

	final int PAGE_SIZE = 8;

	int offset;
	Context context;
	EditText etQuery;
	GridView gvResults;
	SearchAdapter adapter;
	ProgressDialog progress;
	Map<String, Drawable> drawableCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		context = this;
		etQuery = (EditText) findViewById(R.id.etQuery);
		adapter = new SearchAdapter(context, new ArrayList<GoogleImageResult>());
		drawableCache = new HashMap<String, Drawable>();
		gvResults = (GridView) findViewById(R.id.gvResults);
		gvResults.setOnItemClickListener(new ImageItemClickListener());
		gvResults.setAdapter(adapter);
		progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		progress.setMessage(getString(R.string.loading_image));
	}

	public boolean hasRoom(AdapterView v, int elementsBack) {
		int lastVisible = v.getLastVisiblePosition();
		int lastElement = v.getAdapter().getCount() - elementsBack;
		return lastVisible > -1 && lastVisible >= lastElement;
	}

	public GoogleImageSearchParams serializeForm(int offset, int pageSize) {
		Editable text = etQuery.getText();
		String query = text == null ? "" : text.toString();
		return new GoogleImageSearchParams(query, pageSize, offset);
	}

	public void startSearch(View v) {
		offset = 0;
		adapter.clear();
		drawableCache.clear();
		search();
	}

	private void searchIfRoom(AbsListView v) {
		if (hasRoom(v, 3))
			search();
	}

	private void search() {
		search(null);
	}

	private void search(AdapterView v) {
		progress.show();
		gvResults.setOnScrollListener(null);
		SearchCallback callback = new SearchCallback(PAGE_SIZE);
		GoogleImageSearchRequest searchRequest = new GoogleImageSearchRequest(callback);
		GoogleImageSearchParams params = serializeForm(offset, PAGE_SIZE);
		offset += PAGE_SIZE;
		searchRequest.execute(params);
	}

	private void viewImage(GoogleImageResult result) {
		Intent intent = new Intent(context, PreviewActivity.class);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.putExtra("title", result.getTitle());
		intent.putExtra("src", result.getFullUrl());
		startActivity(intent);
	}

	class ImageItemClickListener implements GridView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			viewImage(adapter.getItem(position));
		}

	}

	class InfiniteScrollListener implements GridView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			searchIfRoom(view);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

	}

	class SearchAdapter extends ArrayAdapter<GoogleImageResult> {

		public SearchAdapter(Context context, List<GoogleImageResult> results) {
			super(context, 0, results);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = LayoutInflater.from(getContext());
			View imageThumb = inflator.inflate(R.layout.image_item, null);
			GoogleImageResult result = getItem(position);
			String thumbUrl = result.getThumbUrl();
			ImageItemCallback callback = new ImageItemCallback(imageThumb, thumbUrl);
			if (drawableCache.containsKey(thumbUrl)) {
				callback.onDrawable(drawableCache.get(thumbUrl));
			} else {
				AsyncDrawableRequest request = new AsyncDrawableRequest(callback);
				request.execute(result.getThumbUrl());
			}
			return imageThumb;
		}

	}

	class ImageItemCallback extends AsyncDrawableRequest.Callback {

		String cacheKey;
		ImageView button;
		ProgressBar progress;

		public ImageItemCallback(View imageItem, String thumbUrl) {
			button = (ImageView) imageItem.findViewById(R.id.ivThumb);
			progress = (ProgressBar) imageItem.findViewById(R.id.pbProgress);
			cacheKey = thumbUrl;
		}

		@Override
		public void onStart() {
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		public void onDrawable(Drawable d) {
			if (!drawableCache.containsKey(cacheKey))
				drawableCache.put(cacheKey, d);
			progress.setVisibility(View.GONE);
			button.setImageDrawable(d);
			button.setVisibility(View.VISIBLE);
		}

	}

	class SearchCallback extends GoogleImageSearchRequest.Callback {

		int waitUntilZero;

		SearchCallback(int pageSize) {
			waitUntilZero = pageSize;
		}

		@Override
		public void onResult(GoogleImageResult result) {
			progress.hide();
			adapter.add(result);
			gvResults.setOnScrollListener(new InfiniteScrollListener());
			if (--waitUntilZero <= 0)
				searchIfRoom(gvResults);
		}

		@Override
		public void onError(Exception e) {
			Log.w("Google Search Exception", e);
			progress.hide();
			gvResults.setOnScrollListener(null);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}

	}

}
