package com.ljunggren.common.http;

public class Authorization {

	public enum AuthType {
		BASIC_AUTH,
		HEADER_AUTH,
		NTLM_AUTH,
	}
	
	private String username;
	private String password;
	private AuthType authType;
	private String domain = "";
	
	public Authorization(String username, String password, AuthType authType) {
		super();
		this.username = username;
		this.password = password;
		this.authType = authType;
	}
	
	public Authorization(String username, String password, String domain, AuthType authType) {
		super();
		this.username = username;
		this.password = password;
		this.domain = domain;
		this.authType = authType;
	}
	
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public AuthType getAuthType() {
		return authType;
	}
	public String getDomain() {
		return domain;
	}
	
}
