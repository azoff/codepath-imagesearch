package com.codepath.imagesearch.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Azoff on 10/9/13.
 */
public class GoogleImageResult {

	int width;
	int height;
	URL fullUrl;
	URL thumbUrl;
	String title;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}

	public URL getFullUrl() {
		return fullUrl;
	}

	public URL getThumbUrl() {
		return thumbUrl;
	}

	public static GoogleImageResult fromJsonObject(JSONObject obj)
			throws JSONException, MalformedURLException {
		GoogleImageResult result = new GoogleImageResult();
		result.width    = obj.getInt("width");
		result.height   = obj.getInt("height");
		result.title    = obj.getString("titleNoFormatting");
		result.fullUrl  = new URL(obj.getString("url"));
		result.thumbUrl = new URL(obj.getString("tbUrl"));
		return result;
	}

}
