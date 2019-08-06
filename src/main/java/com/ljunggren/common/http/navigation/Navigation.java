package com.ljunggren.common.http.navigation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import com.ljunggren.common.http.Authorization;
import com.ljunggren.common.http.Get;
import com.ljunggren.common.http.NtlmProxy;
import com.ljunggren.common.http.Post;
import com.ljunggren.common.http.Request;
import com.ljunggren.common.http.Response;

public class Navigation {
	
	{
		url = new String();
		redirect = true;
		acceptSelfSignedCertificates = true;
		cookies = new ArrayList<Cookie>();
		urlParameters = new ArrayList<NameValuePair>();
		connectionTimeout = 10000;
		socketTimeout = 60000;
	}

	private Response response;
	private String url;
	private List<Cookie> cookies;
	private boolean redirect;
	private boolean acceptSelfSignedCertificates;
	private NtlmProxy ntlmProxy;
	private Authorization authorization;
	private List<NameValuePair> urlParameters;
	private RequestType requestType;
	private Integer connectionTimeout;
	private Integer socketTimeout;
	private String payload;
	
	public Navigation() {}
	
	public Navigation httpGet(String url) {
		this.url = url;
		this.requestType = RequestType.GET;
		return this;
	}
	
	public Navigation httpPost(String url) {
		this.url = url;
		this.requestType = RequestType.POST;
		return this;
	}
	
	public Navigation cookies(List<Cookie> cookies) {
		this.cookies = cookies;
		return this;
	}
	
	public Navigation disableRedirect() {
		this.redirect = false;
		return this;
	}
	
	public Navigation disableAcceptSelfSignedCertificates() {
		this.acceptSelfSignedCertificates = false;
		return this;
	}
	
	public Navigation addParameter(String name, String value) {
		this.urlParameters.add(new BasicNameValuePair(name, value));
		return this;
	}
	
	public Navigation authorization(Authorization authorization) {
		this.authorization = authorization;
		return this;
	}
	
	public Navigation connectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}
	
	public Navigation socketTimeout(Integer socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}
	
	public Navigation ntlmProxy(NtlmProxy ntlmProxy) {
		this.ntlmProxy = ntlmProxy;
		return this;
	}
	
	public Navigation payload(String payload) {
		this.payload = payload;
		return this;
	}
	
	public void request() {
		Request request = new Request(url)
			.setUrlParameters(urlParameters)
			.setNtlmProxy(ntlmProxy)
			.setAuthorization(authorization)
			.redirect(redirect)
			.acceptSelfSignedCertificates(acceptSelfSignedCertificates)
			.setCookies(cookies)
			.setConnectionTimeout(connectionTimeout)
			.setSocketTimeout(socketTimeout)
			.setPayload(payload);
		try {
			switch(requestType) {
			case GET:
				response = Get.request(request);
				break;
			case POST:
				response = Post.request(request);
				break;
			}
		} 
		catch (Exception e) {
			String exception = ExceptionUtils.getMessage(e);
			response = Response.builder()
					.responseCode(000)
					.page(exception)
					.cookies(cookies)
					.build();
		}
	}
	
	public List<Cookie> getCookies() {
		return this.cookies;
	}

	public Response getResponse() {
		return response;
	}
}
