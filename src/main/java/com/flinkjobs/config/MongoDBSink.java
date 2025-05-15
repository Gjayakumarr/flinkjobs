package com.flinkjobs.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flinkjobs.packetStucture.ApiLog;
import com.flinkjobs.packetStucture.KafkaObject;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;

/**
 * MongoDBSink Custom Flink sink to write Kafka objects to MongoDB.
 */
public class MongoDBSink extends RichSinkFunction<ApiLog> {

	private static final long serialVersionUID = 1L;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(MongoDBSink.class);

	private transient MongoClient mongoClient;
	private transient MongoCollection<Document> collection;
	private final String connectionString;
	private final String databaseName;
	private final String collectionName;

	private static final int BATCH_SIZE = 50;
	private final List<Document> batchBuffer = new ArrayList<>();

	/**
	 * Constructor to initialize MongoDB connection parameters.
	 *
	 * @param connectionString the MongoDB connection string
	 * @param databaseName     the database name
	 * @param collectionName   the collection name
	 */
	public MongoDBSink(String connectionString, String databaseName, String collectionName) {
		this.connectionString = connectionString;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		// Initialize MongoDB client and collection in the open method (executed once
		// per task)
		mongoClient = MongoClients.create(connectionString);
		MongoDatabase database = mongoClient.getDatabase(databaseName);
		collection = database.getCollection(collectionName);
		logger.info("MongoDBSink open method triggered");
	}

	@Override
	public void invoke(ApiLog kafkaObject, Context context) throws Exception {
		try {
			// Convert KafkaObject to JSON string
			String jsonString = objectMapper.writeValueAsString(kafkaObject);
			// Parse JSON string into a MongoDB Document.
			Document document = Document.parse(jsonString);

			batchBuffer.add(document);
			// Ensure data is flushed on checkpoint (for fault tolerance)
			if (batchBuffer.size() >= BATCH_SIZE) {
				flush();
			}
//			logger.info("Successfully inserted document into MongoDB: {}", document.toJson()); // Keep it commented
		} catch (Exception e) {
			logger.error("Error inserting document into MongoDB: {}", e.getMessage(), e);
		}
	}

	private void flush() {
		if (!batchBuffer.isEmpty()) {
			try {
				collection.insertMany(new ArrayList<>(batchBuffer), new InsertManyOptions().ordered(false));
				logger.info("Inserted {} records into MongoDB.", batchBuffer.size());
			} catch (MongoBulkWriteException e) {
				logger.error("MongoDB bulk write error: {}", e.getMessage(), e);
			} catch (Exception e) {
				logger.error("Unexpected error during batch insert: {}", e.getMessage(), e);
			} finally {
				batchBuffer.clear();
			}
		}
	}

	@Override
	public void close() throws Exception {
		flush(); // Insert any remaining records
		// Close MongoDB client in the close method (executed once per task)
		if (mongoClient != null) {
			mongoClient.close();
//			logger.info("MongoDBSink close method triggered"); // Keep it commented
		}
		super.close();
	}
}

