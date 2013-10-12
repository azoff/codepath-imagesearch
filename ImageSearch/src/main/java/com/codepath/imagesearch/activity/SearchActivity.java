package com.codepath.imagesearch.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import java.util.List;

public class SearchActivity extends Activity {

	final int PAGE_SIZE = 4;

	int offset;
	EditText etQuery;
	GridView gvResults;
	SearchAdapter adapter;
	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		adapter = new SearchAdapter(this, new ArrayList<GoogleImageResult>());
		gvResults.setAdapter(adapter);
		gvResults.setOnScrollListener(new InfiniteScrollListener());
		progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
		progress.setMessage(getString(R.string.loading_image));
	}

	public boolean lastElementVisible(AdapterView v, int elementsBack) {
		int lastElementPosition = v.getLastVisiblePosition();
		return lastElementPosition < 0 || lastElementPosition == v.getAdapter().getCount() - elementsBack;
	}

	public GoogleImageSearchParams serializeForm() {
		Editable text = etQuery.getText();
		String query = text == null ? "" : text.toString();
		GoogleImageSearchParams params = new GoogleImageSearchParams(query);
		return params;
	}

	public void search(View v) {
		offset = 0;
		adapter.clear();
		if (!progress.isShowing()) progress.show();
		infiniteScroll(gvResults);
	}

	private void infiniteScroll(AdapterView v) {
		SearchCallback callback = new SearchCallback();
		GoogleImageSearchRequest searchRequest = new GoogleImageSearchRequest(callback);
		GoogleImageSearchParams params = serializeForm();
		params.add("start", String.valueOf(offset));
		searchRequest.execute(params);
		offset += PAGE_SIZE;
	}

	class InfiniteScrollListener implements GridView.OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			infiniteScroll(view);
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		}

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
			ImageItemCallback callback = new ImageItemCallback(imageThumb);
			AsyncDrawableRequest request = new AsyncDrawableRequest(callback);
			request.execute(result.getThumbUrl());
			return imageThumb;
		}
	}

	class ImageItemCallback extends AsyncDrawableRequest.Callback {

		ImageView button;
		ProgressBar progress;

		public ImageItemCallback(View imageItem) {
			button = (ImageView) imageItem.findViewById(R.id.ivThumb);
			progress = (ProgressBar) imageItem.findViewById(R.id.pbProgress);
		}

		@Override
		public void onStart() {
			progress.setVisibility(View.VISIBLE);
		}

		@Override
		public void onDrawable(Drawable d) {
			progress.setVisibility(View.GONE);
			button.setImageDrawable(d);
			button.setVisibility(View.VISIBLE);
		}

	}

	class SearchCallback extends GoogleImageSearchRequest.Callback {

		@Override
		public void onResult(GoogleImageResult result) {
			if (progress.isShowing()) progress.hide();
			adapter.add(result);
		}

		@Override
		public void onComplete() {
			if (lastElementVisible(gvResults, 1))
				infiniteScroll(gvResults);
		}

		@Override
		public void onError(Exception e) {
			Log.w("Google Search Exception", e);
		}

	}

}
