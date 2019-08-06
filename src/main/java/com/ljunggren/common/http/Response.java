package com.ljunggren.common.http;

import java.security.cert.Certificate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

@Data
@Builder
@AllArgsConstructor
public class Response {

	private int responseCode;
	private String redirectURL;
	private String page;
	private List<Cookie> cookies;
	private List<Certificate> certificates;
	private List<Header> headers;
	private long responseTime;
	
	public Response setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
		return this;
	}
	
}
