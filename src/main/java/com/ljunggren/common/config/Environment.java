package com.ljunggren.common.config;

public enum Environment {

	LOCAL,
	PREPROD,
	PROD;
	
	public static Environment valueOfOrNull(String s) {
		Environment environment = null;
		try {
			environment = Environment.valueOf(s);
		}
		catch (Exception e) {}
		return environment;
	}
	
}
