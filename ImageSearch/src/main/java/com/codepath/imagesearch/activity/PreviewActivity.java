package com.codepath.imagesearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.net.AsyncPreviewRequest;

import java.io.FileNotFoundException;

public class PreviewActivity extends Activity {

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_SRC = "src";

	ImageView ivFull;
	ProgressBar pbProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		ivFull = (ImageView) findViewById(R.id.ivFull);
		pbProgress = (ProgressBar) findViewById(R.id.pbProgress);

		Intent requestor = getIntent();
		String fullUrlString = requestor.getStringExtra(EXTRA_SRC);
		PreviewCallback callback = new PreviewCallback(this);

		Log.d("HERE", fullUrlString);

		if (fullUrlString != null) {
			String title = requestor.getStringExtra(EXTRA_TITLE);
			if (title != null) {
				setTitle(title);
				ivFull.setContentDescription(title);
			}
			AsyncPreviewRequest request = new AsyncPreviewRequest(callback);
			request.execute(fullUrlString);
		} else {
			callback.onError(new IllegalArgumentException("Invalid Image URL"));
		}


	}

	class PreviewCallback extends AsyncPreviewRequest.Callback {

		Activity context;

		PreviewCallback(Activity parent) {
			context = parent;
		}

		@Override
		public void onStart() {
			pbProgress.setProgress(0);
		}

		@Override
		public void onComplete() {
			pbProgress.setProgress(100);
			pbProgress.setVisibility(View.GONE);
			ivFull.setVisibility(View.VISIBLE);
		}

		@Override
		public void onError(Exception e) {
			Log.w("Image Preview Exception", e);
			String message;
			if (e instanceof FileNotFoundException)
				message = "File Not Found";
			else
				message = e.getMessage();
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			context.finish();
		}

		@Override
		public void onDrawable(Drawable d) {
			ivFull.setImageDrawable(d);
		}

		@Override
		public void onProgress(Integer p) {
			pbProgress.setProgress(p);
		}
	}

}
