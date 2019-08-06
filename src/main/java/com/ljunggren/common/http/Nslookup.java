package com.ljunggren.common.http;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Nslookup { 

	public String resolveToIp(String hostname) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostname);
			if (inetAddress.isReachable(5000)) {
				byte[] address = inetAddress.getAddress();
				List<Integer> ipNumbers = new ArrayList<Integer>();
				for (int i = 0; i < address.length; i++) {
					int unsignedByte = address[i] < 0 ? address[i] + 256 : address[i];
					ipNumbers.add(unsignedByte);
				}
				return ipNumbers.stream().map(Object::toString).collect(Collectors.joining(".")).toString();
			}
		} 
		catch (Exception e) {
			// do nothing, hostname does not resolve to ip
		}
		return null;
	}
	
}
