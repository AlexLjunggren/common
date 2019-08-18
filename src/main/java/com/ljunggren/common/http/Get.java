package com.ljunggren.common.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

public class Get extends HttpBase {

	public static Response request(Request request) throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(request.getUrl());
		if (request.getUrlParameters() != null && !request.getUrlParameters().isEmpty()) {
			uriBuilder.setParameters(request.getUrlParameters());
		}
		HttpGet get = new HttpGet(uriBuilder.build());
		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		addCertificateBuilder(request, clientBuilder);

		return request(request, get, clientBuilder);
	}
	
}
