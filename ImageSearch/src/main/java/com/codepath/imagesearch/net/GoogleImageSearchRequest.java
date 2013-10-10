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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azoff on 10/9/13.
 */
public class GoogleImageSearchRequest {

	Callback callback;
	GoogleImageSearchParams params;

	public interface Callback {
		public void onSuccess(List<GoogleImageResult> results);
		public void onError(Exception e);
	}

	private class Task extends AsyncTask<GoogleImageSearchParams, Integer, List<GoogleImageResult>> {

		@Override
		protected List<GoogleImageResult> doInBackground(GoogleImageSearchParams... params) {

			AndroidHttpClient client = AndroidHttpClient.newInstance("GoogleImageSearchClient");

			try {
				HttpGet request       = params[0].buildRequest();
				HttpResponse response = client.execute(request);
				HttpEntity responseBody = response.getEntity();
				String responseJson = EntityUtils.toString(responseBody);
				client.close();
				if (responseJson == null) {
					callback.onError(new NoHttpResponseException("No response returned from Google"));
				} else {
					JSONObject responseObj = new JSONObject(responseJson);
					Integer statusCode = responseObj.optInt("responseStatus");
					if (statusCode < 200 || statusCode > 299) {
						String statusMessage = responseObj.optString("responseDetails");
						callback.onError(new HttpResponseException(statusCode, statusMessage));
					} else {
						JSONObject responseData = responseObj.getJSONObject("responseData");
						JSONArray responseResults = responseData.getJSONArray("results");
						ArrayList<GoogleImageResult> results = new ArrayList<GoogleImageResult>();
						for (int i=0; i<responseResults.length(); i++) {
							JSONObject obj = responseResults.optJSONObject(i);
							results.add(GoogleImageResult.fromJsonObject(obj));
						}
						callback.onSuccess(results);
						return results;
					}
				}
			} catch (IOException e) {
				callback.onError(e);
			} catch (JSONException e) {
				callback.onError(e);
			}

			return null;

		}

	}

	public GoogleImageSearchRequest(GoogleImageSearchParams params, Callback callback) {
		this.callback = callback;
		this.params = params;
	}

	public void execute() {
		Task task = new Task();
		task.execute(params);
	}

}
