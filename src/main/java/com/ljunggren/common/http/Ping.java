package com.ljunggren.common.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ping {
	
	private String address;
	
	public Ping(String address) {
		this.address = address;
	}

	public boolean isReachable() throws UnknownHostException, IOException {
		InetAddress inetAaddress = InetAddress.getByName(address);
		if (inetAaddress.isReachable(5000)) {
			return true;
		}
		return false;
	}
}
