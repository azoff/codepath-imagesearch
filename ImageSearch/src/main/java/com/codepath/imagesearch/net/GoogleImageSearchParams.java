package com.codepath.imagesearch.net;


import android.net.Uri;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.util.List;

/**
 * Created by Azoff on 10/9/13.
 */
public class GoogleImageSearchParams {

	Uri.Builder builder;

	public GoogleImageSearchParams(String query, int pageSize, int offset) {
		builder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images").buildUpon();
		add("q", query);
		add("v", "1.0");
		add("rsz", String.valueOf(pageSize));
		add("start", String.valueOf(offset));
	}

	public Uri buildUri() {
		return builder.build();
	}

	public HttpGet buildRequest() {
		return new HttpGet(buildUri().toString());
	}

	public void add(String key, String value) {
		builder.appendQueryParameter(key, value);
	}

}
