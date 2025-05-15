package com.flinkjobs.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.flinkjobs.config.ApplicationConfig;

public class Constant {

	public static final String BOOTSTRAP_SERVER = ApplicationConfig.getProperty("kafka.bootstrap-server");
	public static final String PRODUCER_TOPIC = ApplicationConfig.getProperty("kafka.producer-topic");
	public static final String CONSUMER_TOPIC = ApplicationConfig.getProperty("kafka.consumer-topic");
	public static final String GROUP_ID = ApplicationConfig.getProperty("kafka.consumer-group-id");
	public static final String DEFAULT_PARALLELISM = ApplicationConfig.getProperty("kafka.parallelism");
	public static final int CHECKPOINT_INTERVAL = Integer.parseInt(ApplicationConfig.getProperty("checkpoint.interval")); // 2 mins
	public static final String MONGODB_URI = ApplicationConfig.getProperty("mongodb.uri");
	public static final String MONGODB_DATABASE_SCHEMA = ApplicationConfig.getProperty("mongodb.database.schema");
	public static final String MONGODB_DATABASE_COLLECTION = ApplicationConfig.getProperty("mongodb.database.collection");
	public static final String MONGODB_USER = ApplicationConfig.getProperty("mongodb.user");
	public static final String MONGODB_PASSWORD = ApplicationConfig.getProperty("mongodb.password");
	public static final String STATE_DATA_PATH = ApplicationConfig.getProperty("state.data.path");

	// #Connection Pool Configuration
	public static final int CONPOOL_MIN_SIZE = Integer.parseInt(ApplicationConfig.getProperty("mongodb.connection.pool.min-size"));
	public static final int CONPOOL_MAX_SIZE = Integer.parseInt(ApplicationConfig.getProperty("mongodb.connection.pool.max-size"));
	public static final int CONPOOL_IDLE_TIME = Integer.parseInt(ApplicationConfig.getProperty("mongodb.connection.pool.max-connection-idle-time"));
	public static final int CONPOOL_LIFE_TIME = Integer.parseInt(ApplicationConfig.getProperty("mongodb.connection.pool.max-connection-life-time"));
	public static final int CONPOOL_TIMEOUT = Integer.parseInt(ApplicationConfig.getProperty("mongodb.socket.connect-timeout"));
	
	/**
	 * Get the current month's collection name.
	 */
	public static String getCurrentMonthCollectionName(String baseCollectionName) {
		LocalDate now = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM_yyyy");
		return baseCollectionName + "_" + now.format(formatter);
	}

}
