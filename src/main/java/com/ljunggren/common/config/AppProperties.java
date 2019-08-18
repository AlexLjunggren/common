package com.ljunggren.common.config;

import java.util.Properties;

public class AppProperties {

	private static Properties properties;

	private static final String EMAIL_USERNAME = "email.username";
	private static final String EMAIL_PASSWORD = "email.password";
	
	static {
		properties = System.getProperties();
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static String getEmailUsername() {
		return properties.getProperty(EMAIL_USERNAME);
	}
	public static String getEmailPassword() {
		return properties.getProperty(EMAIL_PASSWORD);
	}
	
}
