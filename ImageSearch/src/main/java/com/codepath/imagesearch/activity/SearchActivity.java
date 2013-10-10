package com.codepath.imagesearch.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.model.GoogleImageResult;
import com.codepath.imagesearch.net.GoogleImageSearchParams;
import com.codepath.imagesearch.net.GoogleImageSearchRequest;

import java.util.List;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}

	public void search(View v) {
		GoogleImageSearchParams params = new GoogleImageSearchParams("android");
		GoogleImageSearchRequest request = new GoogleImageSearchRequest(params, new SearchCallback());
		request.execute();
	}

	class SearchCallback implements GoogleImageSearchRequest.Callback {

		@Override
		public void onSuccess(List<GoogleImageResult> results) {
			Log.i("GOOGLE", results.toString());
		}

		@Override
		public void onError(Exception e) {
			Log.e("GOOGLE", e.getMessage(), e);
		}
	}

}
