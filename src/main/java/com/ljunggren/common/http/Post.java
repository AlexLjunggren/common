package com.ljunggren.common.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class Post extends HttpBase {

	public static Response request(Request request) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpPost post = new HttpPost(request.getUrl());
		post.setEntity(new UrlEncodedFormEntity(request.getUrlParameters()));
		HttpClientBuilder clientBuilder = HttpClientBuilder.create().useSystemProperties();

		if (request.getPayload() != null && !request.getPayload().isEmpty()) {
			StringEntity entity = new StringEntity(request.getPayload());
			post.setEntity(entity);
		}
		return request(request, post, clientBuilder);
	}
	
}
