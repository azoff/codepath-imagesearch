package com.codepath.imagesearch.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by Azoff on 10/9/13.
 */
public class GoogleImageResult {

	int width;
	int height;
	String title;
	String fullUrl;
	String thumbUrl;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public static GoogleImageResult fromJsonObject(JSONObject obj)
			throws JSONException, MalformedURLException {
		GoogleImageResult result = new GoogleImageResult();
		result.width    = obj.getInt("width");
		result.height   = obj.getInt("height");
		result.title    = obj.getString("titleNoFormatting");
		result.fullUrl  = obj.getString("url");
		result.thumbUrl = obj.getString("tbUrl");
		return result;
	}

}
