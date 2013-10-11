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

	public GoogleImageSearchParams(String query, List<NameValuePair> extraParams, String version) {
		builder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images").buildUpon();
		add("q", query);
		add("v", version);
		if (extraParams != null)
			for (NameValuePair extraParam : extraParams)
				add(extraParam.getName(), extraParam.getValue());
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

	public GoogleImageSearchParams(String query, List<NameValuePair> extraParams) {
		this(query, extraParams, "1.0");
	}

	public GoogleImageSearchParams(String query) {
		this(query, null);
	}

}
