package com.ljunggren.common.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigurationResourceLoader {

    private static Environment environment;
    private static Properties properties;
    private static String propFile;
    
    static {
	    Map<Environment, String> propertyfilemap = new HashMap<Environment, String>();
	    propertyfilemap.put(Environment.LOCAL, "/config/properties/config.properties_local");
	    propertyfilemap.put(Environment.PREPROD, "/config/properties/config.properties_preprod");
	    propertyfilemap.put(Environment.PROD, "/config/properties/config.properties_prod");
	    
	    String env = System.getProperty("app.environment");
	    environment = Environment.valueOfOrNull(env);
	    
	    if (environment == null) {
	        environment = Environment.LOCAL;
	    }
	    
	    propFile = propertyfilemap.get(environment);
	    if (propFile == null) {
	        propFile = propertyfilemap.get(Environment.LOCAL);
	    }
	
	    try {
	        InputStream is = ConfigurationResourceLoader.class.getResourceAsStream(propFile);
	        properties = new Properties();
	        properties.load(is);
	        is.close();
	    } 
	    catch (IOException e) {
	        throw new RuntimeException("Error loading configuration properties", e);
	    }

	    addAdditionalProperties(properties, "email.outlook.properties");
	}

    private static void addAdditionalProperties(Properties properties, String additionalKey) {
    	String additionalPropFile = properties.getProperty(additionalKey);
        if (Files.exists(Paths.get(additionalPropFile))) {
            try {
                InputStream is = new FileInputStream(additionalPropFile);
                Properties additionalProperties = new Properties();
				additionalProperties.load(is);
	            is.close();
	            properties.putAll(additionalProperties);
            }
		    catch (IOException e) {
		    	throw new RuntimeException("Error loading additional properties", e);
		    }
        }
    }
	
    public static Properties getConfigurationProperties() {
        return properties;
    }
    
    public static Environment getEnvironment() {
        return environment;
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
