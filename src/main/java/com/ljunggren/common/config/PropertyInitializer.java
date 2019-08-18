package com.ljunggren.common.config;

import java.util.Enumeration;
import java.util.Properties;

public class PropertyInitializer {

    public static void loadProperties(Properties properties) {
        properties.list(System.err);
        Enumeration<Object> keyEnum = properties.keys();
        while (keyEnum.hasMoreElements()) {
            String propertyName = (String) keyEnum.nextElement();
            String propertyValue = properties.getProperty(propertyName);
            System.setProperty(propertyName, propertyValue);
        }
    }

}
