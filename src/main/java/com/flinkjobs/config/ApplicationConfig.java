package com.flinkjobs.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to load configuration properties from
 * application.properties and profile-specific properties based on the active
 * profile defined in the spring.profiles.active property.
 */
public class ApplicationConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

	private static final Properties properties = new Properties();

	static {
		try {
			// Load default application.properties from the classpath
			InputStream inputStream = ApplicationConfig.class.getClassLoader()
					.getResourceAsStream("application.properties");
			if (inputStream != null) {
				properties.load(inputStream);
				inputStream.close();
				logger.info("Loaded default configuration from application.properties");
			} else {
				logger.warn("application.properties not found in classpath");
			}

			// Get the active profile from the properties
			String activeProfile = properties.getProperty("spring.profiles.active", "default");
			System.out.println("Active profile: " + activeProfile);

			// Load profile-specific properties (e.g., application-local.properties)
			if (!"default".equals(activeProfile)) {
				String profileFileName = "application-" + activeProfile + ".properties";
				InputStream profileInputStream = ApplicationConfig.class.getClassLoader()
						.getResourceAsStream(profileFileName);
				if (profileInputStream != null) {
					properties.load(profileInputStream);
					profileInputStream.close();
					logger.info("Loaded profile-specific configuration from: " + profileFileName);
				} else {
					logger.warn(profileFileName + " not found in classpath");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load application properties", e);
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
}
