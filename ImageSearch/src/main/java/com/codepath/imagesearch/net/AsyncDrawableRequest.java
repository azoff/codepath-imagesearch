package com.codepath.imagesearch.net;

import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Azoff on 10/9/13.
 */
public class AsyncDrawableRequest {

	Task task;
	Callback callback;
	HashMap<String, Drawable> cache;

	public static abstract class Callback {
		public void onStart() { }
		public void onComplete() { }
		public void onError(Exception e) { }
		public void onDrawable(Drawable d) { }
	}

	private class Task extends AsyncTask<String, Drawable, Exception> {

		@Override
		protected Exception doInBackground(String... urls) {

			AndroidHttpClient client = AndroidHttpClient.newInstance("AsyncDrawableAgent");

			for (String url : urls) {

				if (cache.containsKey(url)) {
					publishProgress(cache.get(url));
					continue;
				}

				HttpRequestBase request = new HttpGet(url);

				try {

					HttpResponse response = client.execute(request);
					HttpEntity responseBody = response.getEntity();

					if (isCancelled())
						return endRequest(client, null);

					InputStream imageStream = responseBody.getContent();
					Drawable drawable = Drawable.createFromStream(imageStream, "src");
					cache.put(url, drawable);
					publishProgress(drawable);

				} catch (IOException e) {

					return endRequest(client, e);

				}

			}

			return endRequest(client, null);

		}

		@Override
		protected void onPreExecute() {
			callback.onStart();
			if (cache == null)
				cache = new HashMap<String, Drawable>();
		}

		@Override
		protected void onPostExecute(Exception e) {
			if (e != null) callback.onError(e);
			else           callback.onComplete();
		}

		@Override
		protected void onProgressUpdate(Drawable... drawables) {
			for (Drawable drawable : drawables)
				callback.onDrawable(drawable);
		}

		@Override
		protected void onCancelled(Exception e) {
			onPostExecute(e);
		}

		@Override
		protected void onCancelled() {
			onCancelled(null);
		}

	}

	private Exception endRequest(AndroidHttpClient client, Exception e) {
		client.close();
		return e;
	}

	public AsyncDrawableRequest(Callback callback) {
		this.callback = callback;
	}

	public void execute(String url) {
		task = new Task();
		task.execute(url);
	}

}