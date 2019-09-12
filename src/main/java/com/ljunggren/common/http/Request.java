package com.ljunggren.common.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;

public class Request {
	
	{
		url = new String();
		urlParameters = new ArrayList<NameValuePair>();
		cookies = new ArrayList<Cookie>();
		connectionTimeout = 60000;
	}

	private String url;
	private NtlmProxy ntlmProxy;
	private Authorization authorization;
	private boolean redirect;
	private boolean acceptSelfSignedCertificates;
	private List<NameValuePair> urlParameters;
	private List<Header> headers;
	private List<Cookie> cookies;
	private Integer connectionTimeout;
	private Integer sockectTimeout;
	private String payload;
	
	public Request(String url) {
		this.url = url;
	}
	
	public Request setNtlmProxy(NtlmProxy ntlmProxy) {
		this.ntlmProxy = ntlmProxy;
		return this;
	}
	
	public Request setAuthorization(Authorization authorization) {
		this.authorization = authorization;
		return this;
	}
	
	public Request redirect(boolean redirect) {
		this.redirect = redirect;
		return this;
	}
	
	public Request acceptSelfSignedCertificates(boolean allow) {
		this.acceptSelfSignedCertificates = allow;
		return this;
	}
	
	public Request setUrlParameters(List<NameValuePair> urlParameters) {
		this.urlParameters = urlParameters;
		return this;
	}
	
	public Request setHeaders(List<Header> headers) {
		this.headers = headers;
		return this;
	}
	
	public Request setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
		return this;
	}
	
	public Request setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}
	
	public Request setSocketTimeout(Integer socketTimeout) {
		this.sockectTimeout = socketTimeout;
		return this;
	}
	
	public Request setPayload(String payload) {
		this.payload = payload;
		return this;
	}
	
	public String getUrl() {
		return url;
	}

	public NtlmProxy getNtlmProxy() {
		return ntlmProxy;
	}
	
	public Authorization getAuthorization() {
		return authorization;
	}

	public boolean isRedirect() {
		return redirect;
	}
	
	public boolean isAcceptSelfSignedCertificates() {
		return this.acceptSelfSignedCertificates;
	}
	
	public List<NameValuePair> getUrlParameters() {
		return urlParameters;
	}

	public List<Header> getHeaders() {
		return headers;
	}
	
	public List<Cookie> getCookies() {
		return cookies;
	}
	
	public Integer getConnectionTimeout() {
		return this.connectionTimeout;
	}
	
	public Integer getSockectTimeout() {
		return this.sockectTimeout;
	}
	
	public String getPayload() {
		return this.payload;
	}
	
}
