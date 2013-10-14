package com.codepath.imagesearch.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azoff on 10/9/13.
 */
public class AsyncPreviewRequest {

	Task task;
	Callback callback;
	List<Exception> exs;

	public static abstract class Callback {
		public void onStart() { }
		public void onComplete() { }
		public void onError(Exception e) { }
		public void onDrawable(Drawable d) { }
		public void onProgress(Integer p) { }
	}

	private class Task extends AsyncTask<String, Integer, List<Drawable>> {

		private static final int BUFFER_CHUNK_SIZE = 8192; // 8K

		@Override
		protected List<Drawable> doInBackground(String... urls) {

			int totalLength = 0, currentLength = 0;
			List<Drawable> drawables = new ArrayList<Drawable>();
			List<URLConnection> connections = new ArrayList<URLConnection>();

			for (String urlString : urls) {

				try  {

					URL url = new URL(urlString);
					URLConnection connection = url.openConnection();
					connection.connect();
					connections.add(connection);
					totalLength += connection.getContentLength();

				} catch (IOException ex) {

					exs.add(ex);

				}

			}

			for (URLConnection connection : connections) {

				try  {

					int chunkLength;
					byte buffer[] = new byte[1024];
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					InputStream input = connection.getInputStream();

					while ((chunkLength = input.read(buffer)) >= 0) {
						currentLength += chunkLength;
						publishProgress(currentLength*100/totalLength);
						output.write(buffer, 0, chunkLength);
					}

					InputStream drawableStream = new ByteArrayInputStream(output.toByteArray());
					Drawable drawable = Drawable.createFromStream(drawableStream, "src");
					drawables.add(drawable);

					output.flush();
					input.close();
					output.close();

				} catch (IOException ex) {

					exs.add(ex);

				}

			}

			return drawables;

		}

		@Override
		protected void onPreExecute() {
			callback.onStart();
		}

		@Override
		protected void onPostExecute(List<Drawable> drawables) {
			for (Drawable drawable : drawables)
				callback.onDrawable(drawable);
			for (Exception ex : exs)
				callback.onError(ex);
			callback.onComplete();
		}

		@Override
		protected void onProgressUpdate(Integer... progresses) {
			for (Integer progress : progresses)
				callback.onProgress(progress);
		}

		@Override
		protected void onCancelled(List<Drawable> drawables) {
			onPostExecute(drawables);
		}

		@Override
		protected void onCancelled() {
			onCancelled(new ArrayList<Drawable>());
		}

	}

	public AsyncPreviewRequest(Callback callback) {
		this.callback = callback;
		exs = new ArrayList<Exception>();
	}

	public void execute(String url) {
		task = new Task();
		task.execute(url);
	}

}