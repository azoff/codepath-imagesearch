package com.codepath.imagesearch.net;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import com.codepath.imagesearch.model.GoogleImageResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Azoff on 10/9/13.
 */
public class GoogleImageSearchRequest {

	Task task;
	Callback callback;

	public static abstract class Callback {
		public void onStart() { }
		public void onComplete() { }
		public void onError(Exception e) { }
		public void onResult(GoogleImageResult result) { }
	}

	private class Task extends AsyncTask<GoogleImageSearchParams, GoogleImageResult, Exception> {

		@Override
		protected Exception doInBackground(GoogleImageSearchParams... paramses) {

			AndroidHttpClient client = AndroidHttpClient.newInstance("GoogleImageSearchAgent");

			for (GoogleImageSearchParams params : paramses) {

				try {

					HttpGet request = params.buildRequest();
					HttpResponse response = client.execute(request);
					HttpEntity responseBody = response.getEntity();

					if (isCancelled())
						return endTask(client, null);

					String responseJson = EntityUtils.toString(responseBody);

					if (responseJson == null) {
						return endTask(client, new NoHttpResponseException("No response returned from Google"));
					}

					JSONObject responseObj = new JSONObject(responseJson);
					Integer statusCode = responseObj.getInt("responseStatus");

					if (statusCode < 200 || statusCode > 299) {
						String statusMessage = responseObj.getString("responseDetails");
						return endTask(client, new HttpResponseException(statusCode, statusMessage));
					}

					JSONObject responseData = responseObj.getJSONObject("responseData");
					JSONArray responseResults = responseData.getJSONArray("results");

					for (int i=0; i<responseResults.length(); i++) {

						JSONObject obj = responseResults.getJSONObject(i);
						GoogleImageResult result = GoogleImageResult.fromJsonObject(obj);
						publishProgress(result);

						if (isCancelled())
							return endTask(client, null);

					}

				} catch (IOException e) {

					return endTask(client, e);

				} catch (JSONException e) {

					return endTask(client, e);

				}

			}

			return endTask(client, null);

		}

		@Override
		protected void onPreExecute() {
			callback.onStart();
		}

		@Override
		protected void onPostExecute(Exception e) {
			if (e != null) callback.onError(e);
			else           callback.onComplete();
		}

		@Override
		protected void onProgressUpdate(GoogleImageResult... results) {
			for (GoogleImageResult result : results)
				callback.onResult(result);
		}

		@Override
		protected void onCancelled(Exception e) {
			onPostExecute(e);
		}

		@Override
		protected void onCancelled() {
			onCancelled(null);
		}

		private Exception endTask(AndroidHttpClient client, Exception e) {
			client.close();
			callback.onComplete();
			return e;
		}

	}

	public GoogleImageSearchRequest(Callback callback) {
		this.callback = callback;
	}

	public void execute(GoogleImageSearchParams params) {
		task = new Task();
		task.execute(params);
	}

	public void cancel() {
		if (task != null)
			task.cancel(true);
	}

}
