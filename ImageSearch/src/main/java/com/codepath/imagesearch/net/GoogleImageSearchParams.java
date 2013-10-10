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
		builder.appendQueryParameter("q", query);
		builder.appendQueryParameter("v", version);
		if (extraParams != null)
			for (NameValuePair extraParam : extraParams)
				builder.appendQueryParameter(extraParam.getName(), extraParam.getValue());
	}

	public Uri buildUri() {
		return builder.build();
	}

	public HttpGet buildRequest() {
		return new HttpGet(buildUri().toString());
	}

	public GoogleImageSearchParams(String query, List<NameValuePair> extraParams) {
		this(query, extraParams, "1.0");
	}

	public GoogleImageSearchParams(String query) {
		this(query, null);
	}

}
