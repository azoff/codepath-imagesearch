package com.codepath.imagesearch.activity;

import android.app.Activity;
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

	EditText etQuery;
	GridView gvResults;
	SearchAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvResults = (GridView) findViewById(R.id.gvResults);
		adapter = new SearchAdapter(this, new ArrayList<GoogleImageResult>());
		gvResults.setAdapter(adapter);
	}

	public GoogleImageSearchParams serializeForm() {
		Editable text = etQuery.getText();
		String query = text == null ? "" : text.toString();
		GoogleImageSearchParams params = new GoogleImageSearchParams(query);
		return params;
	}

	public void search(View v) {
		adapter.clear();
		SearchCallback callback = new SearchCallback();
		for (int i=0; i<=20; i+=4) {
			GoogleImageSearchRequest searchRequest = new GoogleImageSearchRequest(callback);
			GoogleImageSearchParams params = serializeForm();
			params.add("start", String.valueOf(i));
			searchRequest.execute(params);
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

		public ImageItemCallback(View imageItem) {
			button = (ImageView) imageItem.findViewById(R.id.ivThumb);
		}

		@Override
		public void onDrawable(Drawable d) {
			button.setImageDrawable(d);
		}

	}

	class SearchCallback extends GoogleImageSearchRequest.Callback {

		@Override
		public void onResult(GoogleImageResult result) {
			adapter.add(result);
		}


		@Override
		public void onError(Exception e) {
			Log.w("Google Search Exception", e);
		}

	}

}
