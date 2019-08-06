package com.ljunggren.common.http;

public class NtlmProxy {
	
	private String ip;
	private int port;
	
	public NtlmProxy(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}
	public int getPort() {
		return port;
	}

}
