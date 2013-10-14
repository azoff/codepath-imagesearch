package com.codepath.imagesearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.codepath.imagesearch.R;
import com.codepath.imagesearch.net.AsyncPreviewRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewActivity extends Activity {

	public static final String EXTRA_TITLE = "title";
	public static final String EXTRA_SRC = "src";

	ImageView ivFull;
	ProgressBar pbProgress;

	File temp;
	String url, title;
	Drawable image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		ivFull = (ImageView) findViewById(R.id.ivFull);
		pbProgress = (ProgressBar) findViewById(R.id.pbProgress);

		Intent requestor = getIntent();
		PreviewCallback callback = new PreviewCallback(this);

		title = requestor.getStringExtra(EXTRA_TITLE);
		if (title != null) {
			setTitle(title);
			ivFull.setContentDescription(title);
		}

		url = requestor.getStringExtra(EXTRA_SRC);
		if (url != null) {
			AsyncPreviewRequest request = new AsyncPreviewRequest(callback);
			request.execute(url);
		} else {
			callback.onError(new IllegalArgumentException("Invalid Image URL"));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_preview_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		temp.delete();
	}

	public void share(MenuItem item) {

		try {

			File cacheDir = new File(getCacheDir(), "images");

			if (cacheDir.exists() || cacheDir.mkdirs()) {

				temp = File.createTempFile("share", ".png", cacheDir);
				FileOutputStream output = new FileOutputStream(temp);
				Bitmap bitmap = ((BitmapDrawable)image).getBitmap();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
				output.flush();
				output.close();

				Uri stream = FileProvider.getUriForFile(this, "com.codepath.imagesearch.fileprovider", temp);

				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, stream);
				shareIntent.setType("image/png");
				startActivityForResult(Intent.createChooser(shareIntent, "Share image..."), 1);

			} else {

				throw new IOException("Unable to make temp dir");

			}

		} catch (IOException e) {

			Log.w("Sharing Exception", e);
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();

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
			image = d;
		}

		@Override
		public void onProgress(Integer p) {
			pbProgress.setProgress(p);
		}
	}

}
