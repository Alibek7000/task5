package com.epam.kozhanbergenov.shop.util;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static final Logger log = Logger.getLogger(ConfigurationManager.class);
    public Properties properties;

    public ConfigurationManager(String fileName) {
        properties = new Properties();
        InputStream inputStream = ConfigurationManager.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}
